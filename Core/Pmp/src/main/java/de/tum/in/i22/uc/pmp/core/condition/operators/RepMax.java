package de.tum.in.i22.uc.pmp.core.condition.operators;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.tum.in.i22.uc.pmp.core.condition.Operator;
import de.tum.in.i22.uc.pmp.core.shared.Event;
import de.tum.in.i22.uc.pmp.core.shared.IPmpMechanism;
import de.tum.in.i22.uc.pmp.xsd.RepMaxType;

public class RepMax extends RepMaxType 
{
  private static Logger log   =LoggerFactory.getLogger(RepMax.class);
  //public long           limit =0;
  
  public RepMax()
  {}
  
  @Override
  public void initOperatorForMechanism(IPmpMechanism mech)
  {
    ((Operator)this.getOperators()).initOperatorForMechanism(mech);
  }  
  
  public String toString()
  {
    return "REPMAX ("+this.getLimit() + ", " + this.getOperators()+")";
  }

  @Override
  public boolean evaluate(Event curEvent)
  {
    if(!this.state.immutable)
    { 
      if(curEvent!=null && ((Operator)this.getOperators()).evaluate(curEvent))
      {
        this.state.counter++;
        log.debug("[REPMAX] Subformula was satisfied; counter incremented to [{}]", this.state.counter);
      }

      if(this.state.counter<=this.getLimit())
        this.state.value=true;
      else 
        this.state.value=false;

      if(curEvent==null && !this.state.value)
      {
        log.debug("[REPMAX] Activating immutability");
        this.state.immutable=true;
      }
    }
    
    log.debug("eval REPMAX [{}]", this.state.value);
    return this.state.value;
  }
}
