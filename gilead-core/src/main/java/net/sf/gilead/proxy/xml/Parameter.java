package net.sf.gilead.proxy.xml;

/**
 * Method parameters
 * 
 * @author bruno.marchesson
 */
public class Parameter {

    /**
     * Name of the parameter
     */
    private String name;

    /**
     * Type of the parameter
     */
    private String type;

    /**
     * Type of the collection parameter
     */
    private String collectionType;

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
     * Convert the parameter to Java 14 syntax
     */
    public String toJava14String() {
        StringBuffer result = new StringBuffer();

        result.append(type);
        result.append(" ");
        result.append(name);

        return result.toString();
    }

    /**
     * Convert the parameter to Java 5 syntax
     */
    public String toJava5String() {
        StringBuffer result = new StringBuffer();

        result.append(type);
        if (collectionType != null) {
            result.append("<");
            result.append(collectionType);
            result.append(">");
        }
        result.append(" ");
        result.append(name);

        return result.toString();
    }
}
