package de.fraunhofer.iese.pef.pdp.internal;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.fraunhofer.iese.pef.pdp.xsd.ParamMatchType;

public class ParamMatch extends ParamMatchType
{
  private static Logger log = LoggerFactory.getLogger(ParamMatch.class);
  
  public String toString()
  {
    String str = "" + this.getName() + " -> " + this.getValue() + " ("+this.getType()+")";
    return str;
  }
  
  public boolean paramMatches(Param<?> param)
  {
    if(param==null)
    {
      log.trace("Parameter [{}] not present", this.getName());
      return false;
    }
    if(this.getName().equals(param.getName()))
    {
      log.trace("param name matches");
      if(this.getValue().equals(param.getValue()))
      {
        log.trace("param value matches");
        return true;
      }
    }
    return false;
  }
  
}
