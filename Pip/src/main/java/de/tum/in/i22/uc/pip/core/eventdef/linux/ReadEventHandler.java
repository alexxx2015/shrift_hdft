package de.tum.in.i22.uc.pip.core.eventdef.linux;

import java.util.Set;

import de.tum.in.i22.uc.cm.datatypes.EStatus;
import de.tum.in.i22.uc.cm.datatypes.IContainer;
import de.tum.in.i22.uc.cm.datatypes.IData;
import de.tum.in.i22.uc.cm.datatypes.IStatus;
import de.tum.in.i22.uc.cm.datatypes.linux.FiledescrName;
import de.tum.in.i22.uc.cm.datatypes.linux.ProcessName;
import de.tum.in.i22.uc.pip.core.eventdef.BaseEventHandler;
import de.tum.in.i22.uc.pip.core.eventdef.ParameterNotFoundException;

/**
 *
 * @author Florian Kelbert
 *
 */
public class ReadEventHandler extends BaseEventHandler {

	@Override
	public IStatus execute() {
		String host = null;
		int pid;
		int fd;

		try {
			host = getParameterValue("host");
			pid = Integer.valueOf(getParameterValue("pid"));
			fd = Integer.valueOf(getParameterValue("fd"));
		} catch (ParameterNotFoundException e) {
			_logger.error(e.getMessage());
			return _messageFactory.createStatus(EStatus.ERROR_EVENT_PARAMETER_MISSING, e.getMessage());
		}

		IContainer fileCont = ifModel.getContainer(FiledescrName.create(host, pid, fd));
		if (fileCont == null) {
			return STATUS_OKAY;
		}

		IContainer procCont = ifModel.getContainer(ProcessName.create(host, pid));
		if (procCont == null) {
			return STATUS_OKAY;
		}

		Set<IData> data = ifModel.getDataInContainer(fileCont);
		if (data == null || data.size() == 0) {
			return STATUS_OKAY;
		}

		for (IContainer c : ifModel.getAliasTransitiveReflexiveClosure(procCont)) {
			ifModel.addDataToContainerMappings(data, c);
		}

		return STATUS_OKAY;
	}
}