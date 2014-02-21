package de.tum.in.i22.pip.core.eventdef.Linux;

import de.tum.in.i22.pip.core.InformationFlowModel;
import de.tum.in.i22.pip.core.eventdef.BaseEventHandler;
import de.tum.in.i22.pip.core.eventdef.ParameterNotFoundException;
import de.tum.in.i22.uc.cm.datatypes.EStatus;
import de.tum.in.i22.uc.cm.datatypes.IStatus;

public class SocketEventHandler extends BaseEventHandler {

	@Override
	public IStatus execute() {
		String host = null;
		String pid = null;
		String fd = null;

		try {
			host = getParameterValue("host");
			pid = getParameterValue("pid");
			fd = getParameterValue("fd");
		} catch (ParameterNotFoundException e) {
			_logger.error(e.getMessage());
			return _messageFactory.createStatus(EStatus.ERROR_EVENT_PARAMETER_MISSING, e.getMessage());
		}

		InformationFlowModel ifModel = getInformationFlowModel();

		String socketContainerId = ifModel.addContainer(_messageFactory.createContainer());

		if (socketContainerId != null) {
			ifModel.addName(FiledescrName.create(host, pid, fd), socketContainerId);
		}
		else {
			_logger.fatal("Unable to create socket container.");
		}

		return _messageFactory.createStatus(EStatus.OKAY);
	}

}
