/**
 *
 */
package net.sf.gilead.proxy.xml;

import org.apache.commons.lang3.StringUtils;

/**
 * Method description
 *
 * @author bruno.marchesson
 */
public class Attribute {
    // ----
    // Attributes
    // ---
    /**
     * Name of the attribute
     */
    private String name;

    /**
     * Visibility of the attribute
     */
    private String visibility;

    /**
     * Type of the attribute
     */
    private String type;

    /**
     * Type of the collection element
     */
    private String collectionType;

    /**
     * Default value of the attribute
     */
    private String defaultValue;

    /**
     * The attribute Javadoc
     */
    private String javadoc;

    // ----
    // Properties
    // ----
    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * @param name the name to set
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return the visibility
     */
    public String getVisibility() {
        return visibility;
    }

    /**
     * @param visibility the visibility to set
     */
    public void setVisibility(String visibility) {
        this.visibility = visibility;
    }

    /**
     * @return the javadoc
     */
    public String getJavadoc() {
        return javadoc;
    }

    /**
     * @param javadoc the javadoc to set
     */
    public void setJavadoc(String javadoc) {
        this.javadoc = javadoc;
    }

    /**
     * @return the type
     */
    public String getType() {
        return type;
    }

    /**
     * @param type the type to set
     */
    public void setType(String type) {
        this.type = type;
    }

    /**
     * @return the collectionType
     */
    public String getCollectionType() {
        return collectionType;
    }

    /**
     * @param collectionType the collectionType to set
     */
    public void setCollectionType(String collectionType) {
        this.collectionType = collectionType;
    }

    /**
     * @return the defaultValue
     */
    public String getDefaultValue() {
        return defaultValue;
    }

    /**
     * @param defaultValue the defaultValue to set
     */
    public void setDefaultValue(String defaultValue) {
        this.defaultValue = defaultValue;
    }

    // ----
    // Public interface
    // ----
    /**
     * Convert Attribute to Java5 syntax
     */
    public String toJava5String() {
        StringBuffer result = new StringBuffer();

        result.append(visibility);
        result.append(" ");
        result.append(type);
        if (collectionType != null) {
            result.append("<");
            result.append(collectionType);
            result.append(">");
        }
        result.append(" ");
        result.append(name);

        if (StringUtils.isEmpty(defaultValue) == false) {
            result.append(" = ");
            result.append(defaultValue);
        }

        result.append(";");

        return result.toString();
    }

    /**
     * Convert Attribute to Java 1.4 syntax
     */
    public String toJava14String() {
        StringBuffer result = new StringBuffer();

        result.append(visibility);
        result.append(" ");
        result.append(type);
        result.append(" ");
        result.append(name);

        if (StringUtils.isEmpty(defaultValue) == false) {
            result.append(" = ");
            result.append(defaultValue);
        }

        result.append(";");

        return result.toString();
    }
}
