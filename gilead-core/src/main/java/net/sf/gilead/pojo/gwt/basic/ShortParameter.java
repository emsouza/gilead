package net.sf.gilead.pojo.gwt.basic;

import net.sf.gilead.pojo.gwt.IGwtSerializableParameter;

/**
 * Short parameter.
 *
 * @author bruno.marchesson
 */
public class ShortParameter implements IGwtSerializableParameter {

    private static final long serialVersionUID = 2165631776081297491L;

    /**
     * The underlying value.
     */
    private Short value;

    /**
     * Empty constructor (needed by GWT)
     */
    public ShortParameter() {}

    /**
     * Constructor.
     */
    public ShortParameter(Short value) {
        this.value = value;
    }

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
}
