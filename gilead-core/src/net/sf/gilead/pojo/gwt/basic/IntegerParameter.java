/**
 *
 */
package net.sf.gilead.pojo.gwt.basic;

import net.sf.gilead.pojo.gwt.IGwtSerializableParameter;

/**
 * Integer parameter.
 *
 * @author bruno.marchesson
 */
public class IntegerParameter implements IGwtSerializableParameter {
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
    private Integer value;

    // ----
    // Getter and setter
    // ----
    /**
     * Change value.
     */
    public void setUnderlyingValue(Integer value) {
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
    public IntegerParameter(Integer value) {
        this.value = value;
    }

    /**
     * Empty constructor (needed by GWT)
     */
    public IntegerParameter() {}
}
