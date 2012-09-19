/**
 * 
 */
package net.sf.gilead.exception;

/**
 * Exception thrown when a component type is encountered. It inherits from Transient Object Exception because most of
 * the time it must be treated as transient.
 * 
 * @author bruno.marchesson
 */
public class ComponentTypeException extends TransientObjectException {

	/**
	 * Serialization ID
	 */
	private static final long serialVersionUID = 3340931714810616895L;

	/**
	 * Constructor
	 */
	public ComponentTypeException(Object object) {
		super(object);
	}

}
