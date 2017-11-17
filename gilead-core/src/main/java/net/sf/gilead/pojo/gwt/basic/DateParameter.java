package net.sf.gilead.pojo.gwt.basic;

import java.util.Date;

import net.sf.gilead.pojo.gwt.IGwtSerializableParameter;

/**
 * Date parameter.
 *
 * @author bruno.marchesson
 */
public class DateParameter implements IGwtSerializableParameter {

    private static final long serialVersionUID = 2165631776081297493L;

    /**
     * The underlying value.
     */
    private Date value;

    /**
     * Empty constructor (needed by GWT)
     */
    public DateParameter() {}

    /**
     * Constructor.
     */
    public DateParameter(Date value) {
        this.value = value;
    }

    /**
     * Change value.
     */
    public void setUnderlyingValue(Date value) {
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
