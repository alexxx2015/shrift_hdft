package de.tum.in.i22.uc.pdp.core.condition.operators;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.tum.in.i22.uc.cm.datatypes.interfaces.IEvent;
import de.tum.in.i22.uc.pdp.core.condition.Operator;
import de.tum.in.i22.uc.pdp.core.mechanisms.Mechanism;
import de.tum.in.i22.uc.pdp.xsd.AndType;

public class OSLAnd extends AndType {
	private static Logger log = LoggerFactory.getLogger(OSLAnd.class);

	public OSLAnd() {
	}

	public OSLAnd(Operator operand1, Operator operand2) {
		this.getOperators().add(operand1);
		this.getOperators().add(operand2);
	}

	@Override
	public void initOperatorForMechanism(Mechanism mech) {
		super.initOperatorForMechanism(mech);
		((Operator) this.getOperators().get(0)).initOperatorForMechanism(mech);
		((Operator) this.getOperators().get(1)).initOperatorForMechanism(mech);
	}

	@Override
	public String toString() {
		return this.getOperators().get(0) + " && " + this.getOperators().get(1);
	}

	@Override
	public boolean evaluate(IEvent curEvent) {
		Boolean op1state = ((Operator) this.getOperators().get(0)).evaluate(curEvent);
		Boolean op2state = ((Operator) this.getOperators().get(1)).evaluate(curEvent);
		this._state.value = op1state && op2state;
		log.debug("eval AND [{}]", this._state.value);
		return this._state.value;
	}
}
