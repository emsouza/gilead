package net.sf.gilead.pojo.gwt.basic;

import net.sf.gilead.pojo.gwt.IGwtSerializableParameter;

/**
 * Character parameter.
 *
 * @author bruno.marchesson
 */
public class CharacterParameter implements IGwtSerializableParameter {

    private static final long serialVersionUID = 2165631776081297493L;

    /**
     * The underlying value.
     */
    private Character value;

    /**
     * Empty constructor (needed by GWT)
     */
    public CharacterParameter() {}

    /**
     * Constructor.
     */
    public CharacterParameter(Character value) {
        this.value = value;
    }

    /**
     * Change value.
     */
    public void setUnderlyingValue(Character value) {
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
