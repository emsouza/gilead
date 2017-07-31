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

package net.sf.gilead.core.beanlib.merge;

import java.io.Serializable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.sf.beanlib.hibernate.UnEnhancer;
import net.sf.beanlib.hibernate4.Hibernate4JavaBeanReplicator;
import net.sf.beanlib.spi.BeanTransformerSpi;
import net.sf.beanlib.spi.replicator.BeanReplicatorSpi;
import net.sf.gilead.core.IPersistenceUtil;
import net.sf.gilead.core.annotations.AnnotationsManager;
import net.sf.gilead.core.beanlib.IClassMapper;
import net.sf.gilead.core.store.IProxyStore;
import net.sf.gilead.exception.NotPersistentObjectException;
import net.sf.gilead.exception.TransientObjectException;

/**
 * Bean replicator with different from and to classes for merge operation
 *
 * @author bruno.marchesson
 */
public class MergeClassBeanReplicator extends Hibernate4JavaBeanReplicator {

    private static final Logger LOGGER = LoggerFactory.getLogger(MergeClassBeanReplicator.class);

    // ---
    // Attributes
    // ---
    /**
     * The class mapper (can be null)
     */
    private IClassMapper _classMapper;

    /**
     * The persistent util class
     */
    private IPersistenceUtil _persistenceUtil;

    /**
     * The current proxy store
     */
    private IProxyStore _proxyStore;

    // ----
    // Factory
    // ----
    public static final Factory factory = new Factory();

    /**
     * Factory for {@link MergeClassBeanReplicator}
     *
     * @author bruno.marchesson
     */
    public static class Factory implements BeanReplicatorSpi.Factory {
        private Factory() {}

        @Override
        public MergeClassBeanReplicator newBeanReplicatable(BeanTransformerSpi beanTransformer) {
            return new MergeClassBeanReplicator(beanTransformer);
        }
    }

    public static MergeClassBeanReplicator newBeanReplicatable(BeanTransformerSpi beanTransformer) {
        return factory.newBeanReplicatable(beanTransformer);
    }

    // ----
    // Constructor
    // ----
    protected MergeClassBeanReplicator(BeanTransformerSpi beanTransformer) {
        super(beanTransformer);
    }

    // ----
    // Properties
    // ----
    /**
     * @return the Class Mapper
     */
    public IClassMapper getClassMapper() {
        return _classMapper;
    }

    /**
     * @param mapper the classMapper to set
     */
    public void setClassMapper(IClassMapper mapper) {
        _classMapper = mapper;
    }

    /**
     * @return the _persistenceUtil
     */
    public IPersistenceUtil getPersistenceUtil() {
        return _persistenceUtil;
    }

    /**
     * @param util the persistence Util to set
     */
    public void setPersistenceUtil(IPersistenceUtil util) {
        _persistenceUtil = util;
    }

    /**
     * @return the proxy store
     */
    public IProxyStore getProxyStore() {
        return _proxyStore;
    }

    /**
     * @param store the proxy Store to set
     */
    public void setProxyStore(IProxyStore store) {
        _proxyStore = store;
    }

    // ----
    // Override
    // ----
    @Override
    public <V extends Object, T extends Object> T replicateBean(V from, java.lang.Class<T> toClass) {
        // Reset bean local
        BeanlibThreadLocal.setProxyInformations(null);

        // Force persistence map computation (useful for subclass)
        _persistenceUtil.isPersistentPojo(from);

        // Add current bean to stack
        BeanlibThreadLocal.getFromBeanStack().push(from);
        T result = super.replicateBean(from, toClass);
        BeanlibThreadLocal.getFromBeanStack().pop();

        return result;
    }

    @Override
    @SuppressWarnings("unchecked")
    protected <T extends Object> T createToInstance(Object from, java.lang.Class<T> toClass)
            throws InstantiationException, IllegalAccessException, SecurityException, NoSuchMethodException {
        // Clone mapper indirection
        //
        if (_classMapper != null) {
            Class<T> sourceClass = (Class<T>) _classMapper.getSourceClass(UnEnhancer.unenhanceClass(from.getClass()));
            if (sourceClass != null) {
                LOGGER.trace("Creating mapped class " + sourceClass);
                toClass = sourceClass;
            } else {
                LOGGER.trace("Creating merged class " + toClass);
            }
        }

        // Get real target class
        //
        if (toClass.isInterface()) {
            // Keep the from class
            //
            toClass = (Class<T>) from.getClass();
        }

        T result = null;

        // Proxy informations
        //
        if ((AnnotationsManager.hasGileadAnnotations(from.getClass())) || (AnnotationsManager.hasGileadAnnotations(toClass))) {
            // Load entity
            try {
                Serializable id = _persistenceUtil.getId(from, toClass);
                toClass = chooseClass(from.getClass(), toClass);
                result = (T) _persistenceUtil.load(id, toClass);
            } catch (NotPersistentObjectException e) {
                LOGGER.warn("Not an hibernate class (" + toClass + ") : annotated values will not be restored.");
                return (T) from;
            } catch (TransientObjectException e) {
                LOGGER.warn("Transient value of class " + toClass + " : annotated values will not be restored.");
                return (T) from;
            }
        }

        if (result == null) {
            result = super.createToInstance(from, toClass);

            // Dynamic proxy workaround : for inheritance purpose
            // beanlib returns an instance of the proxy class
            // since it inherits from the source class...
            //
            if ((_classMapper != null) && (_classMapper.getSourceClass(result.getClass()) != null)) {
                result = newInstanceAsPrivileged(toClass);
            }
        }

        // Add the bean to stack
        BeanlibThreadLocal.getToBeanStack().push(result);
        return result;
    }
}
