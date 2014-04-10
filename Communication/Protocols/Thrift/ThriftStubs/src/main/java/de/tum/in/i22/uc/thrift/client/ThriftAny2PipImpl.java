package de.tum.in.i22.uc.thrift.client;

import java.io.File;
import java.util.Set;

import org.apache.thrift.TException;

import de.tum.in.i22.uc.cm.datatypes.EConflictResolution;
import de.tum.in.i22.uc.cm.datatypes.IContainer;
import de.tum.in.i22.uc.cm.datatypes.IData;
import de.tum.in.i22.uc.cm.datatypes.IEvent;
import de.tum.in.i22.uc.cm.datatypes.IName;
import de.tum.in.i22.uc.cm.datatypes.IPipDeployer;
import de.tum.in.i22.uc.cm.datatypes.IStatus;
import de.tum.in.i22.uc.cm.interfaces.IAny2Pip;
import de.tum.in.i22.uc.thrift.ThriftConverter;
import de.tum.in.i22.uc.thrift.types.TAny2Pip;

class ThriftAny2PipImpl implements IAny2Pip {
	private final TAny2Pip.Client _handle;

	public ThriftAny2PipImpl(TAny2Pip.Client handle) {
		_handle = handle;
	}

	@Override
	public boolean evaluatePredicateSimulatingNextState(IEvent event, String predicate) {
		try {
			return _handle.evaluatePredicateSimulatingNextState(ThriftConverter.toThrift(event), predicate);
		} catch (TException e) {
			throw new RuntimeException(e.getMessage(), e);
		}
	}


	@Override
	public boolean evaluatePredicateCurrentState(String predicate) {
		try {
			return _handle.evaluatePredicatCurrentState(predicate);
		} catch (TException e) {
			throw new RuntimeException(e.getMessage(), e);
		}
	}


	@Override
	public Set<IContainer> getContainersForData(IData data) {
		try {
			return ThriftConverter.fromThriftContainerSet(_handle.getContainerForData(ThriftConverter.toThrift(data)));
		} catch (TException e) {
			throw new RuntimeException(e.getMessage(), e);
		}
	}


	@Override
	public Set<IData> getDataInContainer(IContainer container) {
		try {
			return ThriftConverter.fromThriftDataSet(_handle.getDataInContainer(ThriftConverter.toThrift(container)));
		} catch (TException e) {
			throw new RuntimeException(e.getMessage(), e);
		}
	}


	@Override
	public IStatus update(IEvent event) {
		try {
			return ThriftConverter.fromThrift(_handle.update(ThriftConverter.toThrift(event)));
		} catch (TException e) {
			throw new RuntimeException(e.getMessage(), e);
		}
	}


	@Override
	public IStatus startSimulation() {
		try {
			return ThriftConverter.fromThrift(_handle.startSimulation());
		} catch (TException e) {
			throw new RuntimeException(e.getMessage(), e);
		}
	}


	@Override
	public IStatus stopSimulation() {
		try {
			return ThriftConverter.fromThrift(_handle.stopSimulation());
		} catch (TException e) {
			throw new RuntimeException(e.getMessage(), e);
		}
	}


	@Override
	public boolean isSimulating() {
		try {
			return _handle.isSimulating();
		} catch (TException e) {
			throw new RuntimeException(e.getMessage(), e);
		}
	}


	@Override
	public boolean hasAllData(Set<IData> data) {
		try {
			return _handle.hasAllData(ThriftConverter.toThriftDataSet(data));
		} catch (TException e) {
			throw new RuntimeException(e.getMessage(), e);
		}
	}


	@Override
	public boolean hasAnyData(Set<IData> data) {
		try {
			return _handle.hasAnyData(ThriftConverter.toThriftDataSet(data));
		} catch (TException e) {
			throw new RuntimeException(e.getMessage(), e);
		}
	}


	@Override
	public boolean hasAllContainers(Set<IName> containers) {
		try {
			return _handle.hasAllContainers(ThriftConverter.toThriftNameSet(containers));
		} catch (TException e) {
			throw new RuntimeException(e.getMessage(), e);
		}
	}


	@Override
	public boolean hasAnyContainer(Set<IName> containers) {
		try {
			return _handle.hasAnyContainer(ThriftConverter.toThriftNameSet(containers));
		} catch (TException e) {
			throw new RuntimeException(e.getMessage(), e);
		}
	}

	@Override
	public IStatus initialRepresentation(IName containerName, Set<IData> data) {
		try {
			return ThriftConverter.fromThrift(_handle.initialRepresentation(ThriftConverter.toThrift(containerName), ThriftConverter.toThriftDataSet(data)));
		} catch (TException e) {
			throw new RuntimeException(e.getMessage(), e);
		}
	}


	@Override
	public IStatus updateInformationFlowSemantics(IPipDeployer deployer, File jarFile,
			EConflictResolution flagForTheConflictResolution) {
		// TODO Auto-generated method stub
		// not yet supported by thrift interface
		return null;
	}

}
