package net.sf.gilead.core.serialization;

import java.io.Serializable;

/**
 * Id / Class structure for proxy information collection handling
 *
 * @author bruno.marchesson
 */
public class SerializableId implements Serializable {

    private static final long serialVersionUID = -4365286012503534L;

    /**
     * The underlying id
     */
    protected Serializable id;

    /**
     * Workaround for non persistent and transient values Contains values for Number and String non persistent items
     * Contains hashcode for other
     */
    protected String value;

    /**
     * The associated entity name
     */
    protected String entityName;

    /**
     * @return the id
     */
    public Serializable getId() {
        return id;
    }

    /**
     * @param id the id to set
     */
    public void setId(Serializable id) {
        this.id = id;
    }

    /**
     * @return the value
     */
    public String getValue() {
        return value;
    }

    /**
     * @param value the value to set
     */
    public void setValue(String value) {
        this.value = value;
    }

    /**
     * @return the entity Name
     */
    public String getEntityName() {
        return entityName;
    }

    /**
     * @param entityName the entity Name to set
     */
    public void setEntityName(String entityName) {
        this.entityName = entityName;
    }

    @Override
    public boolean equals(Object other) {
        if ((other == null) || (other instanceof SerializableId == false)) {
            return false;
        }

        // Check entity name
        if (entityName == null) {
            return (((SerializableId) other).entityName == null);
        } else if (entityName.equals(((SerializableId) other).entityName) == false) {
            return false;
        }

        // Check id or hashcode
        if (id == null) {
            if (value != null) {
                return value.equals(((SerializableId) other).value);
            } else {
                return ((SerializableId) other).value == null;
            }
        } else {
            return id.equals(((SerializableId) other).id);
        }
    }

    @Override
    public String toString() {
        StringBuilder result = new StringBuilder();
        result.append("[");
        result.append(entityName);
        result.append("/");
        if (id != null) {
            result.append("ID:");
            result.append(id);
        } else {
            result.append("hashcode:");
            result.append(value);
        }
        result.append("]");

        return result.toString();
    }
}
