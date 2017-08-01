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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.sf.beanlib.hibernate4.Hibernate4JavaBeanReplicator;
import net.sf.beanlib.spi.BeanTransformerSpi;
import net.sf.beanlib.spi.replicator.BeanReplicatorSpi;
import net.sf.gilead.core.PersistenceUtil;
import net.sf.gilead.core.beanlib.ClassMapper;
import net.sf.gilead.core.beanlib.merge.BeanlibCache;
import net.sf.gilead.core.beanlib.merge.MergeClassBeanReplicator;

/**
 * Bean replicator with different from and to classes for clone operation
 *
 * @author bruno.marchesson
 */
public class CloneClassBeanReplicator extends Hibernate4JavaBeanReplicator {

    private static final Logger LOGGER = LoggerFactory.getLogger(CloneClassBeanReplicator.class);

    /**
     * The class mapper (can be null)
     */
    private ClassMapper classMapper;

    /**
     * Persistence util class
     */
    private PersistenceUtil persistenceUtil;

    public static final Factory factory = new Factory();

    /**
     * Factory for {@link MergeClassBeanReplicator}
     *
     * @author bruno.marchesson
     */
    public static class Factory implements BeanReplicatorSpi.Factory {
        private Factory() {}

        @Override
        public CloneClassBeanReplicator newBeanReplicatable(BeanTransformerSpi beanTransformer) {
            return new CloneClassBeanReplicator(beanTransformer);
        }
    }

    public static CloneClassBeanReplicator newBeanReplicatable(BeanTransformerSpi beanTransformer) {
        return factory.newBeanReplicatable(beanTransformer);
    }

    protected CloneClassBeanReplicator(BeanTransformerSpi beanTransformer) {
        super(beanTransformer);
    }

    /**
     * @return the Class Mapper
     */
    public ClassMapper getClassMapper() {
        return classMapper;
    }

    /**
     * @param mapper the classMapper to set
     */
    public void setClassMapper(ClassMapper mapper) {
        classMapper = mapper;
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
    public void setPersistenceUtil(PersistenceUtil util) {
        persistenceUtil = util;
    }

    @Override
    public <V extends Object, T extends Object> T replicateBean(V from, java.lang.Class<T> toClass) {
        // Force persistence map computation (useful for subclass)
        persistenceUtil.isPersistentPojo(from);

        BeanlibCache.getFromBeanStack().push(from);
        T result = super.replicateBean(from, toClass);
        BeanlibCache.getFromBeanStack().pop();
        return result;
    }

    @Override
    @SuppressWarnings("unchecked")
    protected <T extends Object> T createToInstance(Object from, java.lang.Class<T> toClass)
            throws InstantiationException, IllegalAccessException, SecurityException, NoSuchMethodException {
        // Class mapper indirection
        if (classMapper != null) {
            // Get target class
            Class<T> targetClass = (Class<T>) classMapper.getTargetClass(from.getClass());

            // Keep target class only if not null
            if (targetClass != null) {
                LOGGER.trace("Class mapper : from " + from.getClass() + " to " + targetClass);
                toClass = targetClass;
            } else {
                LOGGER.trace("Class mapper : no target class for " + from.getClass());
            }
        }
        return super.createToInstance(from, toClass);
    }
}
