package de.tum.in.i22.pip.core.manager;

import java.io.File;

import de.tum.in.i22.uc.cm.datatypes.EConflictResolution;
import de.tum.in.i22.uc.cm.datatypes.IPipDeployer;
import de.tum.in.i22.uc.cm.datatypes.IStatus;



public interface IPipManager {
	public IStatus updateInformationFlowSemantics(
			IPipDeployer deployer,
			File jarFile,
			EConflictResolution flagForTheConflictResolution);
}	
