package net.sf.gilead.core.store.stateful;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * In Memory Proxy Information Store. This class stores POJO in a simple hashmap
 *
 * @author bruno.marchesson
 */
public class InMemoryProxyStore extends AbstractStatefulProxyStore {

    /**
     * The store hashmap
     */
    protected Map<String, Map<String, Serializable>> map = new HashMap<>();

    @Override
    public void delete(String key) {
        map.remove(key);
    }

    @Override
    public Map<String, Serializable> get(String key) {
        return map.get(key);
    }

    @Override
    public void store(String key, Map<String, Serializable> proxyInformation) {
        map.put(key, proxyInformation);
    }
}
