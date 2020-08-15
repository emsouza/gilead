package net.sf.gilead.proxy.xml;

import java.util.ArrayList;
import java.util.List;

/**
 * Constructor description
 *
 * @author bruno.marchesson
 */
public class Constructor {

    /**
     * Visibility of the method
     */
    private String visibility;

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
            parameters = new ArrayList<>();
        }
        parameters.add(parameter);
    }

    /**
     * Compute Java5 syntax signature
     */
    public String computeJava5Signature(String className) {
        StringBuffer result = new StringBuffer();

        result.append(visibility);
        result.append(" ");
        result.append(className);
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
}
