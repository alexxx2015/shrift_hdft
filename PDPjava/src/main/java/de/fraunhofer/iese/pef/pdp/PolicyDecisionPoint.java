package de.fraunhofer.iese.pef.pdp;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.UnmarshalException;
import javax.xml.bind.Unmarshaller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.protobuf.Message.Builder;
import com.google.protobuf.TextFormat;

import de.fraunhofer.iese.pef.pdp.gproto.MechanismProto.PbMechanism;
import de.fraunhofer.iese.pef.pdp.gproto.PolicyProto.PbPolicy;
import de.fraunhofer.iese.pef.pdp.gproto.PolicyProto.PbPolicyOrBuilder;
import de.fraunhofer.iese.pef.pdp.internal.ActionDescriptionStore;
import de.fraunhofer.iese.pef.pdp.internal.Constants;
import de.fraunhofer.iese.pef.pdp.internal.Decision;
import de.fraunhofer.iese.pef.pdp.internal.Event;
import de.fraunhofer.iese.pef.pdp.internal.EventMatch;
import de.fraunhofer.iese.pef.pdp.internal.IPolicyDecisionPoint;
import de.fraunhofer.iese.pef.pdp.internal.Mechanism;
import de.fraunhofer.iese.pef.pdp.internal.exceptions.InvalidMechanismException;
import de.fraunhofer.iese.pef.pdp.internal.exceptions.InvalidOperatorException;
import de.fraunhofer.iese.pef.pdp.xsd.MechanismBaseType;
import de.fraunhofer.iese.pef.pdp.xsd.PolicyType;

public class PolicyDecisionPoint implements IPolicyDecisionPoint, Serializable
{
  private static Logger                         log                    =LoggerFactory.getLogger(PolicyDecisionPoint.class);
  private static final long                     serialVersionUID       =-6823961095919408237L;
  
  private static IPolicyDecisionPoint           instance               =null;
  private ActionDescriptionStore                actionDescriptionStore =null;
  private HashMap<String, ArrayList<Mechanism>> policyTable = new HashMap<String, ArrayList<Mechanism>>();
  
  private PolicyDecisionPoint()
  {
    this.actionDescriptionStore = ActionDescriptionStore.getInstance();
  }
  
  public static IPolicyDecisionPoint getInstance()
  {
    if(instance==null) instance=new PolicyDecisionPoint();
    return instance;
  }

  public boolean deployPolicy(String policyFilename)
  {
    if(policyFilename.endsWith(".xml"))
      return deployXML(policyFilename);
    else if(policyFilename.endsWith(".gpb"))
      return deployGPB(policyFilename);
    log.warn("Unsupported message format of policy!");
    return false;
  }
  
  public boolean deployXML(String xmlFilename)
  {
    try
    {
      JAXBContext jc=JAXBContext.newInstance("de.fraunhofer.iese.pef.pdp.xsd");
      Unmarshaller u=jc.createUnmarshaller();
      
      //SchemaFactory sf = SchemaFactory.newInstance( XMLConstants.W3C_XML_SCHEMA_NS_URI );
      //Schema schema = sf.newSchema( new File("src/main/resources/xsd/enfLanguage.xsd"));
      ////Schema schema = sf.newSchema( PolicyDecisionPoint.class.getResource("xsd/enfLanguage.xsd").toURI().toURL());
      //u.setSchema(schema);
      //u.setEventHandler(new PolicyValidationEventHandler());
      
      log.info("Deploying policy from file [{}]", xmlFilename);
      JAXBElement<?> poElement=(JAXBElement<?>)u.unmarshal(new FileInputStream(xmlFilename));
      PolicyType curPolicy=(PolicyType)poElement.getValue();

      log.debug("curPolicy [name={}]: {}" , curPolicy.getName(), curPolicy.toString());
      
      List<MechanismBaseType> mechanisms = curPolicy.getDetectiveMechanismOrPreventiveMechanism();
      
      if(this.policyTable.containsKey(curPolicy.getName()))
      {
        log.error("Policy [{}] already deployed! Aborting...", curPolicy.getName());
        return false;
      }
      
      for(MechanismBaseType mech : mechanisms)
      {
        try
        {
          log.debug("Processing mechanism: {}", mech.getName());
          
          Mechanism curMechanism = new Mechanism(mech);
          ArrayList<Mechanism> mechanismList = this.policyTable.get(curPolicy.getName());
          if(mechanismList==null) mechanismList = new ArrayList<Mechanism>();
          if(mechanismList.contains(curMechanism))
          {
            log.error("Mechanism [{}] is already deployed for policy [{}]", curMechanism.getMechanismName(), curPolicy.getName());
            continue;
          }

          mechanismList.add(curMechanism);
          this.policyTable.put(curPolicy.getName(), mechanismList);
          
          log.debug("Starting mechanism update thread...");
          curMechanism.init();
          log.info("Mechanism {} started...", curMechanism.getMechanismName());
        }
        catch(InvalidMechanismException e)
        {
          log.error("Invalid mechanism specified: {}", e.getMessage());
          return false;
        }
      }
      return true;
    }
    catch(UnmarshalException e)
    {
      log.error("Syntax error in policy: " + e.getMessage());
    }
    catch(JAXBException | FileNotFoundException | ClassCastException e)
    {
      e.printStackTrace();
    }
    return false;
  }
  
  public boolean deployGPB(String gpbFilename)
  {
    PbPolicyOrBuilder curPolicy = PbPolicy.newBuilder();
    try 
    {
      CharSequence cs = readFile(gpbFilename);
      TextFormat.merge(cs, (Builder)curPolicy);
    }
    catch(Exception e)
    {
      log.error("Exception occured: " + e.getMessage());
      e.printStackTrace();
      return false;
    }

    log.debug(curPolicy.toString());
    for(PbMechanism mech : curPolicy.getMechanismsList())
    {
      log.debug("Processing mechanism: {}", mech.getName());
      try
      {
        Mechanism curMechanism = new Mechanism(mech);
        ArrayList<Mechanism> mechanismList = this.policyTable.get(curPolicy.getName());
        if(mechanismList==null) mechanismList = new ArrayList<Mechanism>();
        if(mechanismList.contains(curMechanism))
        {
          log.error("Mechanism [{}] is already deployed for policy [{}]", curMechanism.getMechanismName(), curPolicy.getName());
          continue;
        }

        mechanismList.add(curMechanism);
        this.policyTable.put(curPolicy.getName(), mechanismList);
        
        log.debug("Starting mechanism update thread...");
        curMechanism.init();
        log.info("Mechanism {} started...", curMechanism.getMechanismName());
      }
      catch(InvalidOperatorException e)
      {
        log.error("Invalid Operator specified: {}", e.getMessage());
        e.printStackTrace();
      }
    }
    return true;
  }
  
  public boolean revokePolicy(String policyName)
  {
    boolean ret = false;
    for(Mechanism mech : this.policyTable.get(policyName))
    {
      log.info("Revoking mechanism: {}", mech.getMechanismName());
      ret=mech.revoke();
    }
    return ret;
  }

  public Decision notifyEvent(Event event)
  {
    ArrayList<EventMatch> eventMatchList = this.actionDescriptionStore.getEventList(event.getEventAction());
    if(eventMatchList==null) eventMatchList = new ArrayList<EventMatch>();
    log.debug("Searching for subscribed condition nodes for event=[{}] -> subscriptions: {}", event.getEventAction(), eventMatchList.size());
    for(EventMatch eventMatch : eventMatchList)
    {
      log.info("Processing EventMatchOperator for event [{}]", eventMatch.getAction());
      eventMatch.evaluate(event);
    }
    
    ArrayList<Mechanism> mechanismList = this.actionDescriptionStore.getMechanismList(event.getEventAction());
    if(mechanismList==null) mechanismList = new ArrayList<Mechanism>();
    log.debug("Searching for triggered mechanisms for event=[{}] -> subscriptions: {}", event.getEventAction(), mechanismList.size());
    
    Decision d = new Decision("default", Constants.AUTHORIZATION_ALLOW);
    for(Mechanism mech : mechanismList)
    {
      log.info("Processing mechanism [{}] for event [{}]", mech.getMechanismName(), event.getEventAction());
      mech.notifyEvent(event, d);
    }
    return d;
  }

  public ArrayList<String> listDeployedMechanisms()
  {
    ArrayList<String> mechanismList = new ArrayList<String>();
    for(String policyName : this.policyTable.keySet())
    {
      mechanismList.add(policyName + this.policyTable.get(policyName));
    }

    return mechanismList; 
  }
  
  private String readFile(String file) throws IOException
  {
    BufferedReader reader=new BufferedReader(new FileReader(file));
    String line=null;
    StringBuilder stringBuilder=new StringBuilder();
    String ls=System.getProperty("line.separator");

    while((line=reader.readLine()) != null)
    {
      stringBuilder.append(line);
      stringBuilder.append(ls);
    }
    reader.close();
    return stringBuilder.toString();
  }  
  
}
