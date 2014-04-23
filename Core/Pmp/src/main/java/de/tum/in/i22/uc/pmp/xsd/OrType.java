//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.5-2 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2014.04.23 at 08:45:21 AM CEST 
//


package de.tum.in.i22.uc.pmp.xsd;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElements;
import javax.xml.bind.annotation.XmlType;
import org.jvnet.jaxb2_commons.lang.JAXBToStringStrategy;
import org.jvnet.jaxb2_commons.lang.ToString;
import org.jvnet.jaxb2_commons.lang.ToStringStrategy;
import org.jvnet.jaxb2_commons.locator.ObjectLocator;


/**
 * <p>Java class for OrType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="OrType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;group ref="{http://www.iese.fhg.de/pef/1.0/enforcementLanguage}Operators" maxOccurs="2" minOccurs="2"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "OrType", propOrder = {
    "operators"
})
public class OrType implements ToString
{

    @XmlElements({
        @XmlElement(name = "true", type = TrueType.class),
        @XmlElement(name = "false", type = FalseType.class),
        @XmlElement(name = "not", type = NotType.class),
        @XmlElement(name = "or", type = OrType.class),
        @XmlElement(name = "and", type = AndType.class),
        @XmlElement(name = "implies", type = ImpliesType.class),
        @XmlElement(name = "eventMatch", type = EventMatchingOperatorType.class),
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
    protected List<Object> operators;

    /**
     * Gets the value of the operators property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the operators property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getOperators().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link TrueType }
     * {@link FalseType }
     * {@link NotType }
     * {@link OrType }
     * {@link AndType }
     * {@link ImpliesType }
     * {@link EventMatchingOperatorType }
     * {@link SinceType }
     * {@link AlwaysType }
     * {@link BeforeType }
     * {@link DuringType }
     * {@link WithinType }
     * {@link RepLimType }
     * {@link RepSinceType }
     * {@link RepMaxType }
     * {@link StateBasedOperatorType }
     * {@link EvalOperatorType }
     * 
     * 
     */
    public List<Object> getOperators() {
        if (operators == null) {
            operators = new ArrayList<Object>();
        }
        return this.operators;
    }

    public boolean isSetOperators() {
        return ((this.operators!= null)&&(!this.operators.isEmpty()));
    }

    public void unsetOperators() {
        this.operators = null;
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
            List<Object> theOperators;
            theOperators = (this.isSetOperators()?this.getOperators():null);
            strategy.appendField(locator, this, "operators", buffer, theOperators);
        }
        return buffer;
    }

}
