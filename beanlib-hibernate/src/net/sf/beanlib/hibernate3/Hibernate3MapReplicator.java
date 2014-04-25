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
package net.sf.beanlib.hibernate3;

import java.util.Map;

import net.jcip.annotations.ThreadSafe;
import net.sf.beanlib.hibernate.UnEnhancer;
import net.sf.beanlib.provider.replicator.MapReplicator;
import net.sf.beanlib.spi.BeanTransformerSpi;
import net.sf.beanlib.spi.replicator.MapReplicatorSpi;

import org.hibernate.Hibernate;

/**
 * Hibernate 3 specific Map Replicator.
 * 
 * @author Joe D. Velopar
 */
public class Hibernate3MapReplicator extends MapReplicator {
    private static final Factory factory = new Factory();
    
    public static Factory getFactory() {
        return factory;
    }
    /**
     * Factory for {@link MapReplicator}
     * 
     * @author Joe D. Velopar
     */
    @ThreadSafe
    public static class Factory implements MapReplicatorSpi.Factory {
        private Factory() {}
        
        public Hibernate3MapReplicator newMapReplicatable(BeanTransformerSpi beanTransformer) {
            return new Hibernate3MapReplicator(beanTransformer);
        }
    }

    public static Hibernate3MapReplicator newMapReplicatable(BeanTransformerSpi beanTransformer) {
        return factory.newMapReplicatable(beanTransformer);
    }

    protected Hibernate3MapReplicator(BeanTransformerSpi beanTransformer) 
    {
        super(beanTransformer);
    }
    
    @Override
    public <K,V,T> T replicateMap(Map<K,V> from, Class<T> toClass)
    {
        if (!Hibernate.isInitialized(from))
            Hibernate.initialize(from);
        return super.replicateMap(from, toClass);
    }
    
    @Override
    protected <T> T createToInstance(Object from, Class<T> toClass)
        throws InstantiationException, IllegalAccessException, SecurityException, NoSuchMethodException 
    {
        // figure out the pre-enhanced class
        Class<T> actualClass = UnEnhancer.getActualClass(from);
        Class<T> targetClass = chooseClass(actualClass, toClass);
        return newInstanceAsPrivileged(targetClass);
    }

    @Override protected final <T> T unenhanceObject(T from) { return UnEnhancer.unenhanceObject(from); }
}
