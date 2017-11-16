/**
 *
 */
package net.sf.gilead.pojo.gwt.basic;

import net.sf.gilead.pojo.gwt.IGwtSerializableParameter;

/**
 * Double parameter.
 *
 * @author bruno.marchesson
 */
public class DoubleParameter implements IGwtSerializableParameter {

    private static final long serialVersionUID = 2165631776081297493L;

    /**
     * The underlying value.
     */
    private Double value;

    /**
     * Change value.
     */
    public void setUnderlyingValue(Double value) {
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
    public DoubleParameter(Double value) {
        this.value = value;
    }

    /**
     * Empty constructor (needed by GWT)
     */
    public DoubleParameter() {}
}
