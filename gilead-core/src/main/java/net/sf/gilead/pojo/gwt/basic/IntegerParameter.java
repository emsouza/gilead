package net.sf.gilead.pojo.gwt.basic;

import net.sf.gilead.pojo.gwt.IGwtSerializableParameter;

/**
 * Integer parameter.
 *
 * @author bruno.marchesson
 */
public class IntegerParameter implements IGwtSerializableParameter {

    private static final long serialVersionUID = 2165631776081297491L;

    /**
     * The underlying value.
     */
    private Integer value;

    /**
     * Empty constructor (needed by GWT)
     */
    public IntegerParameter() {}

    /**
     * Constructor.
     */
    public IntegerParameter(Integer value) {
        this.value = value;
    }

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
}
