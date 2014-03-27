package de.tum.in.i22.uc.pdp;

import java.util.ArrayList;
import java.util.HashMap;

import de.tum.in.i22.uc.cm.basic.ResponseBasic;
import de.tum.in.i22.uc.cm.basic.StatusBasic;
import de.tum.in.i22.uc.cm.datatypes.EStatus;
import de.tum.in.i22.uc.cm.datatypes.IEvent;
import de.tum.in.i22.uc.cm.datatypes.IMechanism;
import de.tum.in.i22.uc.cm.datatypes.IPdpMechanism;
import de.tum.in.i22.uc.cm.datatypes.IPxpSpec;
import de.tum.in.i22.uc.cm.datatypes.IResponse;
import de.tum.in.i22.uc.cm.datatypes.IStatus;
import de.tum.in.i22.uc.cm.out.ConnectionManager;
import de.tum.in.i22.uc.cm.requests.GenericPdpHandler;
import de.tum.in.i22.uc.pdp.core.Event;
import de.tum.in.i22.uc.pdp.core.IPolicyDecisionPoint;
import de.tum.in.i22.uc.pdp.core.PolicyDecisionPoint;


public class PdpHandler extends GenericPdpHandler {
	private final IPolicyDecisionPoint _lpdp;

	private static PdpHandler _instance;

	private PdpHandler() {
		_lpdp = PolicyDecisionPoint.getInstance();
	}

	public static PdpHandler getInstance() {
		/*
		 * This implementation may seem odd, overengineered, redundant, or all of it.
		 * Yet, it is the best way to implement a thread-safe singleton, cf.
		 * http://www.journaldev.com/171/thread-safety-in-java-singleton-classes-with-example-code
		 * -FK-
		 */
		if (_instance == null) {
			synchronized (PdpHandler.class) {
				if (_instance == null) _instance = new PdpHandler();
			}
		}
		return _instance;
	}


	@Override
	public IResponse notifyEvent(IEvent event) {
		if (event == null) {
			return new ResponseBasic(new StatusBasic(EStatus.ERROR, "null event received"), null, null);
		}
		return _lpdp.notifyEvent(new Event(event)).getResponse();
	}

	@Override
	public IStatus deployMechanism(IMechanism mechanism) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IMechanism exportMechanism(String par) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IStatus revokeMechanism(String policyName) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IStatus revokeMechanism(String policyName, String mechName) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IStatus deployPolicy(String policyFilePath) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public HashMap<String, ArrayList<IPdpMechanism>> listMechanisms() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean registerPxp(IPxpSpec pxp) {
		// TODO Superstar.
		return false;
	}
}
