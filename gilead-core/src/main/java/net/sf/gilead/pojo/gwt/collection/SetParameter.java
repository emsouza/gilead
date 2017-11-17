package net.sf.gilead.pojo.gwt.collection;

import java.util.Set;

import net.sf.gilead.pojo.gwt.IGwtSerializableParameter;

/**
 * Set parameter.
 *
 * @author bruno.marchesson
 */
public class SetParameter implements IGwtSerializableParameter {

    private static final long serialVersionUID = 2165631776081297490L;

    /**
     * The underlying value.
     */
    private Set<IGwtSerializableParameter> value;

    /**
     * Empty constructor (needed by GWT)
     */
    public SetParameter() {}

    /**
     * Constructor.
     */
    public SetParameter(Set<IGwtSerializableParameter> value) {
        this.value = value;
    }

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
}
