package net.sf.gilead.proxy;

import net.sf.gilead.proxy.xml.AdditionalCode;

/**
 * Interface of the server proxy generator
 * 
 * @author bruno.marchesson
 */
public interface IServerProxyGenerator {

    /**
     * Generate a proxy the argument class
     * 
     * @param className
     */
    Class<?> generateProxyFor(Class<?> superClass, AdditionalCode additionalCode);
}