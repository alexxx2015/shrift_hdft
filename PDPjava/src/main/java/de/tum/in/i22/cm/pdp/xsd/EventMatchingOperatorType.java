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
import javax.xml.bind.annotation.XmlType;
import de.tum.in.i22.cm.pdp.internal.ParamMatch;
import de.tum.in.i22.cm.pdp.internal.condition.Operator;
import org.jvnet.jaxb2_commons.lang.JAXBToStringStrategy;
import org.jvnet.jaxb2_commons.lang.ToString;
import org.jvnet.jaxb2_commons.lang.ToStringStrategy;
import org.jvnet.jaxb2_commons.locator.ObjectLocator;


/**
 * <p>Java-Klasse für EventMatchingOperatorType complex type.
 * 
 * <p>Das folgende Schemafragment gibt den erwarteten Content an, der in dieser Klasse enthalten ist.
 * 
 * <pre>
 * &lt;complexType name="EventMatchingOperatorType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="paramMatch" type="{http://www.iese.fhg.de/pef/1.0/enforcementLanguage}ParamMatchType" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *       &lt;attribute name="action" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="tryEvent" use="required" type="{http://www.w3.org/2001/XMLSchema}boolean" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "EventMatchingOperatorType", propOrder = {
    "params"
})
public abstract class EventMatchingOperatorType
    extends Operator
    implements ToString
{

    @XmlElement(name = "paramMatch", type = ParamMatch.class)
    protected List<ParamMatchType> params;
    @XmlAttribute(name = "action", required = true)
    protected String action;
    @XmlAttribute(name = "tryEvent", required = true)
    protected boolean tryEvent;

    /**
     * Gets the value of the params property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the params property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getParams().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link ParamMatchType }
     * 
     * 
     */
    public List<ParamMatchType> getParams() {
        if (params == null) {
            params = new ArrayList<ParamMatchType>();
        }
        return this.params;
    }

    public boolean isSetParams() {
        return ((this.params!= null)&&(!this.params.isEmpty()));
    }

    public void unsetParams() {
        this.params = null;
    }

    /**
     * Ruft den Wert der action-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getAction() {
        return action;
    }

    /**
     * Legt den Wert der action-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setAction(String value) {
        this.action = value;
    }

    public boolean isSetAction() {
        return (this.action!= null);
    }

    /**
     * Ruft den Wert der tryEvent-Eigenschaft ab.
     * 
     */
    public boolean isTryEvent() {
        return tryEvent;
    }

    /**
     * Legt den Wert der tryEvent-Eigenschaft fest.
     * 
     */
    public void setTryEvent(boolean value) {
        this.tryEvent = value;
    }

    public boolean isSetTryEvent() {
        return true;
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
            List<ParamMatchType> theParams;
            theParams = (this.isSetParams()?this.getParams():null);
            strategy.appendField(locator, this, "params", buffer, theParams);
        }
        {
            String theAction;
            theAction = this.getAction();
            strategy.appendField(locator, this, "action", buffer, theAction);
        }
        {
            boolean theTryEvent;
            theTryEvent = (this.isSetTryEvent()?this.isTryEvent():false);
            strategy.appendField(locator, this, "tryEvent", buffer, theTryEvent);
        }
        return buffer;
    }

}
