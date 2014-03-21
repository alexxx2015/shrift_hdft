//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.5-2 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2014.03.21 at 07:13:32 AM CET 
//


package de.tum.in.i22.pdp.xsd;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import org.jvnet.jaxb2_commons.lang.JAXBToStringStrategy;
import org.jvnet.jaxb2_commons.lang.ToString;
import org.jvnet.jaxb2_commons.lang.ToStringStrategy;
import org.jvnet.jaxb2_commons.locator.ObjectLocator;


/**
 * <p>Java class for PreventiveMechanismType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="PreventiveMechanismType">
 *   &lt;complexContent>
 *     &lt;extension base="{http://www.iese.fhg.de/pef/1.0/enforcementLanguage}MechanismBaseType">
 *       &lt;sequence>
 *         &lt;element name="authorizationAction" type="{http://www.iese.fhg.de/pef/1.0/enforcementLanguage}AuthorizationActionType" maxOccurs="unbounded"/>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "PreventiveMechanismType", propOrder = {
    "authorizationAction"
})
public class PreventiveMechanismType
    extends MechanismBaseType
    implements ToString
{

    @XmlElement(required = true)
    protected List<AuthorizationActionType> authorizationAction;

    /**
     * Gets the value of the authorizationAction property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the authorizationAction property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getAuthorizationAction().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link AuthorizationActionType }
     * 
     * 
     */
    public List<AuthorizationActionType> getAuthorizationAction() {
        if (authorizationAction == null) {
            authorizationAction = new ArrayList<AuthorizationActionType>();
        }
        return this.authorizationAction;
    }

    public boolean isSetAuthorizationAction() {
        return ((this.authorizationAction!= null)&&(!this.authorizationAction.isEmpty()));
    }

    public void unsetAuthorizationAction() {
        this.authorizationAction = null;
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
        super.appendFields(locator, buffer, strategy);
        {
            List<AuthorizationActionType> theAuthorizationAction;
            theAuthorizationAction = (this.isSetAuthorizationAction()?this.getAuthorizationAction():null);
            strategy.appendField(locator, this, "authorizationAction", buffer, theAuthorizationAction);
        }
        return buffer;
    }

}
