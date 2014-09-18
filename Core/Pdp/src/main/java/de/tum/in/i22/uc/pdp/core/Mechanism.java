package de.tum.in.i22.uc.pdp.core;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.Observable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.MoreObjects;

import de.tum.in.i22.uc.cm.datatypes.interfaces.ICondition;
import de.tum.in.i22.uc.cm.datatypes.interfaces.IEvent;
import de.tum.in.i22.uc.cm.datatypes.interfaces.IMechanism;
import de.tum.in.i22.uc.pdp.core.exceptions.InvalidMechanismException;
import de.tum.in.i22.uc.pdp.xsd.ExecuteAsyncActionType;
import de.tum.in.i22.uc.pdp.xsd.MechanismBaseType;

public abstract class Mechanism extends Observable implements Runnable, IMechanism {
	protected static Logger _logger = LoggerFactory.getLogger(Mechanism.class);

	public static final Object END_OF_TIMESTEP = new Object();

	/**
	 * The name of this {@link Mechanism}.
	 */
	private final String _name;

	/**
	 * A description of this {@link Mechanism}.
	 */
	private final String _description;
	private long _lastUpdate = 0;
	private long _timestepSize = 0;
	private long _timestep = 0;
	private final EventMatch _triggerEvent;
	private Condition _condition;
	protected AuthorizationAction _authorizationAction;
	private final List<ExecuteAction> _executeAsyncActions;
	private final PolicyDecisionPoint _pdp;
	private boolean _interrupted = false;

	private final Deque<Condition> _backupCondition;

	/**
	 * The name of the policy to which this {@link Mechanism} belongs.
	 */
	private final String _policyName;

	protected Mechanism(MechanismBaseType mech, String policyName, PolicyDecisionPoint pdp) throws InvalidMechanismException {
		_logger.debug("Preparing mechanism from MechanismBaseType");

		if (pdp == null) {
			_logger.error("Impossible to take proper decision with a null pdp. failing miserably");
			throw new RuntimeException();
		}

		_pdp = pdp;
		_policyName = policyName;
		_name = mech.getName();
		_description = mech.getDescription();
		_lastUpdate = 0;
		_timestepSize = mech.getTimestep().getAmount() * TimeAmount.getTimeUnitMultiplier(mech.getTimestep().getUnit());
		_timestep = 0;
		_triggerEvent = EventMatch.convertFrom(mech.getTrigger(), _pdp);

		_condition = new Condition(mech.getCondition(), this);
		_executeAsyncActions = new LinkedList<ExecuteAction>();

		_backupCondition = new ArrayDeque<>(2);

		_logger.debug("Processing executeAsyncActions");
		// Processing synchronous executeActions for allow
		for (ExecuteAsyncActionType execAction : mech.getExecuteAsyncAction()) {
			_executeAsyncActions.add(new ExecuteAction(execAction));
		}

		// Get observed by the PDP, such that we can signal the end of a timestep
		addObserver(pdp);
	}

	@Override
	public String getName() {
		return _name;
	}

	public AuthorizationAction getAuthorizationAction() {
		return _authorizationAction;
	}

	public List<ExecuteAction> getExecuteAsyncActions() {
		return _executeAsyncActions;
	}

	public EventMatch getTriggerEvent() {
		return _triggerEvent;
	}

	@Override
	public long getTimestepSize() {
		return _timestepSize;
	}

	@Override
	public long getLastUpdate() {
		return _lastUpdate;
	}

	@Override
	public ICondition getCondition() {
		return _condition;
	}

	@Override
	public String getPolicyName() {
		return _policyName;
	}

	public void revoke() {
		_interrupted = true;
	}

	public Decision notifyEvent(IEvent event, Decision d) {
		_logger.debug("updating mechanism [{}]", _name);

		if (_triggerEvent.matches(event)) {
			_logger.info("Trigger event matches -> evaluating condition");

			/*
			 * synchronize with tick()
			 */
			synchronized(this) {
				if (_condition.tick()) {
					_logger.info("Condition satisfied; merging mechanism into decision");
					d.processMechanism(this, event);
				} else {
					_logger.info("Condition NOT satisfied");
				}
			}
		}

		return d;
	}

	public void startSimulation() {
		_condition.startSimulation();
		_backupCondition.addFirst(_condition);
	}

	public void stopSimulation() {
		if (_backupCondition.isEmpty()) {
			throw new IllegalStateException("No ongoing simulation. Cannot stop simulation.");
		}
		_condition = _backupCondition.getFirst();
	}


	private boolean tick() {
		long now = System.currentTimeMillis();
		long elapsedLastUpdate = now - _lastUpdate;
		long difference = elapsedLastUpdate - _timestepSize;

		if (difference < 0) { // Aborting update because the timestep has not
			// yet passed
			_logger.trace("[{}] Timestep remaining {} -> timestep has not yet passed", _name, difference);
			_logger.trace("##################################################");
			return false;
		}

		// Correct time substracting possible delay in the execution because
		// difference between timestep and last time
		// mechanism was updated will not be exactly the timestepSize
		if (difference > _timestepSize) {
			_logger.warn(
					"[{}] Timestep difference is larger than mechanism's timestep size => we missed to evaluate at least one timestep!!",
					_name);
			_logger.warn("--------------------------------------------------------------------------------------------------------------");
		}

		_timestep++;
		_logger.debug("//////////////////////////////////////////////////////");
		_logger.debug("[{}] Null-Event updating {}. timestep at interval of {} us", _name, _timestep, _timestepSize);


		/*
		 * synchronize with notifyEvent()
		 */
		boolean conditionValue;
		synchronized(this) {
			conditionValue = _condition.tick();
		}
		_logger.debug("Condition evaluated to: " + conditionValue);
		_logger.debug("//////////////////////////////////////////////////////");

		setChanged();
		notifyObservers(END_OF_TIMESTEP);

		/*
		 * Important! Do this after _condition.evaluate(),
		 * as it will rely on _lastUpdate value.
		 */
		_lastUpdate = now - difference;

		return conditionValue;
	}

	@Override
	public void run() {
		_logger.info("Started mechanism update thread usleep={} ms", _timestepSize);

		_lastUpdate = System.currentTimeMillis();

		while (!_interrupted) {
			try {
				if (tick()) {
					_logger.info("Mechanism condition satisfied; triggered optional executeActions");
					for (ExecuteAction execAction : getExecuteAsyncActions()) {
						if (execAction.getProcessor().equals("pep"))
							_logger.warn("Timetriggered execution of executeAction [{}] not possible with processor PEP",
									execAction.getName());
						else {
							_logger.debug("Execute asynchronous action [{}]", execAction.getName());
							_pdp.executeAction(execAction, false);
						}
					}
				}

				if (_interrupted) {
					_logger.info("Mechanism [{}] thread was interrupted. terminating...", _name);
					return;
				}

				Thread.sleep(_timestepSize);
			} catch (InterruptedException e) {
				_logger.info("[InterruptedException] Mechanism [{}] was interrupted. terminating...", _name);
			}
		}
	}

	@Override
	public String toString() {
		return MoreObjects.toStringHelper(this.getClass())
				.add("_name", _name)
				.add("_description", _description)
				.add("_timestepSize", _timestepSize)
				.add("_lastUpdate", _lastUpdate)
				.add("_timestep", _timestep)
				.add("_triggerEvent", _triggerEvent)
				.add("_condition", _condition)
				.add("_authorizationAction", _authorizationAction)
				.add("_executeActions", _executeAsyncActions)
				.toString();
	}

	public List<ExecuteAction> getExecuteActions() {
		return _executeAsyncActions;
	}

	public PolicyDecisionPoint getPolicyDecisionPoint() {
		return _pdp;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof Mechanism) {
			Mechanism other = (Mechanism) obj;
			return Objects.equals(_name, other._name)
					&& Objects.equals(_policyName, other._policyName);
		}
		return super.equals(obj);
	}

	@Override
	public int hashCode() {
		return Objects.hash(_name, _policyName);
	}
}
