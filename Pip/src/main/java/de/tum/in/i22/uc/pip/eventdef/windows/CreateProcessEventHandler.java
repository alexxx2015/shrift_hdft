package de.tum.in.i22.uc.pip.eventdef.windows;

import de.tum.in.i22.uc.cm.basic.NameBasic;
import de.tum.in.i22.uc.cm.datatypes.EStatus;
import de.tum.in.i22.uc.cm.datatypes.IContainer;
import de.tum.in.i22.uc.cm.datatypes.IStatus;
import de.tum.in.i22.uc.pip.eventdef.BaseEventHandler;
import de.tum.in.i22.uc.pip.eventdef.ParameterNotFoundException;

public class CreateProcessEventHandler extends BaseEventHandler {

	public CreateProcessEventHandler() {
		super();
	}

	@Override
	public IStatus execute() {
		String pid = null;
		String parentPid = null;
		String visibleWindows = null;
		// currently not used
		String processName = null;
		String parentProcessName = null;

		try {
			pid = getParameterValue("PID_Child");
	        parentPid = getParameterValue("PID");
	        visibleWindows = getParameterValue("VisibleWindows");

	        processName = getParameterValue("ChildProcessName");
	        parentProcessName = getParameterValue("ParentProcessName");
		} catch (ParameterNotFoundException e) {
			_logger.error(e.getMessage());
			return _messageFactory.createStatus(EStatus.ERROR_EVENT_PARAMETER_MISSING, e.getMessage());
		}

        IContainer processContainer = WindowsEvents.instantiateProcess(pid, processName);
        IContainer parentProcessContainer = WindowsEvents.instantiateProcess(parentPid, parentProcessName);

        //add data of parent process container to child process container
        ifModel.addDataToContainer(ifModel.getDataInContainer(parentProcessContainer), processContainer);

        //add initial windows of process to model
        //TODO: REGEX??
        String[] visibleWindowsArray = visibleWindows.split(",",0);

        for (String handle : visibleWindowsArray)
        {
            IContainer windowContainer = ifModel.getContainer(new NameBasic(handle));

            if(windowContainer == null)
            {
            	windowContainer = _messageFactory.createContainer();
                ifModel.addName(new NameBasic(handle), windowContainer);
            }

            ifModel.addDataToContainer(ifModel.getDataInContainer(processContainer), windowContainer);

            ifModel.addAlias(processContainer, windowContainer);
        }

        return _messageFactory.createStatus(EStatus.OKAY);
	}

}