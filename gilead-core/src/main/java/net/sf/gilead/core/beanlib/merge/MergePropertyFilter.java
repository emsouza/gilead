package net.sf.gilead.core.beanlib.merge;

import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.Map;

import net.sf.beanlib.spi.DetailedPropertyFilter;
import net.sf.gilead.core.PersistenceUtil;
import net.sf.gilead.core.annotations.AnnotationsManager;
import net.sf.gilead.core.beanlib.CloneAndMergeConstants;
import net.sf.gilead.core.store.ProxyStore;
import net.sf.gilead.pojo.base.ILightEntity;
import net.sf.gilead.util.IntrospectionHelper;

/**
 * Populatable for POJO with the lazy information of the Hibernate POJO This populatable is used to fill back an
 * Hibernate POJO from proxy information. Only the not lazy properties of the Hibernate POJO are filled. To be correct,
 * the clone and merge operations must be managed with the <b>same</b> Hibernate POJO, or a POJO with the <b>same</b>
 * fetching strategy
 * 
 * @author bruno.marchesson
 */
public class MergePropertyFilter implements DetailedPropertyFilter {

    /**
     * The associated persistence utils
     */
    private PersistenceUtil persistenceUtil;

    /**
     * The used Pojo store
     */
    private ProxyStore proxyStore;

    /**
     * Constructor
     */
    public MergePropertyFilter(PersistenceUtil persistenceUtil, ProxyStore proxyStore) {
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
     * @return the proxy store
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
    public boolean propagate(String propertyName, Object cloneBean, Method readerMethod, Object persistentBean, Method setterMethod) {
        // Always reset proxy information on stack
        BeanlibCache.setProxyInformations(null);

        try {
            // Precondition checking
            if ((CloneAndMergeConstants.PROXY_INFORMATIONS.equals(propertyName) == true)
                    || (CloneAndMergeConstants.INITIALIZATION_MAP.equals(propertyName) == true)) {
                return false;
            }

            // Get proxy informations
            Map<String, Serializable> proxyInformations = proxyStore.getProxyInformations(cloneBean, propertyName);

            // 'ReadOnly' or 'ServerOnly' annotation : original info was loaded, do not copy
            if (AnnotationsManager.isServerOrReadOnly(persistentBean, propertyName)) {
                // If the proxy was initialized before clone force the merge value initialization now
                if (isInitialized(proxyInformations)) {
                    Object persistentValue = readPropertyValue(persistentBean, readerMethod.getName());
                    persistenceUtil.initialize(persistentValue);
                }
                return false;
            }

            if (proxyInformations == null) {
                // No proxy informations : just populate the property
                return true;
            }

            // Get clone value
            Object cloneValue = readPropertyValue(cloneBean, readerMethod.getName());

            Class<?> valueClass = readerMethod.getReturnType();
            boolean isCollection = Collection.class.isAssignableFrom(valueClass);
            boolean isMap = Map.class.isAssignableFrom(valueClass);

            if (isCollection) {
                if (isNullValue(cloneValue)) {
                    // The value is now null : proxy is needed
                    // Set collection proxy
                    Object persistentCollection = persistenceUtil.createPersistentCollection(persistentBean, proxyInformations, null);
                    writePropertyValue(persistentBean, persistentCollection, setterMethod.getName(), setterMethod.getParameterTypes());
                    return false;
                } else {
                    // Store proxy info for the copy operation
                    BeanlibCache.setProxyInformations(proxyInformations);
                }
            } else if (isMap) {
                if (isNullValue(cloneValue)) {
                    // Set map proxy
                    Object persistentMap = persistenceUtil.createPersistentMap(persistentBean, proxyInformations, null);
                    writePropertyValue(persistentBean, persistentMap, setterMethod.getName(), setterMethod.getParameterTypes());
                    return false;
                } else {
                    // Store proxy info for the copy operation
                    BeanlibCache.setProxyInformations(proxyInformations);
                }
            } else if (isNullValue(cloneValue) && isInitialized(proxyInformations) == false) {
                // Set an entity proxy
                Object proxy = persistenceUtil.createEntityProxy(proxyInformations);
                if (proxy != null) {
                    writePropertyValue(persistentBean, proxy, setterMethod.getName(), setterMethod.getParameterTypes());
                }

                // Skip beanlib in-depth population
                return false;
            }

            return true;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Indicates if the argument property is lazy or not
     * 
     * @param proxyInfo serialized proxy informations
     * @return
     */
    protected boolean isInitialized(Map<String, Serializable> proxyInfo) {
        if (proxyInfo != null) {
            Serializable initialized = proxyInfo.get(ILightEntity.INITIALISED);
            if (initialized != null) {
                return ((Boolean) initialized).booleanValue();
            }
        }

        // The property has no proxy info or it does not contains 'initialized' field
        return true;
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

    /**
     * Read a property value, even if it has a private getter
     */
    private void writePropertyValue(Object bean, Object value, String propertySetter, Class<?>... parameterTypes)
            throws SecurityException, NoSuchMethodException, IllegalArgumentException, IllegalAccessException, InvocationTargetException {
        Method writeMethod = IntrospectionHelper.getRecursiveDeclaredMethod(bean.getClass(), propertySetter, parameterTypes);
        writeMethod.setAccessible(true);

        writeMethod.invoke(bean, value);
    }

    /**
     * Indicates if the argument value must be considered as null (empty collections or map are also considered as null)
     * 
     * @param value
     * @return
     */
    private boolean isNullValue(Object value) {
        if (value == null) {
            return true;
        }
        return false;
    }
}
