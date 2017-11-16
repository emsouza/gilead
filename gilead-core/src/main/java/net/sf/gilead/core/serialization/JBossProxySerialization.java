/**
 *
 */
package net.sf.gilead.core.serialization;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Arrays;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.sf.gilead.exception.ConvertorException;

/**
 * Serialization manager singleton. It use JBoss serialization library to convert Serializable to simple byte array and
 * deserializes them when back. (needed for proxy informations, since GWT does not like Serializable type in Map<String,
 * Serializable>)
 *
 * @author bruno.marchesson
 */
public class JBossProxySerialization implements ProxySerialization {

    private static final Logger LOGGER = LoggerFactory.getLogger(JBossProxySerialization.class);

    // -------------------------------------------------------------------------
    //
    // Public interface
    //
    // -------------------------------------------------------------------------
    /*
     * (non-Javadoc)
     * @see net.sf.gilead.core.serialization.IProxySerialization#serializeToBytes (java.io.Serializable)
     */
    @Override
    public Object serialize(Serializable serializable) {
        LOGGER.trace("Serialization of " + serializable);
        // Precondition checking
        //
        if (serializable == null) {
            return null;
        }

        // Serialize using Java mechanism
        //
        try {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            ObjectOutputStream oos = new ObjectOutputStream(out);
            oos.writeObject(serializable);
            oos.close();

            return Base64.encodeToString(out.toByteArray(), false);
        } catch (IOException ex) {
            throw new ConvertorException("Error converting Serializable", ex);
        }
    }

    /*
     * (non-Javadoc)
     * @see net.sf.gilead.core.serialization.IProxySerialization#unserializeFromBytes (byte[])
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

        byte[] bytes = Base64.decodeFast((String) object);
        LOGGER.trace("Unserialization of " + Arrays.toString(bytes));

        // Precondition checking
        //
        if ((bytes == null) || (bytes.length == 0)) {
            return null;
        }

        // Convert back to Serializable
        //
        try {
            ByteArrayInputStream in = new ByteArrayInputStream(bytes);
            ObjectInputStream ois = new ObjectInputStream(in);
            return (Serializable) ois.readObject();
        } catch (Exception e) {
            throw new ConvertorException("Error converting Serializable", e);
        }

    }
}
