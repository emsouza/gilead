/**
 * 
 */
package net.sf.gilead.pojo.gwt.collection;

import java.io.Serializable;
import java.util.Set;

import net.sf.gilead.pojo.gwt.IGwtSerializableParameter;

/**
 * Set parameter.
 * 
 * @author bruno.marchesson
 */
public class SetParameter implements IGwtSerializableParameter, Serializable {
	// ----
	// Attributes
	// ----
	/**
	 * Serialization ID.
	 */
	private static final long serialVersionUID = 2165631776081297490L;

	/**
	 * The underlying value.
	 */
	private Set<IGwtSerializableParameter> value;

	// ----
	// Getter and setter
	// ----
	/**
	 * Change value.
	 */
	public void setUnderlyingValue(Set<IGwtSerializableParameter> value) {
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
	public SetParameter(Set<IGwtSerializableParameter> value) {
		this.value = value;
	}

	/**
	 * Empty constructor (needed by GWT)
	 */
	public SetParameter() {}
}
