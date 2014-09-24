package de.tum.in.i22.uc.pdp.core.operators;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.tum.in.i22.uc.pdp.core.Mechanism;
import de.tum.in.i22.uc.pdp.core.operators.State.StateVariable;
import de.tum.in.i22.uc.pdp.xsd.SinceType;

public class Since extends SinceType {
	private static Logger _logger = LoggerFactory.getLogger(Since.class);

	private Operator op1;
	private Operator op2;

	public Since() {
		_state.set(StateVariable.ALWAYS_A, true);
		_state.set(StateVariable.ALWAYS_A_SINCE_LAST_B, false);
	}

	@Override
	protected void init(Mechanism mech, Operator parent, long ttl) {
		super.init(mech, parent, ttl);

		op1 = (Operator) operators.get(0);
		op2 = (Operator) operators.get(1);

		op1.init(mech, this, ttl);
		op2.init(mech, this, ttl);
	}

	@Override
	protected int initId(int id) {
		setId(op1.initId(id) + 1);
		return op2.initId(getId());
	}

	@Override
	public String toString() {
		return "SINCE (" + op1 + ", " + op2 + " )";
	}

	@Override
	public boolean tick() {
		// A since B
		boolean stateA = op1.tick();
		boolean stateB = op2.tick();

		boolean valueAtLastTick = _state.get(StateVariable.VALUE_AT_LAST_TICK);
		boolean alwaysASinceLastB = _state.get(StateVariable.ALWAYS_A_SINCE_LAST_B);

		if (!stateA) {
			_state.set(StateVariable.ALWAYS_A, false);
		}

		if (_state.get(StateVariable.ALWAYS_A)) {
			valueAtLastTick = true;
			_logger.debug("A was always true. Result: {}.", _state.get(StateVariable.VALUE_AT_LAST_TICK));
		}
		else {
			if (stateB) {
				valueAtLastTick = true;
				alwaysASinceLastB = true;
				_logger.debug("B is happening at this timestep. Result: {}.", _state.get(StateVariable.VALUE_AT_LAST_TICK));
			}
			else {
				if (stateA && alwaysASinceLastB) {
					valueAtLastTick = true;
					_logger.debug("A was always true since last B happened. Result: {}.", _state.get(StateVariable.VALUE_AT_LAST_TICK));
				}
				else {
					valueAtLastTick = false;
					alwaysASinceLastB = false;
					_logger.debug("A was NOT always true since last B happened. Result: {}.", _state.get(StateVariable.VALUE_AT_LAST_TICK));
				}
			}
		}

		_state.set(StateVariable.ALWAYS_A_SINCE_LAST_B, alwaysASinceLastB);
		_state.set(StateVariable.VALUE_AT_LAST_TICK, valueAtLastTick);

		return valueAtLastTick;
	}

	@Override
	public void startSimulation() {
		super.startSimulation();
		op1.startSimulation();
		op2.startSimulation();
	}

	@Override
	public void stopSimulation() {
		super.stopSimulation();
		op1.stopSimulation();
		op2.stopSimulation();
	}
}