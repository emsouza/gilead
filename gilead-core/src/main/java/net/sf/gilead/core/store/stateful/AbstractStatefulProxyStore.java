package net.sf.gilead.core.store.stateful;

import java.io.Serializable;
import java.util.Map;

import net.sf.gilead.core.PersistenceUtil;
import net.sf.gilead.core.store.ProxyStore;
import net.sf.gilead.exception.NotPersistentObjectException;
import net.sf.gilead.exception.TransientObjectException;

/**
 * Abstract class for stateful proxy store.
 *
 * @author bruno.marchesson
 */
public abstract class AbstractStatefulProxyStore implements ProxyStore {

    /**
     * The associated persistence util
     */
    protected PersistenceUtil persistenceUtil;

    /**
     * @return the persistence Util implementation
     */
    public PersistenceUtil getPersistenceUtil() {
        return persistenceUtil;
    }

    /**
     * @param persistenceUtil the persistence Util to set
     */
    public void setPersistenceUtil(PersistenceUtil persistenceUtil) {
        this.persistenceUtil = persistenceUtil;
    }

    @Override
    public void storeProxyInformations(Object cloneBean, Object persistentBean, String property, Map<String, Serializable> proxyInformations) {
        Serializable id = UniqueNameGenerator.getUniqueId(persistenceUtil, persistentBean);
        store(computeKey(cloneBean, id, property), proxyInformations);
    }

    @Override
    public Map<String, Serializable> getProxyInformations(Object pojo, String property) {
        try {
            return get(computeKey(pojo, property));
        } catch (TransientObjectException ex) {
            return null;
        } catch (NotPersistentObjectException e) {
            return null;
        }
    }

    @Override
    public void removeProxyInformations(Object pojo, String property) {
        delete(computeKey(pojo, property));
    }

    /**
     * Clean up the proxy store after a complete serialization process
     */
    @Override
    public void cleanUp() {}

    /**
     * Store the value in the map.
     */
    public abstract void store(String key, Map<String, Serializable> proxyInformation);

    /**
     * Get the proxy informations associated with the key
     *
     * @return the value if found, null otherwise
     */
    public abstract Map<String, Serializable> get(String key);

    /**
     * Delete the key from the underlying storage
     *
     * @param key
     */
    public abstract void delete(String key);

    /**
     * Compute the hashmap key
     *
     * @param pojo
     * @param property
     * @return
     */
    public String computeKey(Class<?> pojoClass, Serializable id, String property) {
        pojoClass = persistenceUtil.getUnenhancedClass(pojoClass);
        return UniqueNameGenerator.generateUniqueName(id, pojoClass) + '.' + property;
    }

    /**
     * Compute the hashmap key
     *
     * @param pojo
     * @param property
     * @return
     */
    protected String computeKey(Object pojo, Serializable id, String property) {
        return computeKey(pojo.getClass(), id, property);
    }

    /**
     * Compute the hashmap key
     *
     * @param pojo
     * @param property
     * @return
     */
    protected String computeKey(Object pojo, String property) {
        return UniqueNameGenerator.generateUniqueName(persistenceUtil, pojo) + '.' + property;
    }
}
