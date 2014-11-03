package de.tum.in.i22.uc.pdp.core.operators;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.tum.in.i22.uc.cm.datatypes.interfaces.AtomicOperator;
import de.tum.in.i22.uc.pdp.core.Mechanism;
import de.tum.in.i22.uc.pdp.core.operators.State.StateVariable;
import de.tum.in.i22.uc.pdp.xsd.TrueType;

public class OSLTrue extends TrueType implements AtomicOperator {
	private static Logger _logger = LoggerFactory.getLogger(OSLTrue.class);

	public OSLTrue() {
		_state.set(StateVariable.VALUE_AT_LAST_TICK, true);
	}

	@Override
	protected void init(Mechanism mech, Operator parent, long ttl) {
		super.init(mech, parent, ttl);
	}

	@Override
	public String toString() {
		return "TRUE";
	}

	@Override
	public boolean isPositive() {
		return true;
	}

	@Override
	public boolean tick() {
		_logger.info("Evaluating TRUE. true.");
		return true;
	}
}
