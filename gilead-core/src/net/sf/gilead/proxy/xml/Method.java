/**
 *
 */
package net.sf.gilead.proxy.xml;

import java.util.ArrayList;
import java.util.List;

/**
 * Method description
 *
 * @author bruno.marchesson
 */
public class Method {

    /**
     * Name of the method
     */
    private String name;

    /**
     * Visibility of the method
     */
    private String visibility;

    /**
     * Return type of the method
     */
    private String returnType;

    /**
     * Return collection type of the method
     */
    private String returnCollectionType;

    /**
     * The method Javadoc
     */
    private String javadoc;

    /**
     * Parameters list
     */
    private List<Parameter> parameters;

    /**
     * The method code
     */
    private String code;

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
     * @return the returnType
     */
    public String getReturnType() {
        return returnType;
    }

    /**
     * @param returnType the returnType to set
     */
    public void setReturnType(String returnType) {
        this.returnType = returnType;
    }

    /**
     * @return the returnCollectionType
     */
    public String getReturnCollectionType() {
        return returnCollectionType;
    }

    /**
     * @param returnCollectionType the returnCollectionType to set
     */
    public void setReturnCollectionType(String returnCollectionType) {
        this.returnCollectionType = returnCollectionType;
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
     * @return the parameters
     */
    public List<Parameter> getParameters() {
        return parameters;
    }

    /**
     * @param parameters the parameters to set
     */
    public void setParameters(List<Parameter> parameters) {
        this.parameters = parameters;
    }

    /**
     * @return the code
     */
    public String getCode() {
        return code;
    }

    /**
     * @param code the code to set
     */
    public void setCode(String code) {
        this.code = code;
    }

    /**
     * Add the argument parameter
     * 
     * @param parameter
     */
    public void addParameter(Parameter parameter) {
        if (parameters == null) {
            parameters = new ArrayList<Parameter>();
        }
        parameters.add(parameter);
    }

    /**
     * Compute Java5 syntax signature
     */
    public String computeJava5Signature() {
        StringBuffer result = new StringBuffer();

        result.append(visibility);
        result.append(" ");
        result.append(returnType);
        if (returnCollectionType != null) {
            result.append('<');
            result.append(returnCollectionType);
            result.append('>');
        }
        result.append(" ");
        result.append(name);
        result.append("(");

        if ((parameters != null) && (parameters.isEmpty() == false)) {
            // Add parameters
            boolean firstParameter = true;
            for (Parameter parameter : parameters) {
                if (firstParameter == false) {
                    result.append(", ");
                } else {
                    firstParameter = false;
                }

                result.append(parameter.toJava5String());
            }
        }
        result.append(")");

        return result.toString();
    }

    /**
     * Compute Java14 syntax signature
     */
    public String computeJava14Signature() {
        StringBuffer result = new StringBuffer();

        result.append(visibility);
        result.append(" ");
        result.append(returnType);
        result.append(" ");
        result.append(name);
        result.append("(");

        if ((parameters != null) && (parameters.isEmpty() == false)) {
            // Add parameters
            boolean firstParameter = true;
            for (Parameter parameter : parameters) {
                if (firstParameter == false) {
                    result.append(", ");
                } else {
                    firstParameter = false;
                }

                result.append(parameter.toJava14String());
            }
        }
        result.append(")");

        return result.toString();
    }
}
