/**
 * 
 */
package net.sf.gilead.pojo.gwt.basic;

import java.io.Serializable;

import net.sf.gilead.pojo.gwt.IGwtSerializableParameter;

/**
 * Character parameter.
 * 
 * @author bruno.marchesson
 */
public class CharacterParameter implements IGwtSerializableParameter, Serializable {
	// ----
	// Attributes
	// ----
	/**
	 * Serialization ID.
	 */
	private static final long serialVersionUID = 2165631776081297493L;

	/**
	 * The underlying value.
	 */
	private Character value;

	// ----
	// Getter and setter
	// ----
	/**
	 * Change value.
	 */
	public void setUnderlyingValue(Character value) {
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
	public CharacterParameter(Character value) {
		this.value = value;
	}

	/**
	 * Empty constructor (needed by GWT)
	 */
	public CharacterParameter() {}
}
