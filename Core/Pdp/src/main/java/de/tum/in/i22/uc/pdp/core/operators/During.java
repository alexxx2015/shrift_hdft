package de.tum.in.i22.uc.pdp.core.operators;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.tum.in.i22.uc.pdp.core.Mechanism;
import de.tum.in.i22.uc.pdp.core.TimeAmount;
import de.tum.in.i22.uc.pdp.core.operators.State.StateVariable;
import de.tum.in.i22.uc.pdp.xsd.DuringType;

public class During extends DuringType {
	private static Logger _logger = LoggerFactory.getLogger(During.class);
	private TimeAmount timeAmount = null;

	private Operator op;

	private long _initialCounterValue;

	public During() {
	}

	@Override
	protected void init(Mechanism mech, Operator parent, long ttl) {
		super.init(mech, parent, ttl);
		timeAmount = new TimeAmount(getAmount(), getUnit(), mech.getTimestepSize());

		op = (Operator) operators;

		_initialCounterValue = timeAmount.getTimestepInterval() + 1;

		/*
		 * The During Operator evaluates to true,
		 * if this counter reaches a value of 0.
		 */
		_state.set(StateVariable.COUNTER, _initialCounterValue);

		op.init(mech, this, ttl);
	}

	@Override
	protected int initId(int id) {
		return setId(op.initId(id) + 1);
	}


	@Override
	public String toString() {
		return "DURING(" + timeAmount + "," + op + " )";
	}

	@Override
	public boolean tick() {
		long counter = _state.get(StateVariable.COUNTER);
		_logger.trace("Current state counter: {}", counter);

		if (op.tick()) {
			/*
			 * Subformula evaluated to true.
			 * Decrement the counter if it is still positive.
			 */
			if (counter > 0) {
				counter--;
			}
			_logger.debug("Subformula evaluated to true. Decrementing counter to {}.", counter);
		}
		else {
			/*
			 * Subformula evaluated to false.
			 * Reset the counter to the initial value.
			 */
			counter = _initialCounterValue;
			_logger.debug("Subformula evaluated to false. Resetting counter to {}.", _initialCounterValue);

		}

		_state.set(StateVariable.COUNTER, counter);

		// The result is true, if the counter reaches 0.
		_state.set(StateVariable.VALUE_AT_LAST_TICK, (counter == 0));
		_logger.debug("Result: {}.", _state.get(StateVariable.VALUE_AT_LAST_TICK));
		return _state.get(StateVariable.VALUE_AT_LAST_TICK);
	}

	@Override
	public void startSimulation() {
		super.startSimulation();
		op.startSimulation();
	}

	@Override
	public void stopSimulation() {
		super.stopSimulation();
		op.stopSimulation();
	}
}
