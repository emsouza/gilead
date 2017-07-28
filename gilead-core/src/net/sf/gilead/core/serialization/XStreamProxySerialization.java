/**
 *
 */
package net.sf.gilead.core.serialization;

import com.thoughtworks.xstream.XStream;

import java.io.Serializable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * XStream Serialization strategy. It serializes Serializable instances to String using XStream and deserializes them
 * when back. (needed for proxy informations, since GWT does not like Serializable type in Map<String, Serializable>)
 *
 * @author bruno.marchesson
 */
public class XStreamProxySerialization implements IProxySerialization {

    private static final Logger LOGGER = LoggerFactory.getLogger(XStreamProxySerialization.class);

    // ----
    // Attributes
    // ----
    /**
     * The XStream facade
     */
    private XStream _xstream;

    // -------------------------------------------------------------------------
    //
    // Constructor
    //
    // -------------------------------------------------------------------------
    /**
     * Constructor.
     */
    public XStreamProxySerialization() {
        _xstream = new XStream();
        _xstream.registerConverter(new SerializableIdConverter(_xstream));
    }

    // -------------------------------------------------------------------------
    //
    // Public interface
    //
    // -------------------------------------------------------------------------
    /**
     * Convert Serializable to bytes.
     */
    @Override
    public Object serialize(Serializable serializable) {
        LOGGER.trace("Serialization of " + serializable);
        // Precondition checking
        //
        if (serializable == null) {
            return null;
        }

        // Serialize to bytes and encapsulate into string
        //
        return _xstream.toXML(serializable);
    }

    /**
     * Regenerate Serializable from String.
     */
    @Override
    public Serializable unserialize(Object object) {
        // Precondition checking
        //
        if (object == null) {
            return null;
        }

        if (object instanceof String == false) {
            throw new RuntimeException("Cannot unserialize object : " + object + " (was expecting a String)");
        }

        String string = (String) object;
        LOGGER.trace("Unserialization of " + string);

        // String checking
        //
        if (string.length() == 0) {
            return null;
        }

        // Convert back to bytes and Serializable
        //
        return (Serializable) _xstream.fromXML(string);
    }
}
