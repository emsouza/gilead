package net.sf.gilead.core.serialization;

import java.io.Serializable;

/**
 * Interface for proxy serialization strategy
 * 
 * @author bruno.marchesson
 */
public interface IProxySerialization {

    /**
     * Convert Serializable to target type.
     */
    Object serialize(Serializable serializable);

    /**
     * Regenerate Serializable from target type.
     */
    Serializable unserialize(Object serialized);
}