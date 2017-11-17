package net.sf.gilead.pojo.gwt.collection;

import java.util.List;

import net.sf.gilead.pojo.gwt.IGwtSerializableParameter;

/**
 * List parameter.
 *
 * @author bruno.marchesson
 */
public class ListParameter implements IGwtSerializableParameter {

    private static final long serialVersionUID = 2165631776081297490L;

    /**
     * The underlying value.
     */
    private List<IGwtSerializableParameter> value;

    /**
     * Empty constructor (needed by GWT)
     */
    public ListParameter() {}

    /**
     * Constructor.
     */
    public ListParameter(List<IGwtSerializableParameter> value) {
        this.value = value;
    }

    /**
     * Change value.
     */
    public void setUnderlyingValue(List<IGwtSerializableParameter> value) {
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
