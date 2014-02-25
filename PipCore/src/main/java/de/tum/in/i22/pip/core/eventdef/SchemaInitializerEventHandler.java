package de.tum.in.i22.pip.core.eventdef;

import de.tum.in.i22.pip.core.InformationFlowModel;
import de.tum.in.i22.uc.cm.basic.ContainerName;
import de.tum.in.i22.uc.cm.datatypes.EStatus;
import de.tum.in.i22.uc.cm.datatypes.IContainer;
import de.tum.in.i22.uc.cm.datatypes.IData;
import de.tum.in.i22.uc.cm.datatypes.IEvent;
import de.tum.in.i22.uc.cm.datatypes.IStatus;

public class SchemaInitializerEventHandler extends BaseEventHandler {

	public SchemaInitializerEventHandler() {
		super();
	}

	@Override
	public IStatus execute() {

		//This event is used only during tests to initialize the information flow schema to a specific state

		InformationFlowModel ifModel = getInformationFlowModel();

		String contName="TEST_C";
		String dataName="TEST_D";
		

		IEvent e = getEvent();


		String contId = ifModel.getContainerIdByName(new ContainerName(
				contName));

		_logger.debug("contID = " + contId);

		if (contId == null) {
			IContainer container = _messageFactory.createContainer("TestContainer",contName);

			contId = ifModel.addContainer(container);
			ifModel.addName(new ContainerName(contName), contId);

			IData d= _messageFactory.createData(dataName);
			ifModel.addData(d);
			ifModel.addDataToContainerMapping(dataName, contId);
			_logger.debug(ifModel.toString());
		} else {
			_logger.error("contID = " + contId+" Already exists!!!! IMPOSSIBRU!!!");
		_logger.debug(ifModel.toString());
		}

		return _messageFactory.createStatus(EStatus.OKAY);
	}

}
