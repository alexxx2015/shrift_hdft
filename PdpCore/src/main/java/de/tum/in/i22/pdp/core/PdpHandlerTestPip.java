package de.tum.in.i22.pdp.core;

import java.util.ArrayList;
import java.util.HashMap;

import org.apache.log4j.Logger;

import testutil.DummyMessageGen;

import com.google.inject.Inject;

import de.tum.in.i22.cm.pdp.PolicyDecisionPoint;
import de.tum.in.i22.cm.pdp.internal.Decision;
import de.tum.in.i22.cm.pdp.internal.Event;
import de.tum.in.i22.cm.pdp.internal.Mechanism;
import de.tum.in.i22.pdp.pipcacher.IPdpCore2PipCacher;
import de.tum.in.i22.pdp.pipcacher.IPdpEngine2PipCacher;
import de.tum.in.i22.uc.cm.IMessageFactory;
import de.tum.in.i22.uc.cm.basic.ResponseBasic;
import de.tum.in.i22.uc.cm.basic.StatusBasic;
import de.tum.in.i22.uc.cm.datatypes.EConflictResolution;
import de.tum.in.i22.uc.cm.datatypes.EStatus;
import de.tum.in.i22.uc.cm.datatypes.IEvent;
import de.tum.in.i22.uc.cm.datatypes.IMechanism;
import de.tum.in.i22.uc.cm.datatypes.IPipDeployer;
import de.tum.in.i22.uc.cm.datatypes.IPxpSpec;
import de.tum.in.i22.uc.cm.datatypes.IResponse;
import de.tum.in.i22.uc.cm.datatypes.IStatus;

/**
 * This contains some tests to run the PIP "inside" the PDP
 * 
 * @author Lovat
 * 
 */
public class PdpHandlerTestPip implements IIncoming {

	private static final Logger _logger = Logger.getLogger(PdpHandlerTestPip.class);
	private static IPdpCore2PipCacher _core2pip;
	private static IPdpEngine2PipCacher _engine2pip;
	private static IMessageFactory _messageFactory;
	
	private static PolicyDecisionPoint _lpdp;

	
	public PdpHandlerTestPip() {
	
	}	
	
	@Inject
	public PdpHandlerTestPip(PolicyDecisionPoint lpdp){
		_lpdp = lpdp;
		try {
			_logger.info("JavaPDP started");
//			_lpdp.deployPolicy(System.getProperty("user.dir")+"/../PdpCore/src/main/resources/DontSendSmartMeterData.xml");
//			_lpdp.deployPolicy(System.getProperty("user.dir")+"/../PdpCore/src/main/resources/testTUM.xml");
//			_lpdp.deployPolicy(System.getProperty("user.dir")+"/../PdpCore/src/main/resources/testDistr.xml");
//			_logger.info("Test policy deployed");
		} catch (Exception e) {
			_logger.fatal("Could not load native PDP library! " + e.getMessage());
		}		
	}
	
	
	
	@Override
	public IStatus deployMechanism(IMechanism mechanism) {
		// TODO implement
		_logger.debug("Deploy mechanism called");
		return DummyMessageGen.createOkStatus();
	}

	@Override
	public IMechanism exportMechanism(String par) {
		// TODO implement
		_logger.debug("Export mechanism called");
		return DummyMessageGen.createMechanism();
	}
	

	@Override
	public IStatus revokeMechanism(String policyName) {
		_logger.debug("Revoke mechanism called");
		// TODO implement
		this._lpdp.revokePolicy(policyName);
		return DummyMessageGen.createOkStatus();
	}
	

	@Override
	public IStatus revokeMechanism(String policyName, String mechName) {
		_logger.debug("Revoke mechanism called");
		// TODO implement
		this._lpdp.revokePolicy(policyName, mechName);
		return DummyMessageGen.createOkStatus();
	}

	@Override
	public IResponse notifyEvent(IEvent event) {
		if (event==null){
			_logger.error("null event received. returning error response.");
			return new ResponseBasic(new StatusBasic(EStatus.ERROR,"null event received"), null, null);
		}
		// TODO implement
		_logger.debug("Notify event "+event.getPrefixedName()+" invoked.");

		_logger.debug("Refreshing the cache");
		if (event!=null) _core2pip.refresh(event);
		
		_logger.debug("Converting event to be processed by pdp");
		//Create a new IESE event out of the TUM event
		Event ev = new Event(event);
		
		_logger.debug("Retrieved decision from pdp");
		Decision d = _lpdp.notifyEvent(ev);
		
		_logger.debug("Converting decision [ +"+d+" ]into proper response");
		//Convert (IESE) Decision into a (TUM) Response
		IResponse res= d.getResponse();
				
		_logger.debug("Returning response");
		return res;
	}

	@Override
	public IStatus updateInformationFlowSemantics(IPipDeployer deployer,
			byte[] jarFileBytes,
			EConflictResolution flagForTheConflictResolution) {
		// leave empty
		// this method is never called
		// instead PDP delegates it to PIP
		return null;
	}

	@Override
	public IStatus setPdpCore2PipCacher(IPdpCore2PipCacher core2cacher) {
		if (core2cacher==null){
			_logger.error("Parameter core2cacher is null. Error");
			return new StatusBasic(EStatus.ERROR, "Parameter core2cacher is null. Error");
		}
		_core2pip=core2cacher;
		return new StatusBasic(EStatus.OKAY);
	}

	@Override
	public IStatus setPdpEngine2PipCacher(IPdpEngine2PipCacher engine2cacher) {
		if (engine2cacher==null){
			_logger.error("Parameter engine2cacher is null. Error");
			return new StatusBasic(EStatus.ERROR, "Parameter engine2cacher is null. Error");
		}
		_logger.info("Set PDP Engine.");
		_engine2pip=engine2cacher;
		_lpdp.setIPdpEngine2Pip(_engine2pip);
		return new StatusBasic(EStatus.OKAY);
	}

	@Override
	public IStatus deployPolicy(String policyFilePath) {
		// TODO Auto-generated method stub
		this._lpdp.deployPolicy(policyFilePath);
		return new StatusBasic(EStatus.OKAY);
	}

	@Override
	public HashMap<String, ArrayList<Mechanism>> listMechanisms() {
		// TODO Auto-generated method stub
		HashMap<String, ArrayList<Mechanism>> _return = this._lpdp.listDeployedMechanisms();
		return _return;
	}

	@Override
	public String getCurrentPipModel() {
		// TODO Auto-generated method stub
		return this._engine2pip.getCurrentPipModel();
	}

	@Override
	public boolean registerPxp(IPxpSpec pxp) {
		// TODO Auto-generated method stub
		return this._lpdp.registerPxp(pxp);
	}
	
}
