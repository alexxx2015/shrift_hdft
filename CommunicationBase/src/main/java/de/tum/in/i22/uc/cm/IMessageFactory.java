package de.tum.in.i22.uc.cm;
import java.util.Map;

import de.tum.in.i22.uc.cm.datatypes.EStatus;
import de.tum.in.i22.uc.cm.datatypes.IContainer;
import de.tum.in.i22.uc.cm.datatypes.IData;
import de.tum.in.i22.uc.cm.datatypes.IEvent;
import de.tum.in.i22.uc.cm.datatypes.IPipDeployer;
import de.tum.in.i22.uc.cm.datatypes.IStatus;
import de.tum.in.i22.uc.cm.gpb.PdpProtos.GpEvent;
import de.tum.in.i22.uc.cm.gpb.PdpProtos.GpPipDeployer;


public interface IMessageFactory {
	public IEvent createEvent(String name, Map<String, String> map);
	public IEvent createActualEvent(String name, Map<String, String> map);
	public IEvent createEvent(GpEvent gpEvent, long timestamp);
	public IEvent createEvent(GpEvent gpEvent);
	public IStatus createStatus(EStatus status);
	public IStatus createStatus(EStatus status, String errorMessage);
	public IContainer createContainer();
	public IData createData();
	public IPipDeployer createPipDeployer(GpPipDeployer gpPipDe);
	public IData createData(String id);
	public IEvent createDesiredEvent(String name, Map<String, String> map);
}
