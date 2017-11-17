package net.sf.gilead.pojo.base;

import net.sf.gilead.pojo.gwt.IGwtSerializableParameter;

/**
 * Persistence info handler interface. All the persistent POJO must implements this interface in stateless mode.
 * 
 * @author bruno.marchesson
 */
public interface ILightEntity extends IGwtSerializableParameter {

    public static final String INITIALISED = "initialized";

    /**
     * Add proxy information for the argument property.
     * 
     * @param property the property name
     * @param proxyInformation map of proxy informations (string serialized form)
     */
    void addProxyInformation(String property, Object proxyInfo);

    /**
     * Remove a property proxy information
     */
    void removeProxyInformation(String property);

    /**
     * Get proxy information for the argument property
     * 
     * @param property the property name
     * @return the proxy informations for the property _ string serialized form(can be null)
     */
    public Object getProxyInformation(String property);

    /**
     * Set the initialization state of a property.
     * 
     * @param property
     * @param initialised
     */
    void setInitialized(String property, boolean initialised);

    /**
     * @param property
     * @return if the property was initialised, false otherwise
     */
    boolean isInitialized(String property);

    /**
     * Debug method : write the declared proxy information
     * 
     * @return a human readable description of proxy information
     */
    String getDebugString();
}