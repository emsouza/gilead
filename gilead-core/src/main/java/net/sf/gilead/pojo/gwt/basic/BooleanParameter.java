package net.sf.gilead.pojo.gwt.basic;

import net.sf.gilead.pojo.gwt.IGwtSerializableParameter;

/**
 * Boolean parameter.
 * 
 * @author bruno.marchesson
 */
public class BooleanParameter implements IGwtSerializableParameter {

    private static final long serialVersionUID = 2165631776081297493L;

    /**
     * The underlying value.
     */
    private Boolean value;

    /**
     * Empty constructor (needed by GWT)
     */
    public BooleanParameter() {}

    /**
     * Constructor.
     */
    public BooleanParameter(Boolean value) {
        this.value = value;
    }

    /**
     * Change value.
     */
    public void setUnderlyingValue(Boolean value) {
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
