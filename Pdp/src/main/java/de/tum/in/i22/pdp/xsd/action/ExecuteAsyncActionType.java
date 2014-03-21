//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.5-2 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2014.03.21 at 07:13:32 AM CET 
//


package de.tum.in.i22.pdp.xsd.action;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;
import org.jvnet.jaxb2_commons.lang.JAXBToStringStrategy;
import org.jvnet.jaxb2_commons.lang.ToString;
import org.jvnet.jaxb2_commons.lang.ToStringStrategy;
import org.jvnet.jaxb2_commons.locator.ObjectLocator;


/**
 * <p>Java class for ExecuteAsyncActionType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="ExecuteAsyncActionType">
 *   &lt;complexContent>
 *     &lt;extension base="{http://www.iese.fhg.de/pef/1.0/action}ExecuteActionType">
 *       &lt;attribute name="processor" type="{http://www.iese.fhg.de/pef/1.0/action}ExecutorProcessors" default="pxp" />
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ExecuteAsyncActionType")
public class ExecuteAsyncActionType
    extends ExecuteActionType
    implements ToString
{

    @XmlAttribute(name = "processor")
    protected ExecutorProcessors processor;

    /**
     * Gets the value of the processor property.
     * 
     * @return
     *     possible object is
     *     {@link ExecutorProcessors }
     *     
     */
    public ExecutorProcessors getProcessor() {
        if (processor == null) {
            return ExecutorProcessors.PXP;
        } else {
            return processor;
        }
    }

    /**
     * Sets the value of the processor property.
     * 
     * @param value
     *     allowed object is
     *     {@link ExecutorProcessors }
     *     
     */
    public void setProcessor(ExecutorProcessors value) {
        this.processor = value;
    }

    public boolean isSetProcessor() {
        return (this.processor!= null);
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
            ExecutorProcessors theProcessor;
            theProcessor = this.getProcessor();
            strategy.appendField(locator, this, "processor", buffer, theProcessor);
        }
        return buffer;
    }

}
