package de.tum.in.i22.uc.pmp.core.shared;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.tum.in.i22.uc.cm.datatypes.basic.ResponseBasic;
import de.tum.in.i22.uc.cm.datatypes.basic.StatusBasic;
import de.tum.in.i22.uc.cm.datatypes.basic.StatusBasic.EStatus;
import de.tum.in.i22.uc.cm.datatypes.interfaces.IEvent;
import de.tum.in.i22.uc.cm.datatypes.interfaces.IResponse;
import de.tum.in.i22.uc.cm.datatypes.interfaces.IStatus;

/**
 * Decision is the object produced by the PDP as a result of an event. It
 * contains information about permissiveness of the event and desired actions to
 * be performed.
 */
public class Decision implements java.io.Serializable
{
  private static Logger            log              =LoggerFactory.getLogger(Decision.class);

  private static final long        serialVersionUID =4922446035665121547L;

//  public static final Decision     RESPONSE_ALLOW   =new Decision("ALLOW", Constants.AUTHORIZATION_ALLOW);
//  public static final Decision     RESPONSE_INHIBIT =new Decision("INHIBIT", Constants.AUTHORIZATION_INHIBIT);

  private IPmpAuthorizationAction      mAuthorizationAction;

  /** 'optional' executeActions processed by PEP */
  private ArrayList<IPmpExecuteAction> mExecuteActions  =new ArrayList<IPmpExecuteAction>();

  public Decision()
  {}

  public Decision(IPmpAuthorizationAction authAction)
  {
    this.mAuthorizationAction=authAction;
  }

  public IPmpAuthorizationAction getAuthorizationAction()
  {
    return mAuthorizationAction;
  }

  public void setAuthorizationAction(IPmpAuthorizationAction mAuthorizationAction)
  {
    this.mAuthorizationAction=mAuthorizationAction;
  }

  public ArrayList<IPmpExecuteAction> getExecuteActions()
  {
    return mExecuteActions;
  }

  public void setExecuteActions(ArrayList<IPmpExecuteAction> mExecuteActions)
  {
    this.mExecuteActions=mExecuteActions;
  }

  public void addExecuteAction(IPmpExecuteAction mExecuteActionTmp)
  {
    mExecuteActions.add(mExecuteActionTmp);
  }

  public void processMechanism(IPmpMechanism mech, Event curEvent)
  {
    log.debug("Processing mechanism={} for decision", mech.getMechanismName());

    IPmpAuthorizationAction curAuthAction = mech.getAuthorizationAction();
    if(this.getAuthorizationAction().getType() == Constants.AUTHORIZATION_ALLOW)
    {
      log.debug("Decision still allowing event, processing mechanisms authActions");
      do
      {
        log.debug("Processing authorizationAction {}", curAuthAction.getName());
        if(curAuthAction.getType() == Constants.AUTHORIZATION_ALLOW)
        {
          log.debug("Executing specified executeActions: {}", curAuthAction.getExecuteActions().size());
          boolean executionReturn = false;
          for(IPmpExecuteAction execAction : curAuthAction.getExecuteActions())
          {
            log.debug("Executing [{}]", execAction.getName());

            // TODO: Execution should be forwarded to appropriate execution instance!
            executionReturn=true;
           }

          if(!executionReturn)
          {
            log.warn("Execution failed; continuing with fallback authorization action (if present)");
            curAuthAction=curAuthAction.getFallback();
            if(curAuthAction==null)
            {
              log.warn("No fallback present; implicit INHIBIT");
              this.getAuthorizationAction().setType(Constants.AUTHORIZATION_INHIBIT);
              break;
            }
            continue;
          }

          log.debug("All specified execution actions executed successfully!");
          this.getAuthorizationAction().setType(curAuthAction.getType());
          break;
        }
        else
        {
          log.debug("Authorization action={} requires inhibiting event; adjusting decision", curAuthAction.getName());
          this.getAuthorizationAction().setType(Constants.AUTHORIZATION_INHIBIT);
          break;
        }
      }
      while(true);
    }

    if(this.getAuthorizationAction().getType()==Constants.AUTHORIZATION_INHIBIT)
    {
      log.debug("Decision requires inhibiting event; adjusting delay");
      this.getAuthorizationAction().setDelay(Math.max(this.getAuthorizationAction().getDelay(), curAuthAction.getDelay()));
    }
    else
    {
      log.debug("Decision allows event; copying modifiers (if present)");
      // TODO: modifier collision is not resolved here!
      for(Param<?> curParam : curAuthAction.getModifiers())
        this.getAuthorizationAction().addModifier(curParam);
    }

    log.debug("Processing asynchronous executeActions");
    for(IPmpExecuteAction execAction : mech.getExecuteAsyncActions())
    {
      if(execAction.getProcessor().equals("pep"))
      {
        log.debug("Copying executeAction {} for processing by pep", execAction.getName());
        this.addExecuteAction(execAction);
      }
      else
      {
        log.debug("Execute asynchronous action [{}]", execAction.getName());
//        PxpManager.getInstance().execute(execAction,false);
        }
    }

  }

  @Override
  public String toString()
  {
    if(mAuthorizationAction == null && mExecuteActions == null) return "Decision: null";

    String str="Decision: ";
    if(mAuthorizationAction == null) str+="[]";
    else str+=this.mAuthorizationAction.toString();

    str+="; optional executeActions: [";
    for(IPmpExecuteAction a : mExecuteActions)
      str+=a.toString();
    str+="]";

    return str;
  }

  public IResponse getResponse() {
	//Convert an (IESE) Decision object into a (TUM) Response
	IStatus status;

	try{
		if (getAuthorizationAction().getAuthorizationAction()){
			status= new StatusBasic(EStatus.ALLOW);
		} else {
			status= new StatusBasic(EStatus.INHIBIT);
		}
	} catch (Exception e){
			status= new StatusBasic(EStatus.ERROR,"PDP returned wrong status ("+e+")");
		}

	List<IEvent> list = new ArrayList<IEvent>();

    for (IPmpExecuteAction ea : getExecuteActions()){
    	Event e = new Event(ea.getName(),true);
    	for (Param<?> p: ea.getParams()) e.addParam(p);
    	list.add(e.toIEvent());
    	//TODO: take care of processor. for the time being ignored by TUM
	}

	//TODO: add modified event, didn't found it so far. probably implemented as inhibit+execute.
    IResponse res = new ResponseBasic(status, list, null);

	return res;
  }

}


