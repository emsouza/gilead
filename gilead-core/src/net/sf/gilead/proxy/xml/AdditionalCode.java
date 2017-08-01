/**
 *
 */
package net.sf.gilead.proxy.xml;

import java.util.ArrayList;
import java.util.List;

/**
 * @author bruno.marchesson
 */
public class AdditionalCode {

    /**
     * Suffix for generated classes
     */
    private String suffix;

    /**
     * Implemented interface
     */
    private String implementedInterface;

    /**
     * List of additional attributes
     */
    private List<Attribute> attributes;

    /**
     * List of additional constructors
     */
    private List<Constructor> constructors;

    /**
     * List of additional methods
     */
    private List<Method> methods;

    /**
     * @return the suffix
     */
    public String getSuffix() {
        return suffix;
    }

    /**
     * @param suffix the suffix to set
     */
    public void setSuffix(String suffix) {
        this.suffix = suffix;
    }

    /**
     * @return the implementedInterfaces
     */
    public String getImplementedInterface() {
        return implementedInterface;
    }

    /**
     * @param implementedInterfaces the implementedInterfaces to set
     */
    public void setImplementedInterface(String implementedInterface) {
        this.implementedInterface = implementedInterface;
    }

    /**
     * @return the attributes
     */
    public List<Attribute> getAttributes() {
        return attributes;
    }

    /**
     * @param attributes the attributes to set
     */
    public void setAttributes(List<Attribute> attributes) {
        this.attributes = attributes;
    }

    /**
     * @return
     */
    public List<Constructor> getConstructors() {
        return constructors;
    }

    /**
     * @param constructors
     */
    public void setConstructors(List<Constructor> constructors) {
        this.constructors = constructors;
    }

    /**
     * @return the methods
     */
    public List<Method> getMethods() {
        return methods;
    }

    /**
     * @param methods the methods to set
     */
    public void setMethods(List<Method> methods) {
        this.methods = methods;
    }

    /**
     * Add a new attribute
     */
    public void addAttribute(Attribute attribute) {
        if (attributes == null) {
            attributes = new ArrayList<Attribute>();
        }
        attributes.add(attribute);
    }

    /**
     * Add a new method
     */
    public void addConstructor(Constructor constructor) {
        if (constructors == null) {
            constructors = new ArrayList<Constructor>();
        }
        constructors.add(constructor);
    }

    /**
     * Add a new method
     */
    public void addMethod(Method method) {
        if (methods == null) {
            methods = new ArrayList<Method>();
        }
        methods.add(method);
    }
}
