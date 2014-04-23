//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.5-2 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2014.04.23 at 02:33:36 PM CEST 
//


package de.tum.in.i22.uc.pmp.xsd;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElements;
import javax.xml.bind.annotation.XmlType;
import de.tum.in.i22.uc.pmp.xsd.time.TimeUnitType;
import org.jvnet.jaxb2_commons.lang.JAXBToStringStrategy;
import org.jvnet.jaxb2_commons.lang.ToString;
import org.jvnet.jaxb2_commons.lang.ToStringStrategy;
import org.jvnet.jaxb2_commons.locator.ObjectLocator;


/**
 * <p>Java class for RepLimType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="RepLimType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;group ref="{http://www.iese.fhg.de/pef/1.0/enforcementLanguage}Operators"/>
 *       &lt;/sequence>
 *       &lt;attGroup ref="{http://www.iese.fhg.de/pef/1.0/time}TimeAmountAttributeGroup"/>
 *       &lt;attribute name="lowerLimit" use="required" type="{http://www.w3.org/2001/XMLSchema}long" />
 *       &lt;attribute name="upperLimit" use="required" type="{http://www.w3.org/2001/XMLSchema}long" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "RepLimType", propOrder = {
    "operators"
})
public class RepLimType implements ToString
{

    @XmlElements({
        @XmlElement(name = "true", type = TrueType.class),
        @XmlElement(name = "false", type = FalseType.class),
        @XmlElement(name = "not", type = NotType.class),
        @XmlElement(name = "or", type = OrType.class),
        @XmlElement(name = "and", type = AndType.class),
        @XmlElement(name = "implies", type = ImpliesType.class),
        @XmlElement(name = "eventMatch", type = EventMatchingOperatorType.class),
        @XmlElement(name = "conditionParamMatch", type = ConditionParamMatchType.class),
        @XmlElement(name = "since", type = SinceType.class),
        @XmlElement(name = "always", type = AlwaysType.class),
        @XmlElement(name = "before", type = BeforeType.class),
        @XmlElement(name = "during", type = DuringType.class),
        @XmlElement(name = "within", type = WithinType.class),
        @XmlElement(name = "repLim", type = RepLimType.class),
        @XmlElement(name = "repSince", type = RepSinceType.class),
        @XmlElement(name = "repMax", type = RepMaxType.class),
        @XmlElement(name = "stateBasedFormula", type = StateBasedOperatorType.class),
        @XmlElement(name = "eval", type = EvalOperatorType.class)
    })
    protected Object operators;
    @XmlAttribute(name = "lowerLimit", required = true)
    protected long lowerLimit;
    @XmlAttribute(name = "upperLimit", required = true)
    protected long upperLimit;
    @XmlAttribute(name = "amount", required = true)
    protected long amount;
    @XmlAttribute(name = "unit")
    protected TimeUnitType unit;

    /**
     * Gets the value of the operators property.
     * 
     * @return
     *     possible object is
     *     {@link TrueType }
     *     {@link FalseType }
     *     {@link NotType }
     *     {@link OrType }
     *     {@link AndType }
     *     {@link ImpliesType }
     *     {@link EventMatchingOperatorType }
     *     {@link ConditionParamMatchType }
     *     {@link SinceType }
     *     {@link AlwaysType }
     *     {@link BeforeType }
     *     {@link DuringType }
     *     {@link WithinType }
     *     {@link RepLimType }
     *     {@link RepSinceType }
     *     {@link RepMaxType }
     *     {@link StateBasedOperatorType }
     *     {@link EvalOperatorType }
     *     
     */
    public Object getOperators() {
        return operators;
    }

    /**
     * Sets the value of the operators property.
     * 
     * @param value
     *     allowed object is
     *     {@link TrueType }
     *     {@link FalseType }
     *     {@link NotType }
     *     {@link OrType }
     *     {@link AndType }
     *     {@link ImpliesType }
     *     {@link EventMatchingOperatorType }
     *     {@link ConditionParamMatchType }
     *     {@link SinceType }
     *     {@link AlwaysType }
     *     {@link BeforeType }
     *     {@link DuringType }
     *     {@link WithinType }
     *     {@link RepLimType }
     *     {@link RepSinceType }
     *     {@link RepMaxType }
     *     {@link StateBasedOperatorType }
     *     {@link EvalOperatorType }
     *     
     */
    public void setOperators(Object value) {
        this.operators = value;
    }

    public boolean isSetOperators() {
        return (this.operators!= null);
    }

    /**
     * Gets the value of the lowerLimit property.
     * 
     */
    public long getLowerLimit() {
        return lowerLimit;
    }

    /**
     * Sets the value of the lowerLimit property.
     * 
     */
    public void setLowerLimit(long value) {
        this.lowerLimit = value;
    }

    public boolean isSetLowerLimit() {
        return true;
    }

    /**
     * Gets the value of the upperLimit property.
     * 
     */
    public long getUpperLimit() {
        return upperLimit;
    }

    /**
     * Sets the value of the upperLimit property.
     * 
     */
    public void setUpperLimit(long value) {
        this.upperLimit = value;
    }

    public boolean isSetUpperLimit() {
        return true;
    }

    /**
     * Gets the value of the amount property.
     * 
     */
    public long getAmount() {
        return amount;
    }

    /**
     * Sets the value of the amount property.
     * 
     */
    public void setAmount(long value) {
        this.amount = value;
    }

    public boolean isSetAmount() {
        return true;
    }

    /**
     * Gets the value of the unit property.
     * 
     * @return
     *     possible object is
     *     {@link TimeUnitType }
     *     
     */
    public TimeUnitType getUnit() {
        if (unit == null) {
            return TimeUnitType.TIMESTEPS;
        } else {
            return unit;
        }
    }

    /**
     * Sets the value of the unit property.
     * 
     * @param value
     *     allowed object is
     *     {@link TimeUnitType }
     *     
     */
    public void setUnit(TimeUnitType value) {
        this.unit = value;
    }

    public boolean isSetUnit() {
        return (this.unit!= null);
    }

    public String toString() {
        final ToStringStrategy strategy = JAXBToStringStrategy.INSTANCE;
        final StringBuilder buffer = new StringBuilder();
        append(null, buffer, strategy);
        return buffer.toString();
    }

    public StringBuilder append(ObjectLocator locator, StringBuilder buffer, ToStringStrategy strategy) {
        strategy.appendStart(locator, this, buffer);
        appendFields(locator, buffer, strategy);
        strategy.appendEnd(locator, this, buffer);
        return buffer;
    }

    public StringBuilder appendFields(ObjectLocator locator, StringBuilder buffer, ToStringStrategy strategy) {
        {
            Object theOperators;
            theOperators = this.getOperators();
            strategy.appendField(locator, this, "operators", buffer, theOperators);
        }
        {
            long theLowerLimit;
            theLowerLimit = (this.isSetLowerLimit()?this.getLowerLimit(): 0L);
            strategy.appendField(locator, this, "lowerLimit", buffer, theLowerLimit);
        }
        {
            long theUpperLimit;
            theUpperLimit = (this.isSetUpperLimit()?this.getUpperLimit(): 0L);
            strategy.appendField(locator, this, "upperLimit", buffer, theUpperLimit);
        }
        {
            long theAmount;
            theAmount = (this.isSetAmount()?this.getAmount(): 0L);
            strategy.appendField(locator, this, "amount", buffer, theAmount);
        }
        {
            TimeUnitType theUnit;
            theUnit = this.getUnit();
            strategy.appendField(locator, this, "unit", buffer, theUnit);
        }
        return buffer;
    }

}
