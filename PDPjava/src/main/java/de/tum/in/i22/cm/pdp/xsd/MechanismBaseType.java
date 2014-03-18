//
// Diese Datei wurde mit der JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.5-2 generiert 
// Siehe <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Änderungen an dieser Datei gehen bei einer Neukompilierung des Quellschemas verloren. 
// Generiert: 2014.03.17 um 10:31:41 AM CET 
//


package de.tum.in.i22.cm.pdp.xsd;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;
import de.tum.in.i22.cm.pdp.internal.condition.operators.EventMatchOperator;
import de.tum.in.i22.cm.pdp.xsd.action.ExecuteAsyncActionType;
import de.tum.in.i22.cm.pdp.xsd.time.TimeAmountType;
import org.jvnet.jaxb2_commons.lang.JAXBToStringStrategy;
import org.jvnet.jaxb2_commons.lang.ToString;
import org.jvnet.jaxb2_commons.lang.ToStringStrategy;
import org.jvnet.jaxb2_commons.locator.ObjectLocator;


/**
 * <p>Java-Klasse für MechanismBaseType complex type.
 * 
 * <p>Das folgende Schemafragment gibt den erwarteten Content an, der in dieser Klasse enthalten ist.
 * 
 * <pre>
 * &lt;complexType name="MechanismBaseType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="description" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="timestep" type="{http://www.iese.fhg.de/pef/1.0/time}TimeAmountType" minOccurs="0"/>
 *         &lt;element name="trigger" type="{http://www.iese.fhg.de/pef/1.0/enforcementLanguage}EventMatchingOperatorType" minOccurs="0"/>
 *         &lt;element name="condition" type="{http://www.iese.fhg.de/pef/1.0/enforcementLanguage}ConditionType" minOccurs="0"/>
 *         &lt;element name="authorizationAction" type="{http://www.iese.fhg.de/pef/1.0/enforcementLanguage}AuthorizationActionType" maxOccurs="0" minOccurs="0"/>
 *         &lt;element name="executeAsyncAction" type="{http://www.iese.fhg.de/pef/1.0/action}ExecuteAsyncActionType" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *       &lt;attribute name="name" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "MechanismBaseType", propOrder = {
    "description",
    "timestep",
    "trigger",
    "condition",
    "executeAsyncAction"
})
@XmlSeeAlso({
    PreventiveMechanismType.class,
    DetectiveMechanismType.class
})
public class MechanismBaseType implements ToString
{

    protected String description;
    protected TimeAmountType timestep;
    @XmlElement(type = EventMatchOperator.class)
    protected EventMatchOperator trigger;
    protected ConditionType condition;
    protected List<ExecuteAsyncActionType> executeAsyncAction;
    @XmlAttribute(name = "name", required = true)
    protected String name;

    /**
     * Ruft den Wert der description-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDescription() {
        return description;
    }

    /**
     * Legt den Wert der description-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDescription(String value) {
        this.description = value;
    }

    public boolean isSetDescription() {
        return (this.description!= null);
    }

    /**
     * Ruft den Wert der timestep-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link TimeAmountType }
     *     
     */
    public TimeAmountType getTimestep() {
        return timestep;
    }

    /**
     * Legt den Wert der timestep-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link TimeAmountType }
     *     
     */
    public void setTimestep(TimeAmountType value) {
        this.timestep = value;
    }

    public boolean isSetTimestep() {
        return (this.timestep!= null);
    }

    /**
     * Ruft den Wert der trigger-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link EventMatchingOperatorType }
     *     
     */
    public EventMatchingOperatorType getTrigger() {
        return trigger;
    }

    /**
     * Legt den Wert der trigger-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link EventMatchingOperatorType }
     *     
     */
    public void setTrigger(EventMatchingOperatorType value) {
        this.trigger = ((EventMatchOperator) value);
    }

    public boolean isSetTrigger() {
        return (this.trigger!= null);
    }

    /**
     * Ruft den Wert der condition-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link ConditionType }
     *     
     */
    public ConditionType getCondition() {
        return condition;
    }

    /**
     * Legt den Wert der condition-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link ConditionType }
     *     
     */
    public void setCondition(ConditionType value) {
        this.condition = value;
    }

    public boolean isSetCondition() {
        return (this.condition!= null);
    }

    /**
     * Gets the value of the executeAsyncAction property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the executeAsyncAction property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getExecuteAsyncAction().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link ExecuteAsyncActionType }
     * 
     * 
     */
    public List<ExecuteAsyncActionType> getExecuteAsyncAction() {
        if (executeAsyncAction == null) {
            executeAsyncAction = new ArrayList<ExecuteAsyncActionType>();
        }
        return this.executeAsyncAction;
    }

    public boolean isSetExecuteAsyncAction() {
        return ((this.executeAsyncAction!= null)&&(!this.executeAsyncAction.isEmpty()));
    }

    public void unsetExecuteAsyncAction() {
        this.executeAsyncAction = null;
    }

    /**
     * Ruft den Wert der name-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getName() {
        return name;
    }

    /**
     * Legt den Wert der name-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setName(String value) {
        this.name = value;
    }

    public boolean isSetName() {
        return (this.name!= null);
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
            String theDescription;
            theDescription = this.getDescription();
            strategy.appendField(locator, this, "description", buffer, theDescription);
        }
        {
            TimeAmountType theTimestep;
            theTimestep = this.getTimestep();
            strategy.appendField(locator, this, "timestep", buffer, theTimestep);
        }
        {
            EventMatchingOperatorType theTrigger;
            theTrigger = this.getTrigger();
            strategy.appendField(locator, this, "trigger", buffer, theTrigger);
        }
        {
            ConditionType theCondition;
            theCondition = this.getCondition();
            strategy.appendField(locator, this, "condition", buffer, theCondition);
        }
        {
            List<ExecuteAsyncActionType> theExecuteAsyncAction;
            theExecuteAsyncAction = (this.isSetExecuteAsyncAction()?this.getExecuteAsyncAction():null);
            strategy.appendField(locator, this, "executeAsyncAction", buffer, theExecuteAsyncAction);
        }
        {
            String theName;
            theName = this.getName();
            strategy.appendField(locator, this, "name", buffer, theName);
        }
        return buffer;
    }

}
