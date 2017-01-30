package de.tum.in.i22.uc.dmp.requests;

import java.util.Set;

import de.tum.in.i22.uc.cm.datatypes.basic.XmlPolicy;
import de.tum.in.i22.uc.cm.datatypes.interfaces.IData;
import de.tum.in.i22.uc.cm.datatypes.interfaces.IName;
import de.tum.in.i22.uc.cm.datatypes.interfaces.IStatus;
import de.tum.in.i22.uc.cm.processing.DmpProcessor;

public class RemoteTransferDmpRequest extends DmpRequest<IStatus> {
	
	private Set<XmlPolicy> _policies;
	private String _fromHost;
	private IName _containerName;
	private Set<IData> _data;
	
	public RemoteTransferDmpRequest(Set<XmlPolicy> policies, String fromHost, IName containerName, Set<IData> data) {
		_policies = policies;
		_fromHost = fromHost;
		_containerName = containerName;
		_data = data;
	}

	@Override
	public IStatus process(DmpProcessor processor) {
		return processor.remoteTransfer(_policies, _fromHost, _containerName, _data);
	}
}