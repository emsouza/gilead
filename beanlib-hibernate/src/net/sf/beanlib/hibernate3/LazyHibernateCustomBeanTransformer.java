/*
 * Copyright 2009 The Apache Software Foundation.
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
package net.sf.beanlib.hibernate3;

import net.sf.beanlib.PropertyInfo;
import net.sf.beanlib.spi.BeanTransformerSpi;
import net.sf.beanlib.spi.CustomBeanTransformerSpi;

import org.hibernate.Hibernate;

/**
 * A convenient custom bean transformer that can be used 
 * to replicate only the initialized properties/associations 
 * in a Hibernate object graph.
 * <p>
 * Sample Usage:
 * <pre>
 * HibernateBeanReplicator replicator = new Hibernate3BeanReplicator();
 * replicator.initCustomTransformerFactory(new LazyHibernateCustomBeanTransformer.Factory());
 * replicator.deepCopy(...);
 * </pre>
 */
public class LazyHibernateCustomBeanTransformer implements CustomBeanTransformerSpi 
{
    public boolean isTransformable(Object from, Class<?> toClass, PropertyInfo propertyInfo)
    {   // apply custom transformation for the uninitialized properties 
        return !Hibernate.isInitialized(from); 
    } 

    public <T> T transform(Object in, Class<T> toClass, PropertyInfo propertyInfo) { return null; }
    
    /** The factory for a {@link LazyHibernateCustomBeanTransformer}. */
    public static class Factory implements CustomBeanTransformerSpi.Factory {
        public CustomBeanTransformerSpi newCustomBeanTransformer(BeanTransformerSpi contextBeanTransformer) {
            return new LazyHibernateCustomBeanTransformer();
        }
    }
}
