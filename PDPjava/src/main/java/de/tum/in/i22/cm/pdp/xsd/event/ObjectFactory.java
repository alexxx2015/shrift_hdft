//
// Diese Datei wurde mit der JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.5-2 generiert 
// Siehe <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Änderungen an dieser Datei gehen bei einer Neukompilierung des Quellschemas verloren. 
// Generiert: 2014.03.17 um 10:31:41 AM CET 
//


package de.tum.in.i22.cm.pdp.xsd.event;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the de.tum.in.i22.cm.pdp.xsd.event package. 
 * <p>An ObjectFactory allows you to programatically 
 * construct new instances of the Java representation 
 * for XML content. The Java representation of XML 
 * content can consist of schema derived interfaces 
 * and classes representing the binding of schema 
 * type definitions, element declarations and model 
 * groups.  Factory methods for each of these are 
 * provided in this class.
 * 
 */
@XmlRegistry
public class ObjectFactory {

    private final static QName _Event_QNAME = new QName("http://www.iese.fhg.de/pef/1.0/event", "event");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: de.tum.in.i22.cm.pdp.xsd.event
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link EventType }
     * 
     */
    public EventType createEventType() {
        return new EventType();
    }

    /**
     * Create an instance of {@link EventParameterType }
     * 
     */
    public EventParameterType createEventParameterType() {
        return new EventParameterType();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link EventType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.iese.fhg.de/pef/1.0/event", name = "event")
    public JAXBElement<EventType> createEvent(EventType value) {
        return new JAXBElement<EventType>(_Event_QNAME, EventType.class, null, value);
    }

}
