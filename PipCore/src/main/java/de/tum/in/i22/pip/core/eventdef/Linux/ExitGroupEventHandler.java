package de.tum.in.i22.pip.core.eventdef.Linux;

import de.tum.in.i22.pip.core.eventdef.BaseEventHandler;
import de.tum.in.i22.pip.core.eventdef.ParameterNotFoundException;
import de.tum.in.i22.uc.cm.datatypes.EStatus;
import de.tum.in.i22.uc.cm.datatypes.IStatus;

public class ExitGroupEventHandler extends BaseEventHandler {

	@Override
	public IStatus execute() {
		String host = null;
		String pids = null;

		try {
			host = getParameterValue("host");
			pids = getParameterValue("pids");
		} catch (ParameterNotFoundException e) {
			_logger.error(e.getMessage());
			return _messageFactory.createStatus(EStatus.ERROR_EVENT_PARAMETER_MISSING, e.getMessage());
		}

		for (String pid : pids.split(":")) {
			LinuxEvents.exit(host, Integer.valueOf(pid));
		}

		return STATUS_OKAY;
	}

}