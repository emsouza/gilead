/**
 * 
 */
package net.sf.gilead.pojo.gwt.basic;

import java.io.Serializable;

import net.sf.gilead.pojo.gwt.IGwtSerializableParameter;

/**
 * Short parameter.
 * 
 * @author bruno.marchesson
 */
public class ShortParameter implements IGwtSerializableParameter, Serializable {
	// ----
	// Attributes
	// ----
	/**
	 * Serialization ID.
	 */
	private static final long serialVersionUID = 2165631776081297491L;

	/**
	 * The underlying value.
	 */
	private Short value;

	// ----
	// Getter and setter
	// ----
	/**
	 * Change value.
	 */
	public void setUnderlyingValue(Short value) {
		this.value = value;
	}

	/**
	 * @return the underlying value
	 */
	@Override
	public Object getUnderlyingValue() {
		return this.value;
	}

	// ----
	// Constructor
	// ----
	/**
	 * Constructor.
	 */
	public ShortParameter(Short value) {
		this.value = value;
	}

	/**
	 * Empty constructor (needed by GWT)
	 */
	public ShortParameter() {}
}
