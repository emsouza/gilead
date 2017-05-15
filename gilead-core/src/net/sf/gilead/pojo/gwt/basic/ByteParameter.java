/**
 *
 */
package net.sf.gilead.pojo.gwt.basic;

import net.sf.gilead.pojo.gwt.IGwtSerializableParameter;

/**
 * Byte parameter.
 *
 * @author bruno.marchesson
 */
public class ByteParameter implements IGwtSerializableParameter {
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
    private Byte value;

    // ----
    // Getter and setter
    // ----
    /**
     * Change value.
     */
    public void setUnderlyingValue(Byte value) {
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
    public ByteParameter(Byte value) {
        this.value = value;
    }

    /**
     * Empty constructor (needed by GWT)
     */
    public ByteParameter() {}
}
