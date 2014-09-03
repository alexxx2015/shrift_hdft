package de.tum.in.i22.uc.pdp.core.condition.operators;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.tum.in.i22.uc.cm.datatypes.interfaces.IEvent;
import de.tum.in.i22.uc.pdp.core.mechanisms.Mechanism;
import de.tum.in.i22.uc.pdp.xsd.FalseType;

public class OSLFalse extends FalseType {
	private static Logger log = LoggerFactory.getLogger(OSLFalse.class);

	public OSLFalse() {
	}

	public OSLFalse(FalseType op, Mechanism curMechanism) {
	}

	@Override
	public void init(Mechanism mech) {
		super.init(mech);
	}

	@Override
	public String toString() {
		return "FALSE";
	}

	@Override
	public boolean evaluate(IEvent curEvent) {
		log.debug("eval FALSE");
		return false;
	}
}