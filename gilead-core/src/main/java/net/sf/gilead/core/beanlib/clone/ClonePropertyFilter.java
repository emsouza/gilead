package net.sf.gilead.core.beanlib.clone;

import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.sf.beanlib.spi.DetailedPropertyFilter;
import net.sf.gilead.core.PersistenceUtil;
import net.sf.gilead.core.beanlib.CloneAndMergeConstants;
import net.sf.gilead.core.store.ProxyStore;
import net.sf.gilead.pojo.base.ILightEntity;
import net.sf.gilead.util.IntrospectionHelper;

/**
 * Populatable for Hibernate lazy handling
 *
 * @author bruno.marchesson
 */
public class ClonePropertyFilter implements DetailedPropertyFilter {

    private static final Logger LOGGER = LoggerFactory.getLogger(ClonePropertyFilter.class);

    /**
     * The associated persistence utils
     */
    private PersistenceUtil persistenceUtil;

    /**
     * The proxy informations store.
     */
    private ProxyStore proxyStore;

    /**
     * Constructor
     */
    public ClonePropertyFilter(PersistenceUtil persistenceUtil, ProxyStore proxyStore) {
        setPersistenceUtil(persistenceUtil);
        setProxyStore(proxyStore);
    }

    /**
     * @return the persistence Util implementation to use
     */
    public PersistenceUtil getPersistenceUtil() {
        return persistenceUtil;
    }

    /**
     * @param util the persistenceUtil to set
     */
    public void setPersistenceUtil(PersistenceUtil persistenceUtil) {
        this.persistenceUtil = persistenceUtil;
    }

    /**
     * @return the used proxy Store
     */
    public ProxyStore getProxyStore() {
        return proxyStore;
    }

    /**
     * @param store the proxy store to set
     */
    public void setProxyStore(ProxyStore proxyStore) {
        this.proxyStore = proxyStore;
    }

    @Override
    public boolean propagate(String propertyName, Object fromBean, Method readerMethod, Object toBean, Method setterMethod) {
        // Is the property lazy loaded ?
        //
        try {
            if ((CloneAndMergeConstants.PROXY_INFORMATIONS.equals(propertyName) == true)
                    || (CloneAndMergeConstants.INITIALIZATION_MAP.equals(propertyName) == true)) {
                return false;
            }

            // Get from value
            Object fromValue = readPropertyValue(fromBean, readerMethod.getName());
            if (fromValue == null) {
                return true;
            }

            boolean isPersistentCollection = persistenceUtil.isPersistentCollection(fromValue.getClass());
            boolean isPersistentMap = persistenceUtil.isPersistentMap(fromValue.getClass());

            // Lazy handling
            //
            if (persistenceUtil.isInitialized(fromValue) == false) {
                // Lazy property !
                LOGGER.trace(fromBean.toString() + "." + propertyName + " --> not initialized");

                // Get proxy informations
                Map<String, Serializable> proxyInformations;
                if (isPersistentMap) {
                    proxyInformations = persistenceUtil.serializePersistentMap((Map<?, ?>) fromValue);
                } else if (isPersistentCollection) {
                    proxyInformations = persistenceUtil.serializePersistentCollection((Collection<?>) fromValue);
                } else {
                    proxyInformations = persistenceUtil.serializeEntityProxy(fromValue);
                }

                // Add lazy property
                proxyInformations.put(ILightEntity.INITIALISED, false);

                // Store proxy information
                proxyStore.storeProxyInformations(toBean, fromBean, propertyName, proxyInformations);

                return false;
            } else if (isPersistentMap) {
                // Persistent map handling
                Map<String, Serializable> proxyInformations = persistenceUtil.serializePersistentMap((Map<?, ?>) fromValue);

                proxyStore.storeProxyInformations(toBean, fromBean, propertyName, proxyInformations);
            } else if (isPersistentCollection) {
                // Persistent collection handling
                Map<String, Serializable> proxyInformations = persistenceUtil.serializePersistentCollection((Collection<?>) fromValue);

                proxyStore.storeProxyInformations(toBean, fromBean, propertyName, proxyInformations);
            }

            return true;
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    /**
     * Read a property value, even if it has a private getter
     */
    private Object readPropertyValue(Object bean, String propertyGetter)
            throws SecurityException, NoSuchMethodException, IllegalArgumentException, IllegalAccessException, InvocationTargetException {
        Method readMethod = IntrospectionHelper.getRecursiveDeclaredMethod(bean.getClass(), propertyGetter, (Class[]) null);
        readMethod.setAccessible(true);
        return readMethod.invoke(bean, (Object[]) null);
    }
}
