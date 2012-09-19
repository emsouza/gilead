/**
 * 
 */
package net.sf.gilead.exception;

/**
 * Exception thrown when the target class from the class mapper is not assignable to the source one
 * 
 * @author bruno.marchesson
 */
public class NotAssignableException extends RuntimeException {
	// ----
	// Attributes
	// ----
	/**
	 * Serialisation ID
	 */
	private static final long serialVersionUID = -6720555954758348687L;

	/**
	 * The source class
	 */
	private Class<?> sourceClass;

	/**
	 * The target class
	 */
	private Class<?> targetClass;

	// ----
	// Properties
	// ----
	/**
	 * @return the sourceClass
	 */
	public Class<?> getSourceClass() {
		return sourceClass;
	}

	/**
	 * @return the targetClass
	 */
	public Class<?> getTargetClass() {
		return targetClass;
	}

	// ----
	// Constructor
	// ----
	/**
	 * Constructor
	 */
	public NotAssignableException(Class<?> sourceClass, Class<?> targetClass) {
		this.sourceClass = sourceClass;
		this.targetClass = targetClass;
	}
}
