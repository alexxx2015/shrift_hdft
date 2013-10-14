package de.fraunhofer.iese.ind2uce.internal.pdp;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.ArrayList;

public interface IPolicyDecisionPoint extends Remote
{
  // PDP exported methods
  public int      pdpStart() throws RemoteException;
  public int      pdpStop() throws RemoteException;
  
  public int      registerPEP(String pepName, String className) throws RemoteException;
  public int      registerAction(String actionName, String pepName) throws RemoteException;

  public int      registerPXP(String pxpName, String className) throws RemoteException;
  public int      registerPXPinstance(String pxpName, Object clazz) throws RemoteException;
  public int      registerExecutor(String actionName, String pxpName) throws RemoteException;

  public String   pdpNotifyEventXML(String event) throws RemoteException;
  public Decision pdpNotifyEventJNI(Event event) throws RemoteException;
  
  public int      pdpDeployPolicy(String filename) throws RemoteException;
  public int      pdpDeployPolicyString(String policy, String namespace) throws RemoteException;
  public int      pdpDeployMechanism(String filename, String mechName) throws RemoteException;
  public int      pdpDeployMechanismString(String policy, String mechName) throws RemoteException;
  public int      pdpRevokeMechanism(String mechName, String namespace) throws RemoteException;
  
  public String            listDeployedMechanisms() throws RemoteException;
  public ArrayList<String> listDeployedMechanismsJNI() throws RemoteException;
  
  public int      setRuntimeLogLevel(int newLevel) throws RemoteException;     
}
