//
// Diese Datei wurde mit der JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.11 generiert 
// Siehe <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Änderungen an dieser Datei gehen bei einer Neukompilierung des Quellschemas verloren. 
// Generiert: 2017.09.05 um 11:15:39 AM CEST 
//


package de.mainzelliste.paths.configuration;

import javax.xml.bind.annotation.XmlRegistry;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the de.mainzelliste.paths.configuration package. 
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


    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: de.mainzelliste.paths.configuration
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link Paths }
     * 
     */
    public Paths createPaths() {
        return new Paths();
    }

    /**
     * Create an instance of {@link Paths.Path }
     * 
     */
    public Paths.Path createPathsPath() {
        return new Paths.Path();
    }

    /**
     * Create an instance of {@link Paths.Path.Parameters }
     * 
     */
    public Paths.Path.Parameters createPathsPathParameters() {
        return new Paths.Path.Parameters();
    }

    /**
     * Create an instance of {@link Paths.Path.Parameters.Parameter }
     * 
     */
    public Paths.Path.Parameters.Parameter createPathsPathParametersParameter() {
        return new Paths.Path.Parameters.Parameter();
    }

}
