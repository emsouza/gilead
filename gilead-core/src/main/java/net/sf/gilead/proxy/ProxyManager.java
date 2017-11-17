package net.sf.gilead.proxy;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import net.sf.gilead.proxy.xml.AdditionalCode;

/**
 * Proxy manager
 *
 * @author bruno.marchesson
 */
public class ProxyManager {

    /**
     * Addition code for Java 5 Light entity
     */
    public static final String JAVA_5_LAZY_POJO = "net/sf/gilead/proxy/xml/LightEntity.java5.xml";

    /**
     * Unique instance of the singleton
     */
    private static ProxyManager _instance = null;

    /**
     * Proxy generator
     */
    private IServerProxyGenerator _proxyGenerator;

    /**
     * Map of the generated proxy
     */
    private Map<Class<?>, Class<?>> _generatedProxyMap;

    /**
     * @return the instance
     */
    public static synchronized ProxyManager getInstance() {
        if (_instance == null) {
            _instance = new ProxyManager();
        }
        return _instance;
    }

    /**
     * @return the proxy Generator
     */
    public IServerProxyGenerator getProxyGenerator() {
        return _proxyGenerator;
    }

    /**
     * @param generator the proxy Generator to set
     */
    public void setProxyGenerator(IServerProxyGenerator generator) {
        _proxyGenerator = generator;
    }

    /**
     * Constructor
     */
    protected ProxyManager() {
        _generatedProxyMap = new HashMap<>();
        // Default proxy generator
        _proxyGenerator = new JavassistProxyGenerator();
    }

    /**
     * Generate a proxy class
     *
     * @return the associated proxy class if found, null otherwise
     */
    public synchronized Class<?> generateProxyClass(Class<?> clazz, AdditionalCode additionalCode) {
        Class<?> proxyClass = _generatedProxyMap.get(clazz);
        if (proxyClass == null) {
            // Generate proxy
            proxyClass = _proxyGenerator.generateProxyFor(clazz, additionalCode);
            _generatedProxyMap.put(clazz, proxyClass);
        }

        return proxyClass;
    }

    /**
     * @return the associated proxy class if found, null otherwise
     */
    public Class<?> getProxyClass(Class<?> clazz) {
        return _generatedProxyMap.get(clazz);
    }

    /**
     * @return the associated source class if found, null otherwise
     */
    public Class<?> getSourceClass(Class<?> proxyClass) {
        // Iterate over proxy map
        for (Entry<Class<?>, Class<?>> entry : _generatedProxyMap.entrySet()) {
            if (proxyClass.equals(entry.getValue())) {
                return entry.getKey();
            }
        }

        // Source class not found
        return null;
    }

    /**
     * Clear generated proxy classes (for multiple tests run)
     */
    public void clear() {
        _generatedProxyMap.clear();
    }
}
