package de.tum.in.i22.uc.pip.eventdef.ios;

import java.util.HashSet;
import java.util.Set;

import de.tum.in.i22.uc.cm.	datatypes.basic.DataBasic;
import de.tum.in.i22.uc.cm.datatypes.basic.NameBasic;
import de.tum.in.i22.uc.cm.datatypes.basic.StatusBasic.EStatus;
import de.tum.in.i22.uc.cm.datatypes.interfaces.IContainer;
import de.tum.in.i22.uc.cm.datatypes.interfaces.IData;
import de.tum.in.i22.uc.cm.datatypes.interfaces.IStatus;
import de.tum.in.i22.uc.pip.eventdef.BaseEventHandler;
import de.tum.in.i22.uc.pip.eventdef.ParameterNotFoundException;


public class ReadGPSEventHandler extends BaseEventHandler {

	@Override
	protected IStatus update() {
		String processName = null; // identifier of the running application (app's name)
		String processId =null; // identifier of the phone
		String locationData = null; // items to be flown to the runningApp memory
		
		// read the event parameters
				try {
					processId = getParameterValue("ProcessID");
					processName=getParameterValue("ProcessName");
					locationData = getParameterValue("GPSdata");

				} catch (ParameterNotFoundException e) {
					_logger.error(e.getMessage());
					return _messageFactory.createStatus(
							EStatus.ERROR_EVENT_PARAMETER_MISSING, e.getMessage());
				}
				// check if a container for "fileName" exists, If not, create a new container : container-name= fileName
				IContainer gpsRunningAppMenmoryContainer = null;
				String gpsRunningAppMenmoryContainerName="ReadGPSx"+processId+"x"+processName;
				gpsRunningAppMenmoryContainer = _informationFlowModel.getContainer(new NameBasic(gpsRunningAppMenmoryContainerName));
				if (gpsRunningAppMenmoryContainer == null) {
					//1. create new container
					gpsRunningAppMenmoryContainer = _messageFactory.createContainer();
					 //2. add name to the container
					 _informationFlowModel.addName(new NameBasic(gpsRunningAppMenmoryContainerName),
							 gpsRunningAppMenmoryContainer, true);
				}
				// add the ucABitem to the abRunningAppMenmoryContainer
				//_informationFlowModel.addData(_informationFlowModel.getData(new NameBasic(abRunningAppMenmoryContainerName)), abRunningAppMenmoryContainer);
				
				Set<IData> dataSetABucItems = new HashSet<IData>();
				dataSetABucItems.add(new DataBasic(locationData.trim()));
				_informationFlowModel.addData(dataSetABucItems, gpsRunningAppMenmoryContainer);
				return _messageFactory.createStatus(EStatus.OKAY);
				
		
	}

}