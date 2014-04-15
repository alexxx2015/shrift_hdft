package de.tum.in.i22.uc.pmp.core.condition.operators;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.tum.in.i22.uc.pmp.core.condition.CircularArray;
import de.tum.in.i22.uc.pmp.core.condition.Operator;
import de.tum.in.i22.uc.pmp.core.condition.TimeAmount;
import de.tum.in.i22.uc.pmp.core.shared.Event;
import de.tum.in.i22.uc.pmp.core.shared.IPmpMechanism;
import de.tum.in.i22.uc.pmp.xsd.BeforeType;

public class Before extends BeforeType 
{
  private static Logger         log        =LoggerFactory.getLogger(Before.class);
  public TimeAmount             timeAmount =null;
  
  public Before()
  {}
  
  @Override
  public void initOperatorForMechanism(IPmpMechanism mech)
  {
    this.timeAmount = new TimeAmount(this.getAmount(), this.getUnit(), mech.getTimestepSize());
    this.state.circArray = new CircularArray<Boolean>(this.timeAmount.timestepInterval);
    for(int a=0; a<this.timeAmount.timestepInterval; a++)
      this.state.circArray.set(false, a);    
    ((Operator)this.getOperators()).initOperatorForMechanism(mech);
  }  
  
  public String toString()
  {
    return "BEFORE ("+this.timeAmount + ", " + this.getOperators()+" )";
  }

  @Override
  public boolean evaluate(Event curEvent)
  { // before = at (currentTime - interval) operand was true
    log.debug("circularArray: {}", this.state.circArray);
    
    Boolean curValue = this.state.circArray.readFirst();
    this.state.value = curValue;
    if(curEvent==null)
    {
      curValue=this.state.circArray.pop();
      Boolean operandState =  ((Operator)this.getOperators()).evaluate(curEvent);
      this.state.circArray.push(operandState);
      
      log.debug("circularArray: {}", this.state.circArray);
    }
    
    log.debug("eval BEFORE [{}]", this.state.value);
    return this.state.value;
  }
}
