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

package net.sf.gilead.core;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.sf.beanlib.hibernate.HibernateBeanReplicator;
import net.sf.beanlib.provider.BeanPopulator;
import net.sf.beanlib.spi.BeanTransformerSpi;
import net.sf.gilead.core.beanlib.IClassMapper;
import net.sf.gilead.core.beanlib.clone.CloneBeanReplicator;
import net.sf.gilead.core.beanlib.merge.BeanlibThreadLocal;
import net.sf.gilead.core.beanlib.merge.MergeBeanPopulator;
import net.sf.gilead.core.store.IProxyStore;

/**
 * This class replaces all "lazy but not loaded" Hibernate association with null to allow the argument POJO to be used
 * in other libraries without additional loading of 'LazyInitializationException'. The lazy properties are stored as
 * string. They can be reused for "reattaching" the clone pojo to a fresh Hibernate object.
 *
 * @author bruno.marchesson
 */
public class LazyKiller {

    private static final Logger LOGGER = LoggerFactory.getLogger(LazyKiller.class);

    /**
     * The class mapper
     */
    private IClassMapper classMapper;

    /**
     * The associated persistence utils
     */
    private IPersistenceUtil persistenceUtil;

    /**
     * The used proxy store
     */
    private IProxyStore proxyStore;

    /**
     * The cloned map. It is used to propagate treated beans throughout collection clone and merge.
     */
    private static ThreadLocal<Map<Object, Object>> clonedMap = new ThreadLocal<Map<Object, Object>>();

    // ----
    // Properties
    // ----
    /**
     * @return the persistence Util implementation to use
     */
    public IPersistenceUtil getPersistenceUtil() {
        return persistenceUtil;
    }

    /**
     * @param util the persistenceUtil to set
     */
    public void setPersistenceUtil(IPersistenceUtil util) {
        persistenceUtil = util;
    }

    /**
     * @param mapper the class Mapper to set
     */
    public void setClassMapper(IClassMapper mapper) {
        classMapper = mapper;
    }

    /**
     * @return the associated proxy Store
     */
    public IProxyStore getProxyStore() {
        return proxyStore;
    }

    /**
     * @param store the proxy Store to set
     */
    public void setProxyStore(IProxyStore store) {
        proxyStore = store;
    }

    /**
     * Empty constructor
     */
    public LazyKiller() {
        this(null, null, null);
    }

    /**
     * Base constructor
     *
     * @param classMapper the class mapping service
     * @param persistenceUtil persistence util implementation
     * @param proxyStore the proxy store
     */
    public LazyKiller(IClassMapper classMapper, IPersistenceUtil persistenceUtil, IProxyStore proxyStore) {
        setClassMapper(classMapper);
        setPersistenceUtil(persistenceUtil);
        setProxyStore(proxyStore);
    }

    /**
     * Reset the clone map.
     */
    public void reset() {
        clonedMap.remove();
    }

    /**
     * Hibernate detachment
     *
     * @param hibernatePojo the input hibernate pojo
     * @return a pure Java clone
     */
    public Object detach(Object hibernatePojo) {
        // Precondition checking
        if (hibernatePojo == null) {
            return null;
        }

        LOGGER.trace("Detaching " + hibernatePojo.toString());

        // Search for Proxy
        Class<?> targetClass = hibernatePojo.getClass();
        if (classMapper != null) {
            Class<?> mappedClass = classMapper.getTargetClass(targetClass);
            if (mappedClass != null) {
                targetClass = mappedClass;
            }
        }

        return clone(hibernatePojo, targetClass);
    }

    /**
     * Hibernate detachment
     *
     * @param hibernatePojo the input hibernate pojo
     * @return a pure Java clone
     */
    public Object detach(Object hibernatePojo, Class<?> cloneClass) {
        // Precondition checking
        if (hibernatePojo == null) {
            return null;
        }

        LOGGER.trace("Detaching " + hibernatePojo.toString());

        // Clone with beanLib
        return clone(hibernatePojo, cloneClass);
    }

    /**
     * Hibernate attachment
     *
     * @param hibernatePojo the stored or fresh Hibernate POJO
     * @param clonePojo the cloned pojo
     */
    public void attach(Object hibernatePojo, Object clonePojo) {
        // Precondition checking
        if ((hibernatePojo == null) || (clonePojo == null)) {
            return;
        }

        LOGGER.trace("Attaching " + clonePojo.toString());

        // Populate with BeanLib
        populate(hibernatePojo, clonePojo);
    }

    /**
     * Clone the abstract POJO with BeanLib Every time a lazy property is detected, it is replaced with null. It is also
     * marked as "lazy" for ILightEntity sub-classes
     *
     * @param pojo
     * @return
     */
    protected Object clone(Object hibernatePojo, Class<?> cloneClass) {
        HibernateBeanReplicator replicator = new CloneBeanReplicator(classMapper, persistenceUtil, proxyStore);

        return replicator.copy(hibernatePojo, cloneClass);
    }

    /**
     * Populate the hibernatePojo (a fresh new one or the one used to clone) with the clone detached object. This
     * Hibernate POJO holds the lazy properties information
     */
    public void populate(Object hibernatePojo, Object clonePojo) {

        // Populate hibernate POJO from the cloned pojo
        BeanPopulator replicator = MergeBeanPopulator.newBeanPopulator(clonePojo, hibernatePojo, classMapper, persistenceUtil, proxyStore);

        // Propagate cloned map if needed
        BeanTransformerSpi transformer = (BeanTransformerSpi) replicator.getTransformer();
        Map<Object, Object> clonedMap = LazyKiller.clonedMap.get();
        if (clonedMap != null) {
            transformer.getClonedMap().putAll(clonedMap);
        }

        // Store root pojo on bean stack
        BeanlibThreadLocal.getFromBeanStack().clear();
        BeanlibThreadLocal.getFromBeanStack().push(clonePojo);
        BeanlibThreadLocal.getToBeanStack().push(hibernatePojo);

        replicator.populate();

        // Fill cloned map if needed
        LazyKiller.clonedMap.set(transformer.getClonedMap());
    }

    public void info(String origem) {
        LoggerFactory.getLogger(LazyKiller.class).info(origem + " cloneMap " + (clonedMap.get() != null ? "NOK" : "OK"));
    }
}
