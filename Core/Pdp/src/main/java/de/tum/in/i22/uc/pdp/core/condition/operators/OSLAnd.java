package de.tum.in.i22.uc.pdp.core.condition.operators;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.tum.in.i22.uc.cm.datatypes.interfaces.IEvent;
import de.tum.in.i22.uc.cm.settings.Settings;
import de.tum.in.i22.uc.pdp.core.mechanisms.Mechanism;
import de.tum.in.i22.uc.pdp.xsd.AndType;

public class OSLAnd extends AndType {
	private static Logger _logger = LoggerFactory.getLogger(OSLAnd.class);

	private Operator op1;
	private Operator op2;

	public OSLAnd() {
	}

	@Override
	public void init(Mechanism mech) {
		super.init(mech);

		op1 = (Operator) operators.get(0);
		op2 = (Operator) operators.get(1);

		if (Settings.getInstance().getDistributionEnabled()) {
			ensureDNF();
		}

		op1.init(mech);
		op2.init(mech);
	}

	@Override
	int initId(int id) {
		_id = op1.initId(id) + 1;
		setFullId(_id);
		_logger.debug("My [{}] id is {}.", this, getFullId());
		return op2.initId(_id);
	}

	@Override
	public String toString() {
		return op1 + " && " + op2;
	}

	@Override
	protected boolean localEvaluation(IEvent curEvent) {
		/*
		 * Important: _Always_ evaluate both operators
		 */
		boolean op1state = op1.evaluate(curEvent);
		boolean op2state = op2.evaluate(curEvent);

		_state.setValue(op1state && op2state);

		_logger.debug("eval AND [{}]", _state.value());
		return _state.value();
	}


	/**
	 * If distribution is enabled, then conditions must be in DNF (cf. CANS 2014 paper).
	 * This method checks whether the operands of AND(.,.) are AND, NOT, or a Literal.
	 * If this is not the case, an IllegalStateException is thrown.
	 *
	 * @throws IllegalStateException if this object is not in DNF.
	 */
	private void ensureDNF() throws IllegalArgumentException {
		if (!(op1 instanceof OSLAnd) && !(op1 instanceof OSLNot) && !(op1 instanceof LiteralOperator)) {
			throw new IllegalStateException("Parameter 'distributionEnabled' is true, but ECA-Condition was not in disjunctive normal form (first operand of "
						+ getClass() + " was of type " + op1.getClass() + ").");
		}
		if (!(op2 instanceof OSLAnd) && !(op2 instanceof OSLNot) && !(op2 instanceof LiteralOperator)) {
			throw new IllegalStateException("Parameter 'distributionEnabled' is true, but ECA-Condition was not in disjunctive normal form (second operand of "
					+ getClass() + " was of type " + op2.getClass() + ").");
		}
	}
}
