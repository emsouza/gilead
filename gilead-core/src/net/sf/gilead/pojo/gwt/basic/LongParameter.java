/**
 *
 */
package net.sf.gilead.pojo.gwt.basic;

import net.sf.gilead.pojo.gwt.IGwtSerializableParameter;

/**
 * Long parameter.
 *
 * @author bruno.marchesson
 */
public class LongParameter implements IGwtSerializableParameter {

    private static final long serialVersionUID = 2165631776081297492L;

    /**
     * The underlying value.
     */
    private Long value;

    /**
     * Change value.
     */
    public void setUnderlyingValue(Long value) {
        this.value = value;
    }

    /**
     * @return the underlying value
     */
    @Override
    public Object getUnderlyingValue() {
        return this.value;
    }

    /**
     * Constructor.
     */
    public LongParameter(Long value) {
        this.value = value;
    }

    /**
     * Empty constructor (needed by GWT)
     */
    public LongParameter() {}
}
