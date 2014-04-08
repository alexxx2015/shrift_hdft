package de.tum.in.i22.uc.pip.eventdef.linux;
import de.tum.in.i22.uc.cm.datatypes.EStatus;
import de.tum.in.i22.uc.cm.datatypes.IContainer;
import de.tum.in.i22.uc.cm.datatypes.IName;
import de.tum.in.i22.uc.cm.datatypes.IStatus;
import de.tum.in.i22.uc.cm.datatypes.linux.FileContainer;
import de.tum.in.i22.uc.cm.datatypes.linux.FilenameName;
import de.tum.in.i22.uc.cm.datatypes.linux.ProcessContainer;
import de.tum.in.i22.uc.cm.datatypes.linux.ProcessName;
import de.tum.in.i22.uc.pip.eventdef.BaseEventHandler;
import de.tum.in.i22.uc.pip.eventdef.ParameterNotFoundException;

/**
 *
 * @author Florian Kelbert
 *
 */
public class ExecveEventHandler extends BaseEventHandler {

	@Override
	protected IStatus update() {
		String host = null;
		int pid;
		String filename = null;

		try {
			host = getParameterValue("host");
			pid = Integer.valueOf(getParameterValue("pid"));
			filename = getParameterValue("filename");
		} catch (ParameterNotFoundException e) {
			_logger.error(e.getMessage());
			return _messageFactory.createStatus(EStatus.ERROR_EVENT_PARAMETER_MISSING, e.getMessage());
		}

		IName fileName = FilenameName.create(host, LinuxEvents.toRealPath(filename));
		IName procName = ProcessName.create(host, pid);

		IContainer fileCont = basicIfModel.getContainer(fileName);
		IContainer procCont = basicIfModel.getContainer(procName);

		if (fileCont == null) {
			fileCont = new FileContainer();
			basicIfModel.addName(fileName, fileCont);
		}

		if (procCont == null) {
			procCont = new ProcessContainer(host, pid);
			basicIfModel.addName(procName, procCont);
		}

		basicIfModel.copyData(fileName, procName);

		return STATUS_OKAY;
	}
}

