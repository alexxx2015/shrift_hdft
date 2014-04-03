namespace cpp de.tum.i22.in.uc.thrift.types
namespace csharp de.tum.i22.in.uc.thrift.types
namespace java de.tum.i22.in.uc.thrift.types

include "Types.thrift"

service TAny2Pdp  {
	
	///////////////	
	// PEP 2 PDP //
	///////////////	
	Types.TResponse notifyEventSync(1: Types.TEvent e),
	oneway void notifyEventAsync(1: Types.TEvent e),
	
	
	///////////////	
	// PXP 2 PDP //
	///////////////	
	bool registerPxp(1: Types.TPxpSpec pxp),	
	
	
	///////////////	
	// PMP 2 PDP //
	///////////////	
	Types.TStatus deployMechanism(1: string mechanism),
	
	//TODO: This method returns the mechanism together with the current evaluation status.
	//      i.e. in case of cardinalities, the value of the counter so far
	// IMechanism exportMechanism(1:string par);
	
	//Overloading is not supported
	Types.TStatus revokeMechanism1 (1: string policyName),
	Types.TStatus revokeMechanism2 (1: string policyName, 2: string mechName),

	// This method would make sense to be remotely invoked only if the path was a URL,
	// but this is not supported yet. Still, for completeness, we expose this method too.
	Types.TStatus deployPolicy (1: string policyFilePath),
	
	//HashMap<String, ArrayList<IPdpMechanism>> listMechanisms();
	map<string,list<string>> listMechanisms()
}




service TAny2Pip  {
	
	///////////////	
	// PEP 2 PIP //
	///////////////	
	// Still no idea how to handle this
	// public IStatus updateInformationFlowSemantics(IPipDeployer deployer, File jarFile, ConflictResolution flagForTheConflictResolution);
	
	
	///////////////	
	// PMP 2 PIP //
	///////////////	
	Types.TStatus initialRepresentation(1: Types.TName container,2: set<Types.TData> data),
	
	    
	///////////////	
	// PIP 2 PIP //    
    ///////////////	
	bool hasAllData(1: set<Types.TData> data),
    bool hasAnyData(1: set<Types.TData> data),
    bool hasAllContainers(1: set<Types.TContainer> container),
    bool hasAnyContainer(1: set<Types.TContainer> container),
    Types.TStatus notifyActualEvent(1:Types.TEvent event)
	
	
	///////////////	
	// PDP 2 PIP //
	///////////////	
	bool evaluatePredicateSimulatingNextState(1:Types.TEvent event, 2:string predicate),
	bool evaluatePredicatCurrentState(1:string predicate),
	set<Types.TContainer> getContainerForData(1:Types.TData data),
	set<Types.TData> getDataInContainer(1:Types.TContainer container),
	//Already present in  PIP2PIP, with same behavior --> Status notifyActualEvent(1:Types.Event event),
	Types.TStatus startSimulation(),
	Types.TStatus stopSimulation(),
	bool isSimulating()

}



service TAny2Pmp  {

}



service TAny2Any  {

	// This method aggregates all the servers before
	// If thrift supported multiple inheritance, this would be equivalent to
	//
	// service TAny2Any extends TAny2Pdp,TAny2Pip,TAny2Pmp
	

	///////////////	
	// PEP 2 PDP //
	///////////////	
	Types.TResponse notifyEventSync(1: Types.TEvent e),
	oneway void notifyEventAsync(1: Types.TEvent e),
	
	
	///////////////	
	// PXP 2 PDP //
	///////////////	
	bool registerPxp(1: Types.TPxpSpec pxp),	
	
	
	///////////////	
	// PMP 2 PDP //
	///////////////	
	Types.TStatus deployMechanism(1: string mechanism),
	
	//TODO: This method returns the mechanism together with the current evaluation status.
	//      i.e. in case of cardinalities, the value of the counter so far
	// IMechanism exportMechanism(1:string par);
	
	//Overloading is not supported
	Types.TStatus revokeMechanism1 (1: string policyName),
	Types.TStatus revokeMechanism2 (1: string policyName, 2: string mechName),

	// This method would make sense to be remotely invoked only if the path was a URL,
	// but this is not supported yet. Still, for completeness, we expose this method too.
	Types.TStatus deployPolicy (1: string policyFilePath),
	
	//HashMap<String, ArrayList<IPdpMechanism>> listMechanisms();
	map<string,list<string>> listMechanisms()
	
	///////////////	
	// PEP 2 PIP //
	///////////////	
	// Still no idea how to handle this
	// public IStatus updateInformationFlowSemantics(IPipDeployer deployer, File jarFile, ConflictResolution flagForTheConflictResolution);
	
	
	///////////////	
	// PMP 2 PIP //
	///////////////	
	Types.TStatus initialRepresentation(1: Types.TContainer container,2: Types.TData data),
	
	    
	///////////////	
	// PIP 2 PIP //    
    ///////////////	
	bool hasAllData(1: set<Types.TData> data),
    bool hasAnyData(1: set<Types.TData> data),
    bool hasAllContainers(1: set<Types.TContainer> container),
    bool hasAnyContainer(1: set<Types.TContainer> container),
    Types.TStatus notifyActualEvent(1:Types.TEvent event),
    Types.TStatus notifyDataTransfer(1:Types.TName containerName, 2:set<Types.TData> data)
	
	
	///////////////	
	// PDP 2 PIP //
	///////////////	
	bool evaluatePredicateSimulatingNextState(1:Types.TEvent event, 2:string predicate),
	bool evaluatePredicatCurrentState(1:string predicate),
	set<Types.TContainer> getContainerForData(1:Types.TData data),
	set<Types.TData> getDataInContainer(1:Types.TContainer container),
	//Already present in  PIP2PIP, with same behavior --> Status notifyActualEvent(1:Types.TEvent event),
	Types.TStatus startSimulation(),
	Types.TStatus stopSimulation(),
	bool isSimulating()
	
	///////////////	
	// PMP 2 PMP //
    ///////////////	
	// Not sure yet what the interface between PMPs will be,
    // so for the time being we duplicate the policy management methods
    // relative to pdps. Names take Pmp as suffix to be distignuishable in TAny2any.
    // notice that these methods need to be cahnged, as soon as we know what the 
    // interface between 2 PMPs will look like

	Types.TStatus deployMechanismPmp(1: string mechanism),

	//Overloading is not supported
	Types.TStatus revokeMechanism1Pmp (1: string policyName),
	Types.TStatus revokeMechanism2Pmp (1: string policyName, 2: string mechName),

	// This method would make sense to be remotely invoked only if the path was a URL,
	// but this is not supported yet. Still, for completeness, we expose this method too.
	Types.TStatus deployPolicyPmp (1: string policyFilePath),

	//TODO: This method returns the mechanism together with the current evaluation status.
	//      i.e. in case of cardinalities, the value of the counter so far
	// IMechanism exportMechanism(1:string par);
	
	//HashMap<String, ArrayList<IPdpMechanism>> listMechanisms();
	map<string, list<string>> listMechanismsPmp()
	
	
}




/*****************************************
***************** P X P ******************
******************************************/

service TAny2Pxp{
    ///////////////	
	// PDP 2 PXP //
    ///////////////	
	Types.TStatus execute(1: list<Types.TEvent> eventList)
}





/*****************************************
 *****************************************
 *** Old methods used by Tobias's code ***
 *****************************************
 *****************************************
 

service GenericThriftConnector {

	oneway void processEventAsync(1: Types.TEvent e),

	Types.TResponse processEventSync(1: Types.TEvent e)

}

service ExtendedThriftConnector extends GenericThriftConnector {
	
	oneway void processEventAsync(1: Types.TEvent e, 2: string senderID),

	Types.TResponse processEventSync(1: Types.TEvent e, 2: string senderID)
	
}


service MWThriftConnector {

	oneway void dumpGraph(1: string graphName),
	
	oneway void setDetectionMode(1: DetectionMode mode),
	
	string getGraphInfo(1: string graphName),

	string getFullGraph(1: string graphName),
	
	string getPartialGraph(1: string graphName, 2: long startTime, 3: long endTime)
	
}
*/