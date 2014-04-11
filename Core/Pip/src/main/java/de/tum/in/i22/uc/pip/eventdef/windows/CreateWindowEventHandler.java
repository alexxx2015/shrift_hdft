package de.tum.in.i22.uc.pip.eventdef.windows;

import de.tum.in.i22.uc.cm.datatypes.basic.NameBasic;
import de.tum.in.i22.uc.cm.datatypes.basic.StatusBasic.EStatus;
import de.tum.in.i22.uc.cm.datatypes.interfaces.IContainer;
import de.tum.in.i22.uc.cm.datatypes.interfaces.IStatus;
import de.tum.in.i22.uc.pip.eventdef.BaseEventHandler;
import de.tum.in.i22.uc.pip.eventdef.ParameterNotFoundException;

public class CreateWindowEventHandler extends BaseEventHandler {

	public CreateWindowEventHandler() {
		super();
	}

	@Override
	protected IStatus update() {
		String pid = null;
		String processName = null;
		String windowHandle = null;
		try {
			pid = getParameterValue("PID");
			processName = getParameterValue("ProcessName");
			windowHandle = getParameterValue("WindowHandle");
		} catch (ParameterNotFoundException e) {
			_logger.error(e.getMessage());
			return _messageFactory.createStatus(
					EStatus.ERROR_EVENT_PARAMETER_MISSING, e.getMessage());
		}

		IContainer processContainer = WindowsEvents.instantiateProcess(pid, processName);

		IContainer containerIdByWindowHandle = basicIfModel.getContainer(new NameBasic(windowHandle));

		// check if container for window exists and create new container if not
		if (containerIdByWindowHandle == null) {
			containerIdByWindowHandle = _messageFactory.createContainer();
			basicIfModel.addName(new NameBasic(windowHandle), containerIdByWindowHandle);
		}

		basicIfModel.addData(basicIfModel.getData(processContainer), containerIdByWindowHandle);
		basicIfModel.addAlias(processContainer, containerIdByWindowHandle);

		return _messageFactory.createStatus(EStatus.OKAY);
	}
}
