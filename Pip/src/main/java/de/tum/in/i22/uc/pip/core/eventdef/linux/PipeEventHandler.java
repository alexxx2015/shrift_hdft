package de.tum.in.i22.uc.pip.core.eventdef.linux;

import de.tum.in.i22.uc.cm.datatypes.EStatus;
import de.tum.in.i22.uc.cm.datatypes.IContainer;
import de.tum.in.i22.uc.cm.datatypes.IStatus;
import de.tum.in.i22.uc.cm.datatypes.linux.FiledescrName;
import de.tum.in.i22.uc.cm.datatypes.linux.PipeContainer;
import de.tum.in.i22.uc.pip.core.eventdef.BaseEventHandler;
import de.tum.in.i22.uc.pip.core.eventdef.ParameterNotFoundException;

public class PipeEventHandler extends BaseEventHandler {

	@Override
	public IStatus execute() {
		String host = null;
		int pid;
		int fd1;
		int fd2;

		try {
			host = getParameterValue("host");
			pid = Integer.valueOf(getParameterValue("pid"));
			fd1 = Integer.valueOf(getParameterValue("fd1"));
			fd2 = Integer.valueOf(getParameterValue("fd2"));
		} catch (ParameterNotFoundException e) {
			_logger.error(e.getMessage());
			return _messageFactory.createStatus(EStatus.ERROR_EVENT_PARAMETER_MISSING, e.getMessage());
		}

		IContainer pipeContainer = new PipeContainer();

		ifModel.addName(FiledescrName.create(host, pid, fd1), pipeContainer);
		ifModel.addName(FiledescrName.create(host, pid, fd2), pipeContainer);

		return STATUS_OKAY;
	}

}