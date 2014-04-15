package de.tum.in.i22.uc.pdp.core.shared;

import java.util.List;
import java.util.Map;

import de.tum.in.i22.uc.cm.interfaces.IPdp2Pip;

public interface IPolicyDecisionPoint {
	// PDP exported methods
	public Decision notifyEvent(Event event);

	public boolean deployPolicyURI(String filename);

	public boolean deployPolicyXML(String XMLPolicy);

	public boolean revokePolicy(String policyName);

	public boolean revokeMechanism(String policyName, String mechName);

	public Map<String, List<String>> listDeployedMechanisms();
	
	public IPdp2Pip get_pip();

}
