/**
 * 
 */
package net.sf.gilead.exception;

/**
 * Exception when trying to clone or merge an object not persisted with Persistence engine
 * 
 * @author bruno.marchesson
 */
public class NotPersistentObjectException extends RuntimeException {
	// ----
	// Attribute
	// ----
	/**
	 * Serialisation ID
	 */
	private static final long serialVersionUID = 3274347637647294793L;

	/**
	 * The exception object
	 */
	private Object _object;

	// ----
	// Property
	// ----
	/**
	 * @return the object
	 */
	public final Object getObject() {
		return _object;
	}

	// ----
	// Constructor
	// ----
	/**
	 * Base constructor
	 */
	public NotPersistentObjectException(Object object) {
		_object = object;
	}

	/**
	 * Get formatted message
	 */
	@Override
	public String getMessage() {
		StringBuilder stringBuilder = new StringBuilder();

		stringBuilder.append("Not Persistent Object : ");
		// stringBuilder.append(_object);
		if (_object != null) {
			stringBuilder.append(" [Class is ");
			stringBuilder.append(_object.getClass().getName());
			stringBuilder.append("]");
		}
		return stringBuilder.toString();
	}
}
