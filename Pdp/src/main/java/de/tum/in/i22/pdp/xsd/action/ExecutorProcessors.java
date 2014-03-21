//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.5-2 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2014.03.21 at 07:13:32 AM CET 
//


package de.tum.in.i22.pdp.xsd.action;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for ExecutorProcessors.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="ExecutorProcessors">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="pep"/>
 *     &lt;enumeration value="pxp"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "ExecutorProcessors")
@XmlEnum
public enum ExecutorProcessors {

    @XmlEnumValue("pep")
    PEP("pep"),
    @XmlEnumValue("pxp")
    PXP("pxp");
    private final String value;

    ExecutorProcessors(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static ExecutorProcessors fromValue(String v) {
        for (ExecutorProcessors c: ExecutorProcessors.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
