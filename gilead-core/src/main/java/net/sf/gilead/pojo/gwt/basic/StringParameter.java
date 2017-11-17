package net.sf.gilead.pojo.gwt.basic;

import net.sf.gilead.pojo.gwt.IGwtSerializableParameter;

/**
 * String parameter.
 *
 * @author bruno.marchesson
 */
public class StringParameter implements IGwtSerializableParameter {

    private static final long serialVersionUID = 2165631776081297490L;

    /**
     * The underlying value.
     */
    private String value;

    /**
     * Empty constructor (needed by GWT)
     */
    public StringParameter() {}

    /**
     * Constructor.
     */
    public StringParameter(String value) {
        this.value = value;
    }

    /**
     * Change value.
     */
    public void setUnderlyingValue(String value) {
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
