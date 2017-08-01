/**
 *
 */
package net.sf.gilead.pojo.gwt;

/**
 * String serialized version of a parameter. Use to end proxy information that is not a collection or a basic type.
 *
 * @author bruno.marchesson
 */
public class SerializedParameter implements IGwtSerializableParameter {

    private static final long serialVersionUID = 2165631776081297490L;

    /**
     * The underlying value.
     */
    private String value;

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

    /**
     * Constructor.
     */
    public SerializedParameter(String value) {
        this.value = value;
    }

    /**
     * Empty constructor (needed by GWT)
     */
    public SerializedParameter() {}
}
