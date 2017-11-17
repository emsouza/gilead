package net.sf.gilead.proxy.gwt;

import net.sf.gilead.proxy.ProxyManager;

/**
 * Proxy generator for GWT 1.5
 * 
 * @author bruno.marchesson
 */
public class Gwt15ProxyGenerator extends AbstractGwtProxyGenerator {

    /**
     * Constructor
     */
    public Gwt15ProxyGenerator() {
        super(ProxyManager.JAVA_5_LAZY_POJO);
    }
}
