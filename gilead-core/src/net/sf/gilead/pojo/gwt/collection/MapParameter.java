/**
 *
 */
package net.sf.gilead.pojo.gwt.collection;

import java.util.Map;

import net.sf.gilead.pojo.gwt.IGwtSerializableParameter;

/**
 * Map parameter.
 *
 * @author bruno.marchesson
 */
public class MapParameter implements IGwtSerializableParameter {

    private static final long serialVersionUID = 2165631776081297490L;

    /**
     * The underlying value.
     */
    private Map<IGwtSerializableParameter, IGwtSerializableParameter> value;

    /**
     * Change value.
     */
    public void setUnderlyingValue(Map<IGwtSerializableParameter, IGwtSerializableParameter> value) {
        this.value = value;
    }

    /**
     * @return the undelying value
     */
    @Override
    public Object getUnderlyingValue() {
        return this.value;
    }

    /**
     * Constructor.
     */
    public MapParameter(Map<IGwtSerializableParameter, IGwtSerializableParameter> value) {
        this.value = value;
    }

    /**
     * Empty constructor (needed by GWT)
     */
    public MapParameter() {}
}
