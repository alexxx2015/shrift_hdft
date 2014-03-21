//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.5-2 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2014.03.21 at 07:13:32 AM CET 
//


package de.tum.in.i22.pdp.xsd;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElements;
import javax.xml.bind.annotation.XmlType;
import org.jvnet.jaxb2_commons.lang.JAXBToStringStrategy;
import org.jvnet.jaxb2_commons.lang.ToString;
import org.jvnet.jaxb2_commons.lang.ToStringStrategy;
import org.jvnet.jaxb2_commons.locator.ObjectLocator;


/**
 * <p>Java class for AuthorizationActionType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="AuthorizationActionType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;choice>
 *         &lt;element name="allow" type="{http://www.iese.fhg.de/pef/1.0/enforcementLanguage}AuthorizationAllowType"/>
 *         &lt;element name="inhibit" type="{http://www.iese.fhg.de/pef/1.0/enforcementLanguage}AuthorizationInhibitType"/>
 *       &lt;/choice>
 *       &lt;attribute name="name" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="start" type="{http://www.w3.org/2001/XMLSchema}boolean" default="false" />
 *       &lt;attribute name="fallback" type="{http://www.w3.org/2001/XMLSchema}string" default="inhibit" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "AuthorizationActionType", propOrder = {
    "allowOrInhibit"
})
public class AuthorizationActionType
    implements ToString
{

    @XmlElements({
        @XmlElement(name = "allow", type = AuthorizationAllowType.class),
        @XmlElement(name = "inhibit", type = AuthorizationInhibitType.class)
    })
    protected Object allowOrInhibit;
    @XmlAttribute(name = "name", required = true)
    protected String name;
    @XmlAttribute(name = "start")
    protected Boolean start;
    @XmlAttribute(name = "fallback")
    protected String fallback;

    /**
     * Gets the value of the allowOrInhibit property.
     * 
     * @return
     *     possible object is
     *     {@link AuthorizationAllowType }
     *     {@link AuthorizationInhibitType }
     *     
     */
    public Object getAllowOrInhibit() {
        return allowOrInhibit;
    }

    /**
     * Sets the value of the allowOrInhibit property.
     * 
     * @param value
     *     allowed object is
     *     {@link AuthorizationAllowType }
     *     {@link AuthorizationInhibitType }
     *     
     */
    public void setAllowOrInhibit(Object value) {
        this.allowOrInhibit = value;
    }

    public boolean isSetAllowOrInhibit() {
        return (this.allowOrInhibit!= null);
    }

    /**
     * Gets the value of the name property.
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
     * Sets the value of the name property.
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

    /**
     * Gets the value of the start property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public boolean isStart() {
        if (start == null) {
            return false;
        } else {
            return start;
        }
    }

    /**
     * Sets the value of the start property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setStart(boolean value) {
        this.start = value;
    }

    public boolean isSetStart() {
        return (this.start!= null);
    }

    public void unsetStart() {
        this.start = null;
    }

    /**
     * Gets the value of the fallback property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getFallback() {
        if (fallback == null) {
            return "inhibit";
        } else {
            return fallback;
        }
    }

    /**
     * Sets the value of the fallback property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setFallback(String value) {
        this.fallback = value;
    }

    public boolean isSetFallback() {
        return (this.fallback!= null);
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
            Object theAllowOrInhibit;
            theAllowOrInhibit = this.getAllowOrInhibit();
            strategy.appendField(locator, this, "allowOrInhibit", buffer, theAllowOrInhibit);
        }
        {
            String theName;
            theName = this.getName();
            strategy.appendField(locator, this, "name", buffer, theName);
        }
        {
            boolean theStart;
            theStart = (this.isSetStart()?this.isStart():false);
            strategy.appendField(locator, this, "start", buffer, theStart);
        }
        {
            String theFallback;
            theFallback = this.getFallback();
            strategy.appendField(locator, this, "fallback", buffer, theFallback);
        }
        return buffer;
    }

}
