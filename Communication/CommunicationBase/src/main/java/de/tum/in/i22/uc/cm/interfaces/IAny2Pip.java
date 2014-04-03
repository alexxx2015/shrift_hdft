package de.tum.in.i22.uc.cm.interfaces;

import java.io.File;
import java.util.Set;

import de.tum.in.i22.uc.cm.datatypes.EConflictResolution;
import de.tum.in.i22.uc.cm.datatypes.IContainer;
import de.tum.in.i22.uc.cm.datatypes.IData;
import de.tum.in.i22.uc.cm.datatypes.IEvent;
import de.tum.in.i22.uc.cm.datatypes.IName;
import de.tum.in.i22.uc.cm.datatypes.IPipDeployer;
import de.tum.in.i22.uc.cm.datatypes.IStatus;

/**
 * Interface defining methods to be invoked on a PIP.
 * @author Kelbert & Lovat
 *
 */
public interface IAny2Pip {
	/*
	 * From PEP
	 */
	public IStatus updateInformationFlowSemantics(
			IPipDeployer deployer,
			File jarFile,
			EConflictResolution flagForTheConflictResolution);


	/*
	 * From PDP
	 */
	public Boolean evaluatePredicateSimulatingNextState(IEvent event, String predicate);
	public Boolean evaluatePredicateCurrentState(String predicate);
	public Set<IContainer> getContainersForData(IData data);
	public Set<IData> getDataInContainer(IContainer container);
	IStatus startSimulation();
	IStatus stopSimulation();
	boolean isSimulating();


	/*
	 * From PIP
	 */
    public boolean hasAllData(Set<IData> data);
    public boolean hasAnyData(Set<IData> data);
    public boolean hasAllContainers(Set<IContainer> container);
    public boolean hasAnyContainer(Set<IContainer> container);


    /*
     * From PDP & PIP
     */
	public IStatus update(IEvent event);

	/*
	 * From PMP & PIP
	 */
	public IStatus initialRepresentation(IName containerName, Set<IData> data);
}
