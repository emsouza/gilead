package net.sf.gilead.proxy.xml;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;

import java.io.FileNotFoundException;
import java.io.InputStream;

/**
 * XML reader for additional code
 *
 * @author bruno.marchesson
 */
public class AdditionalCodeReader {

    /**
     * Read method
     *
     * @param filePath path of the XML file
     * @return the deserialised Additional code
     * @throws FileNotFoundException
     */
    public static AdditionalCode readFromFile(String filePath) throws FileNotFoundException {
        XStream xstream = new XStream(new DomDriver());

        // Alias
        xstream.alias("additionalCode", AdditionalCode.class);
        xstream.alias("attribute", Attribute.class);
        xstream.alias("constructor", Constructor.class);
        xstream.alias("method", Method.class);
        xstream.alias("parameter", Parameter.class);

        // Attributes declaration
        xstream.useAttributeFor(AdditionalCode.class, "suffix");
        xstream.useAttributeFor(AdditionalCode.class, "implementedInterface");

        xstream.useAttributeFor(Attribute.class, "name");
        xstream.useAttributeFor(Attribute.class, "visibility");
        xstream.useAttributeFor(Attribute.class, "type");
        xstream.useAttributeFor(Attribute.class, "collectionType");
        xstream.useAttributeFor(Attribute.class, "defaultValue");

        xstream.useAttributeFor(Constructor.class, "visibility");

        xstream.useAttributeFor(Method.class, "visibility");
        xstream.useAttributeFor(Method.class, "returnType");
        xstream.useAttributeFor(Method.class, "returnCollectionType");
        xstream.useAttributeFor(Method.class, "name");

        xstream.useAttributeFor(Parameter.class, "type");
        xstream.useAttributeFor(Parameter.class, "name");

        // Read Additional code
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        try (InputStream input = classLoader.getResourceAsStream(filePath)) {
            return (AdditionalCode) xstream.fromXML(input);
        } catch (Exception e) {
            throw new FileNotFoundException(filePath);
        }
    }
}
