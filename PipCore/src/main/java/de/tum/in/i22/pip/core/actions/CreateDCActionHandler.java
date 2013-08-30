package de.tum.in.i22.pip.core.actions;

import org.apache.log4j.Logger;

import de.tum.in.i22.pip.core.InformationFlowModel;
import de.tum.in.i22.pip.core.Name;
import de.tum.in.i22.uc.cm.datatypes.EStatus;
import de.tum.in.i22.uc.cm.datatypes.IContainer;
import de.tum.in.i22.uc.cm.datatypes.IStatus;

/**
 * Printing action
 * @author Stoimenov
 *
 */
public class CreateDCActionHandler extends BaseActionHandler {

	private static final Logger _logger = Logger
			.getLogger(CreateDCActionHandler.class);

	public CreateDCActionHandler() {
		super();
	}

	@Override
	public IStatus execute() {
		_logger.info("CreateDC action handler execute");
		String pid = null;
		// String processNaem = null;
		String deviceName = null;
		try {
			pid = getParameterValue("PID");
			// processName = getParameterValue("ProcessName");
			deviceName = getParameterValue("lpszDevice");
		} catch (ParameterNotFoundException e) {
			_logger.error(e.getMessage());
			return _messageFactory.createStatus(
					EStatus.ERROR_EVENT_PARAMETER_MISSING, e.getMessage());
		}

		String processContainerId = instantiateProcess(pid);

		InformationFlowModel ifModel = getInformationFlowModel();
		String deviceContainerId = ifModel.getContainerIdByName(new Name(deviceName));

		// check if container for device exists and create new container if not
		if (deviceContainerId == null) {
			IContainer container = _messageFactory.createContainer();
			deviceContainerId = ifModel.addContainer(container);
			ifModel.addName(new Name(deviceName), deviceContainerId);
		}

		ifModel.addDataToContainerMappings(
				ifModel.getDataInContainer(processContainerId),
				deviceContainerId);

		return _messageFactory.createStatus(EStatus.OKAY);
	}

}
