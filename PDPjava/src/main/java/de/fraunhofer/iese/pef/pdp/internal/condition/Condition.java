package de.fraunhofer.iese.pef.pdp.internal.condition;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.fraunhofer.iese.pef.pdp.gproto.ConditionProto.PbUnaryOperator;
import de.fraunhofer.iese.pef.pdp.internal.Event;
import de.fraunhofer.iese.pef.pdp.internal.Mechanism;
import de.fraunhofer.iese.pef.pdp.internal.exceptions.InvalidOperatorException;
import de.fraunhofer.iese.pef.pdp.xsd.ConditionType;

public class Condition
{
  private static Logger log = LoggerFactory.getLogger(Condition.class);

  public Operator operator = null;
  
  public Condition()
  {}
  
  public Condition(ConditionType cond, Mechanism curMechanism)
  {
    log.debug("Preparing condition from ConditionType");
    this.operator = (Operator)cond.getOperators();
    this.operator.initOperatorForMechanism(curMechanism);
    
//    try
//    {
//      Constructor<? extends Object> constructor = cond.getOperators().getClass().getConstructor((cond.getOperators().getClass().getSuperclass()), Mechanism.class);
//      this.operator = (Operator)constructor.newInstance(cond.getOperators(), curMechanism);
//      log.debug("this.operator = {}", this.operator);
//    }
//    catch(NoSuchMethodException | InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e)
//    {
//      e.printStackTrace();
//    }
  }
  
  public Condition(PbUnaryOperator pbCondition, Mechanism curMechanism) throws InvalidOperatorException
  {
    log.debug("Preparing condition from pbCondition");
    log.debug("condition: {}", pbCondition);
    if(pbCondition == null) return;
    
    PbOperatorHelper helper = new PbOperatorHelper();
    this.operator = helper.processOperator(pbCondition);
    this.operator.initOperatorForMechanism(curMechanism);
  }
  
  public String toString()
  {
    return "Condition: { " + this.operator + " }";
  }
  
  public boolean evaluate(Event curEvent)
  {
    log.debug("Evaluating condition...");
    boolean ret=false;
    try
    {
       ret = this.operator.evaluate(curEvent);
    }
    catch(Exception e)
    {
      log.error("Exception during evaluation: {}", e.getMessage());
      e.printStackTrace();
    }
    log.debug("condition value: [{}]", ret);
    return ret;
  }  
  
  
  
}
