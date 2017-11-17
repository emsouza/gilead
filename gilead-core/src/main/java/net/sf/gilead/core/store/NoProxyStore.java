package net.sf.gilead.core.store;

import java.io.Serializable;
import java.util.Map;

/**
 * Empty proxy store. The proxy informations is not stored, so this store can be used for cloning entities only (trying
 * to merge them throws an exception).
 * 
 * @author bruno.marchesson
 */
public class NoProxyStore implements IProxyStore {

    @Override
    public void storeProxyInformations(Object cloneBean, Object persistentBean, String property, Map<String, Serializable> proxyInformations) {}

    @Override
    public void removeProxyInformations(Object pojo, String property) {}

    @Override
    public Map<String, Serializable> getProxyInformations(Object pojo, String property) {
        // Must no be called !
        throw new RuntimeException("Cannot merge entities with NoProxyStore !");
    }

    /**
     * Clean up the proxy store after a complete serialization process
     */
    @Override
    public void cleanUp() {}
}
