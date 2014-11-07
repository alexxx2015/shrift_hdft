package de.tum.in.i22.uc.pdp.core.operators;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.tum.in.i22.uc.cm.datatypes.basic.CircularArray;
import de.tum.in.i22.uc.cm.datatypes.interfaces.AtomicOperator;
import de.tum.in.i22.uc.cm.settings.Settings;
import de.tum.in.i22.uc.pdp.core.Mechanism;
import de.tum.in.i22.uc.pdp.core.TimeAmount;
import de.tum.in.i22.uc.pdp.core.operators.State.StateVariable;
import de.tum.in.i22.uc.pdp.xsd.BeforeType;

public class Before extends BeforeType {
	private static Logger _logger = LoggerFactory.getLogger(Before.class);
	private TimeAmount _timeAmount;

	private Operator op;

	public Before() {
	}

	@Override
	protected void init(Mechanism mech, Operator parent, long ttl) {
		super.init(mech, parent, ttl);

		op = ((Operator) operators);

		if (Settings.getInstance().getDistributionEnabled()) {
			ensureDNF();
		}

		if (amount <= 0) {
			throw new IllegalArgumentException("Amount must be positive.");
		}

		_timeAmount = new TimeAmount(amount, unit, mech.getTimestepSize());
		if (_timeAmount.getTimestepInterval() <= 0) {
			throw new IllegalStateException("Arguments must result in a positive timestepValue.");
		}

		CircularArray<Boolean> stateCircArray = new CircularArray<>(_timeAmount.getTimestepInterval());
		for (int a = 0; a < _timeAmount.getTimestepInterval(); a++) {
			stateCircArray.set(false, a);
		}

		_state.set(StateVariable.CIRC_ARRAY, stateCircArray);

		op.init(mech, this, Math.max(ttl, _timeAmount.getInterval() + 2 * mech.getTimestepSize()));

		_positivity = op.getPositivity();
	}

	private void ensureDNF() {
		/*
		 * Probably, this will also work if we allow a full
		 * DNF inside BEFORE (i.e. or, and, not).
		 *
		 * The following formulas are equivalent:
		 * - not(a) before j 	== not(a before j)
		 * - (a and b) before j == (a before j) and (b before j)
		 * - (a or b) before j 	== (a before j) or (b before j)
		 *
		 */
		if (!(op instanceof OSLOr) && !(op instanceof OSLAnd) && !(op instanceof OSLNot) && !(op instanceof AtomicOperator)) {
			throw new IllegalStateException(
					"Parameter 'distributionEnabled' is true, but ECA-Condition was not in disjunctive normal form (operand of "
							+ getClass() + " was not of type " + AtomicOperator.class + ").");
		}
	}

	@Override
	protected int initId(int id) {
		return setId(op.initId(id) + 1);
	}

	@Override
	public String toString() {
		return "BEFORE(" + _timeAmount + ", " + op + " )";
	}

	@Override
	public boolean tick() {
		CircularArray<Boolean> circArray = _state.get(StateVariable.CIRC_ARRAY);

		_logger.debug("circularArray: {}", circArray);

		// The value being popped from the array corresponds to the
		// Operator's value at time (now() - timeAmount). Therefore,
		// this value is the result of the evaluation of BEFORE at this timestep.
		_state.set(StateVariable.VALUE_AT_LAST_TICK, circArray.pop());

		// Evaluate the Operator. Push the result to the array, where
		// it will be popped when time is ripe (i.e. after timeAmount).
		circArray.push(op.tick());

		_logger.debug("Value [{}] was popped from circularArray. Result: {}. New circularArray: {}", _state.get(StateVariable.VALUE_AT_LAST_TICK), _state.get(StateVariable.VALUE_AT_LAST_TICK), circArray);

		return _state.get(StateVariable.VALUE_AT_LAST_TICK);
	}

	@Override
	public boolean distributedTickPostprocessing() {
		CircularArray<Boolean> circArray = _state.get(StateVariable.CIRC_ARRAY);

		/*
		 * If distributed evaluation yields a different result from what
		 * we have pushed into the array before upon local evaluation, then remove
		 * this 'wrong' element and insert the correct one.
		 * The inserted element will be popped when time is ripe (i.e. after timeAmount).
		 */
		boolean distributedTickProcess = op.distributedTickPostprocessing();
		if (distributedTickProcess != circArray.peekLast()) {
			circArray.removeLast();
			circArray.push(distributedTickProcess);
		}

		// The actual result at this timestep is not changed
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
