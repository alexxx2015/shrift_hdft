package de.tum.in.i22.uc.pdp.core.shared;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.tum.in.i22.uc.cm.datatypes.basic.EventBasic;
import de.tum.in.i22.uc.cm.datatypes.basic.ParamBasic;
import de.tum.in.i22.uc.cm.datatypes.basic.ResponseBasic;
import de.tum.in.i22.uc.cm.datatypes.basic.StatusBasic;
import de.tum.in.i22.uc.cm.datatypes.basic.StatusBasic.EStatus;
import de.tum.in.i22.uc.cm.datatypes.interfaces.IEvent;
import de.tum.in.i22.uc.cm.datatypes.interfaces.IResponse;
import de.tum.in.i22.uc.cm.datatypes.interfaces.IStatus;
import de.tum.in.i22.uc.pdp.PxpManager;
import de.tum.in.i22.uc.pdp.core.AuthorizationAction;
import de.tum.in.i22.uc.pdp.core.ExecuteAction;
import de.tum.in.i22.uc.pdp.core.mechanisms.Mechanism;

/**
 * Decision is the object produced by the PDP as a result of an event. It
 * contains information about permissiveness of the event and desired actions to
 * be performed.
 */
public class Decision implements java.io.Serializable {
	private static Logger _logger = LoggerFactory.getLogger(Decision.class);

	private static final long serialVersionUID = 4922446035665121547L;

	private AuthorizationAction _mAuthorizationAction;

	/** 'optional' executeActions processed by PXP */
	private ArrayList<ExecuteAction> _mExecuteActions = new ArrayList<ExecuteAction>();
	private PxpManager _pxpManager;

	public Decision(AuthorizationAction authAction, PxpManager pxpManager) {
		_mAuthorizationAction = authAction;
		_pxpManager = pxpManager;
	}

	public AuthorizationAction getAuthorizationAction() {
		return _mAuthorizationAction;
	}

	public void setAuthorizationAction(AuthorizationAction mAuthorizationAction) {
		_mAuthorizationAction = mAuthorizationAction;
	}

	public ArrayList<ExecuteAction> getExecuteActions() {
		return _mExecuteActions;
	}

	public void setExecuteActions(ArrayList<ExecuteAction> mExecuteActions) {
		_mExecuteActions = mExecuteActions;
	}

	public void addExecuteAction(ExecuteAction mExecuteActionTmp) {
		_mExecuteActions.add(mExecuteActionTmp);
	}

	public void processMechanism(Mechanism mech, IEvent curEvent) {
		_logger.debug("Processing mechanism={} for decision", mech.getName());

		AuthorizationAction curAuthAction = mech.getAuthorizationAction();
		if (getAuthorizationAction().getType() == Constants.AUTHORIZATION_ALLOW) {
			_logger.debug("Decision still allowing event, processing mechanisms authActions");
			do {
				_logger.debug("Processing authorizationAction {}", curAuthAction.getName());
				if (curAuthAction.getType() == Constants.AUTHORIZATION_ALLOW) {
					_logger.debug("Executing specified executeActions: {}", curAuthAction.getExecuteActions().size());
					boolean executionReturn = false;
					if (curAuthAction.getExecuteActions().size() == 0)
						executionReturn = true;
					for (ExecuteAction execAction : curAuthAction.getExecuteActions()) {
						_logger.debug("Executing [{}]", execAction.getName());

						// TODO: Execution should be forwarded to appropriate
						// execution instance!
						executionReturn = _pxpManager.execute(execAction, true);
					}

					if (!executionReturn) {
						_logger.warn("Execution failed; continuing with fallback authorization action (if present)");
						curAuthAction = curAuthAction.getFallback();
						if (curAuthAction == null) {
							_logger.warn("No fallback present; implicit INHIBIT");
							getAuthorizationAction().setType(Constants.AUTHORIZATION_INHIBIT);
							break;
						}
						continue;
					}

					_logger.debug("All specified execution actions executed successfully!");
					getAuthorizationAction().setType(curAuthAction.getType());
					break;
				} else {
					_logger.debug("Authorization action={} requires inhibiting event; adjusting decision",
							curAuthAction.getName());
					getAuthorizationAction().setType(Constants.AUTHORIZATION_INHIBIT);
					break;
				}
			} while (true);
		}

		if (getAuthorizationAction().getType() == Constants.AUTHORIZATION_INHIBIT) {
			_logger.debug("Decision requires inhibiting event; adjusting delay");
			getAuthorizationAction().setDelay(
					Math.max(getAuthorizationAction().getDelay(), curAuthAction.getDelay()));
		} else {
			_logger.debug("Decision allows event; copying modifiers (if present)");
			// TODO: modifier collision is not resolved here!
			for (ParamBasic curParam : curAuthAction.getModifiers())
				getAuthorizationAction().addModifier(curParam);
		}

		List<ExecuteAction> asyncActions = mech.getExecuteAsyncActions();
		if (asyncActions == null)
			return;
		_logger.debug("Processing asynchronous executeActions ({})", asyncActions.size());
		for (ExecuteAction execAction : asyncActions) {
			if (execAction.getProcessor().equals("pep")) {
				_logger.debug("Copying executeAction {} for processing by pep", execAction.getName());
				addExecuteAction(execAction);
			} else {
				_logger.debug("Execute asynchronous action [{}]", execAction.getName());
				_pxpManager.execute(execAction, false);
			}
		}

	}

	@Override
	public String toString() {
		if (_mAuthorizationAction == null && _mExecuteActions == null)
			return "Decision: null";

		String str = "Decision: ";
		if (_mAuthorizationAction == null)
			str += "[]";
		else
			str += _mAuthorizationAction.toString();

		str += "; optional executeActions: [";
		for (ExecuteAction a : _mExecuteActions)
			str += a.toString();
		str += "]";

		return str;
	}

	public IResponse getResponse() {
		// Convert an (IESE) Decision object into a (TUM) Response
		IStatus status;

		try {
			if (getAuthorizationAction().getAuthorizationAction()) {
				if (getAuthorizationAction().getModifiers() != null
						&& getAuthorizationAction().getModifiers().size() != 0)
					status = new StatusBasic(EStatus.MODIFY);
				else
					status = new StatusBasic(EStatus.ALLOW);
			} else {
				status = new StatusBasic(EStatus.INHIBIT);
			}
		} catch (Exception e) {
			status = new StatusBasic(EStatus.ERROR, "PDP returned wrong status (" + e + ")");
		}

		List<IEvent> list = new ArrayList<IEvent>();

		for (ExecuteAction ea : getExecuteActions()) {
			list.add(new EventBasic(ea.getName(), ea.getParams(), false));
			// TODO: take care of processor. for the time being ignored by TUM
		}

		List<ParamBasic> modifiedParameters = getAuthorizationAction().getModifiers();
		Map<String, String> modifiedParamI = new HashMap<String, String>();

		for (ParamBasic p : modifiedParameters) {
			modifiedParamI.put(p.getName(), p.getValue().toString());
		}

		IEvent modifiedEvent = new EventBasic("triggerEvent", modifiedParamI);
		IResponse res = new ResponseBasic(status, list, modifiedEvent);

		return res;
	}
}
