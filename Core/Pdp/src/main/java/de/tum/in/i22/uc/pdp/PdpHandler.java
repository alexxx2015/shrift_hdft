package de.tum.in.i22.uc.pdp;

import java.util.Map;
import java.util.Observable;
import java.util.Observer;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.tum.in.i22.uc.cm.datatypes.basic.EventBasic;
import de.tum.in.i22.uc.cm.datatypes.basic.PxpSpec;
import de.tum.in.i22.uc.cm.datatypes.basic.ResponseBasic;
import de.tum.in.i22.uc.cm.datatypes.basic.StatusBasic;
import de.tum.in.i22.uc.cm.datatypes.basic.StatusBasic.EStatus;
import de.tum.in.i22.uc.cm.datatypes.basic.XmlPolicy;
import de.tum.in.i22.uc.cm.datatypes.interfaces.IEvent;
import de.tum.in.i22.uc.cm.datatypes.interfaces.IMechanism;
import de.tum.in.i22.uc.cm.datatypes.interfaces.IResponse;
import de.tum.in.i22.uc.cm.datatypes.interfaces.IStatus;
import de.tum.in.i22.uc.cm.distribution.IDistributionManager;
import de.tum.in.i22.uc.cm.distribution.LocalLocation;
import de.tum.in.i22.uc.cm.interfaces.IPdp2Pip;
import de.tum.in.i22.uc.cm.processing.PdpProcessor;
import de.tum.in.i22.uc.cm.processing.PipProcessor;
import de.tum.in.i22.uc.cm.processing.PmpProcessor;
import de.tum.in.i22.uc.cm.processing.dummy.DummyPipProcessor;
import de.tum.in.i22.uc.cm.processing.dummy.DummyPmpProcessor;
import de.tum.in.i22.uc.pdp.core.PolicyDecisionPoint;
import de.tum.in.i22.uc.pdp.distribution.DistributedPdpResponse;
import de.tum.in.i22.uc.pdp.xsd.PolicyType;

public class PdpHandler extends PdpProcessor implements Observer {

	private static Logger _logger = LoggerFactory.getLogger(PdpHandler.class);

	private PolicyDecisionPoint _lpdp;

	private final PxpManager _pxpManager;


	public PdpHandler() {
		super(LocalLocation.getInstance());
		_pxpManager = new PxpManager();
		init(new DummyPipProcessor(), new DummyPmpProcessor());
	}

	@Override
	public IMechanism exportMechanism(String par) {
		// TODO: functionality not yet implemented in the pdp
		return null;
	}

	@Override
	public IStatus revokePolicy(String policyName) {
		_lpdp.revokePolicy(policyName);
		return new StatusBasic(EStatus.OKAY);
	}

	@Override
	public IStatus revokeMechanism(String policyName, String mechName) {
		// TODO: sanitize inputs
		boolean b = _lpdp.revokeMechanism(policyName, mechName);
		return b == true ? new StatusBasic(EStatus.OKAY) : new StatusBasic(
				EStatus.ERROR, "revokeMechanism failed");
	}

	@Override
	public IStatus deployPolicyURI(String policyFilePath) {
		return _lpdp.deployPolicyURI(policyFilePath) ? new StatusBasic(
				EStatus.OKAY) : new StatusBasic(EStatus.ERROR,
						"deploy policy failed");
	}

	@Override
	public IStatus deployPolicyXML(XmlPolicy XMLPolicy) {
		return _lpdp.deployPolicyXML(XMLPolicy)
				? new StatusBasic(EStatus.OKAY)
				: new StatusBasic(EStatus.ERROR, "deploy policy failed");
	}

	@Override
	public Map<String, Set<String>> listMechanisms() {
		return _lpdp.listDeployedMechanisms();
	}

	@Override
	public boolean registerPxp(PxpSpec pxp) {
		return _pxpManager.registerPxp(pxp);
	}

	@Override
	public void notifyEventAsync(IEvent event) {
		IResponse res = _lpdp.notifyEvent(event);
		if (event.isActual()) {
			getPip().update(event);
		}

		/*
		 * FIXME: It seems that as of now PolicyDecisionPoint
		 * handles all events as being actual events. This needs
		 * to be changed. Updating the distributed state must then
		 * only be performed if the event is actual.
		 */
		if (res instanceof DistributedPdpResponse) {
			_distributionManager.update(res);
		}
	}

	@Override
	public IResponse notifyEventSync(IEvent event) {
		if (event == null) {
			return new ResponseBasic(new StatusBasic(EStatus.ERROR, "null event received"), null, null);
		}
		IResponse res = _lpdp.notifyEvent(event);

		/*
		 * (1) If the event is actual, we update the PIP in any case
		 *
		 * (2) If the event is *not* actual AND if the event was allowed by the
		 * PDP AND if for this event allowance implies that the event is to be
		 * considered as actual event, then we create the corresponding actual
		 * event and signal it to both the PIP and the PDP as actual event.
		 */

		if (event.isActual()) {
			getPip().update(event);
		} else if (res.getAuthorizationAction().isStatus(EStatus.ALLOW) && event.allowImpliesActual()) {
			IEvent ev2 = new EventBasic(event.getName(), event.getParameters(), true);
			notifyEventAsync(ev2);
		}

		/*
		 * FIXME: It seems that as of now PolicyDecisionPoint
		 * handles all events as being actual events. This needs
		 * to be changed. Updating the distributed state must then
		 * only be performed if the event is actual.
		 */
		if (res instanceof DistributedPdpResponse) {
			_distributionManager.update(res);
		}

		return res;
	}

	@Override
	public void init(PipProcessor iface1, PmpProcessor iface2, IDistributionManager distributionManager) {
		super.init(iface1, iface2, distributionManager);

		IPdp2Pip pip = getPip();
		_logger.debug("initializing PDP. Pip reference is " + (pip != null ? "not " : "") + "NULL");
		_lpdp = new PolicyDecisionPoint(pip, _pxpManager);

		// Observe the PolicyDecisionPoint. The PolicyDecisionPoint will
		// tell us if a policy needs to be registered with the DistributionManager
		_lpdp.addObserver(this);
	}

	@Override
	public void processEventAsync(IEvent pepEvent) {
		notifyEventAsync(pepEvent);
	}

	@Override
	public IResponse processEventSync(IEvent pepEvent) {
		return notifyEventSync(pepEvent);
	}

	@Override
	public void stop() {
		_lpdp.stop();
	}

	@Override
	public void update(Observable o, final Object arg) {
		if (o instanceof PolicyDecisionPoint) {
			if (arg instanceof DistributedPdpResponse) {
				_distributionManager.update((IResponse) arg);
			}
			else if (arg instanceof PolicyType) {
				/*
				 * The PolicyDecisionPoint will inform us about Policies to register
				 * with the DistributionManager. As registering the policy for remote
				 * purposes might take a while, we start the registration process in a new thread and go on.
				 */

				new Thread() {
					@Override
					public void run() {
						_distributionManager.register(((PolicyType) arg).getName());
					}
				}.start();
			}
		}
	}
}
