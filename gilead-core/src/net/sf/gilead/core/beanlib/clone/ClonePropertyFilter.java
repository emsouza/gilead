/*
 * Copyright 2007 The Apache Software Foundation.
 *
 * Licensed under the Apache License, Version 2.0 (the "License")
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package net.sf.gilead.core.beanlib.clone;

import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.sf.beanlib.spi.DetailedPropertyFilter;
import net.sf.gilead.core.IPersistenceUtil;
import net.sf.gilead.core.annotations.AnnotationsManager;
import net.sf.gilead.core.beanlib.CloneAndMergeConstants;
import net.sf.gilead.core.store.IProxyStore;
import net.sf.gilead.pojo.base.ILightEntity;
import net.sf.gilead.util.IntrospectionHelper;

/**
 * Populatable for Hibernate lazy handling
 *
 * @author bruno.marchesson
 */
public class ClonePropertyFilter implements DetailedPropertyFilter {

    private static final Logger LOGGER = LoggerFactory.getLogger(ClonePropertyFilter.class);

    // ----
    // Attributes
    // ----
    /**
     * The associated persistence utils
     */
    private IPersistenceUtil _persistenceUtil;

    /**
     * The proxy informations store.
     */
    private IProxyStore _proxyStore;

    // ----
    // Properties
    // ----
    /**
     * @return the persistence Util implementation to use
     */
    public IPersistenceUtil getPersistenceUtil() {
        return _persistenceUtil;
    }

    /**
     * @param util the persistenceUtil to set
     */
    public void setPersistenceUtil(IPersistenceUtil util) {
        _persistenceUtil = util;
    }

    /**
     * @return the used proxy Store
     */
    public IProxyStore getProxyStore() {
        return _proxyStore;
    }

    /**
     * @param store the proxy store to set
     */
    public void setProxyStore(IProxyStore store) {
        _proxyStore = store;
    }

    // -------------------------------------------------------------------------
    //
    // Constructor
    //
    // -------------------------------------------------------------------------
    /**
     * Constructor
     */
    public ClonePropertyFilter(IPersistenceUtil persistenceUtil, IProxyStore proxyStore) {
        setPersistenceUtil(persistenceUtil);
        setProxyStore(proxyStore);
    }

    // -------------------------------------------------------------------------
    //
    // DetailedBeanPopulatable implementation
    //
    // -------------------------------------------------------------------------
    /*
     * (non-Javadoc)
     * @see net.sf.beanlib.spi.DetailedBeanPopulatable#shouldPopulate(java.lang.String , java.lang.Object,
     * java.lang.reflect.Method, java.lang.Object, java.lang.reflect.Method)
     */
    @Override
    public boolean propagate(String propertyName, Object fromBean, Method readerMethod, Object toBean, Method setterMethod) {
        // Is the property lazy loaded ?
        //
        try {
            if ((CloneAndMergeConstants.PROXY_INFORMATIONS.equals(propertyName) == true)
                    || (CloneAndMergeConstants.INITIALIZATION_MAP.equals(propertyName) == true)) {
                return false;
            }

            // 'ServerOnly' annotation handling
            //
            if ((AnnotationsManager.isServerOnly(fromBean,
                    propertyName)) /*
                                    * || ( AnnotationsManager . isServerOnly ( toBean , propertyName ))
                                    */) {
                return false;
            }

            // Get from value
            //
            Object fromValue = readPropertyValue(fromBean, readerMethod.getName());
            if (fromValue == null) {
                return true;
            }

            boolean isPersistentCollection = _persistenceUtil.isPersistentCollection(fromValue.getClass());
            boolean isPersistentMap = _persistenceUtil.isPersistentMap(fromValue.getClass());

            // Lazy handling
            //
            if (_persistenceUtil.isInitialized(fromValue) == false) {
                // Lazy property !
                //
                LOGGER.trace(fromBean.toString() + "." + propertyName + " --> not initialized");

                // Get proxy informations
                //
                Map<String, Serializable> proxyInformations;
                if (isPersistentMap) {
                    proxyInformations = _persistenceUtil.serializePersistentMap((Map<?, ?>) fromValue);
                } else if (isPersistentCollection) {
                    proxyInformations = _persistenceUtil.serializePersistentCollection((Collection<?>) fromValue);
                } else {
                    proxyInformations = _persistenceUtil.serializeEntityProxy(fromValue);
                }

                // Add lazy property
                //
                proxyInformations.put(ILightEntity.INITIALISED, false);

                // Store proxy information
                //
                _proxyStore.storeProxyInformations(toBean, fromBean, propertyName, proxyInformations);

                return false;
            } else if (isPersistentMap) {
                // Persistent map handling
                //
                Map<String, Serializable> proxyInformations = _persistenceUtil.serializePersistentMap((Map<?, ?>) fromValue);

                _proxyStore.storeProxyInformations(toBean, fromBean, propertyName, proxyInformations);
            } else if (isPersistentCollection) {
                // Persistent collection handling
                //
                Map<String, Serializable> proxyInformations = _persistenceUtil.serializePersistentCollection((Collection<?>) fromValue);

                _proxyStore.storeProxyInformations(toBean, fromBean, propertyName, proxyInformations);
            }

            return true;
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    // -------------------------------------------------------------------------
    //
    // Internal methods
    //
    // -------------------------------------------------------------------------
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
