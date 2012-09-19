package net.sf.gilead.core.serialization;

import java.io.Serializable;

/**
 * Interface for proxy serialization strategy
 * 
 * @author bruno.marchesson
 */
public interface IProxySerialization {

	// -------------------------------------------------------------------------
	//
	// Public interface
	//
	// -------------------------------------------------------------------------
	/**
	 * Convert Serializable to target type.
	 */
	public abstract Object serialize(Serializable serializable);

	/**
	 * Regenerate Serializable from target type.
	 */
	public abstract Serializable unserialize(Object serialized);

}