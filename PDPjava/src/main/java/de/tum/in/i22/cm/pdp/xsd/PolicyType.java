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
import javax.xml.bind.annotation.XmlElements;
import javax.xml.bind.annotation.XmlType;
import org.jvnet.jaxb2_commons.lang.JAXBToStringStrategy;
import org.jvnet.jaxb2_commons.lang.ToString;
import org.jvnet.jaxb2_commons.lang.ToStringStrategy;
import org.jvnet.jaxb2_commons.locator.ObjectLocator;


/**
 * <p>Java-Klasse für PolicyType complex type.
 * 
 * <p>Das folgende Schemafragment gibt den erwarteten Content an, der in dieser Klasse enthalten ist.
 * 
 * <pre>
 * &lt;complexType name="PolicyType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;choice maxOccurs="unbounded" minOccurs="0">
 *           &lt;element name="detectiveMechanism" type="{http://www.iese.fhg.de/pef/1.0/enforcementLanguage}DetectiveMechanismType"/>
 *           &lt;element name="preventiveMechanism" type="{http://www.iese.fhg.de/pef/1.0/enforcementLanguage}PreventiveMechanismType"/>
 *         &lt;/choice>
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
@XmlType(name = "PolicyType", propOrder = {
    "detectiveMechanismOrPreventiveMechanism"
})
public class PolicyType
    implements ToString
{

    @XmlElements({
        @XmlElement(name = "detectiveMechanism", type = DetectiveMechanismType.class),
        @XmlElement(name = "preventiveMechanism", type = PreventiveMechanismType.class)
    })
    protected List<MechanismBaseType> detectiveMechanismOrPreventiveMechanism;
    @XmlAttribute(name = "name", required = true)
    protected String name;

    /**
     * Gets the value of the detectiveMechanismOrPreventiveMechanism property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the detectiveMechanismOrPreventiveMechanism property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getDetectiveMechanismOrPreventiveMechanism().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link DetectiveMechanismType }
     * {@link PreventiveMechanismType }
     * 
     * 
     */
    public List<MechanismBaseType> getDetectiveMechanismOrPreventiveMechanism() {
        if (detectiveMechanismOrPreventiveMechanism == null) {
            detectiveMechanismOrPreventiveMechanism = new ArrayList<MechanismBaseType>();
        }
        return this.detectiveMechanismOrPreventiveMechanism;
    }

    public boolean isSetDetectiveMechanismOrPreventiveMechanism() {
        return ((this.detectiveMechanismOrPreventiveMechanism!= null)&&(!this.detectiveMechanismOrPreventiveMechanism.isEmpty()));
    }

    public void unsetDetectiveMechanismOrPreventiveMechanism() {
        this.detectiveMechanismOrPreventiveMechanism = null;
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
            List<MechanismBaseType> theDetectiveMechanismOrPreventiveMechanism;
            theDetectiveMechanismOrPreventiveMechanism = (this.isSetDetectiveMechanismOrPreventiveMechanism()?this.getDetectiveMechanismOrPreventiveMechanism():null);
            strategy.appendField(locator, this, "detectiveMechanismOrPreventiveMechanism", buffer, theDetectiveMechanismOrPreventiveMechanism);
        }
        {
            String theName;
            theName = this.getName();
            strategy.appendField(locator, this, "name", buffer, theName);
        }
        return buffer;
    }

}
