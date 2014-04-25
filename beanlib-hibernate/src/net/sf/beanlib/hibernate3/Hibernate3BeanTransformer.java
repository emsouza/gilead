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

import net.sf.beanlib.hibernate.UnEnhancer;
import net.sf.beanlib.provider.BeanPopulator;
import net.sf.beanlib.provider.BeanTransformer;
import net.sf.beanlib.spi.BeanPopulatorSpi;

/**
 * A Hibernate 3 Bean Transformer is simply a {@link BeanTransformer}
 * with the extended capabilities of handling Hibernate (v3.x) objects.  
 * By default, the Hibernate proxies are replaced
 * with the actual instances, eagerly fetching from the database as necessary. 
 * However, the exact behavior of the transformation process, including whether eager
 * fetching is enabled or not, can be controlled using the same SPI as BeanTransformer.
 * 
 * @see BeanTransformer
 * @see Hibernate3BeanReplicator
 * 
 * @author Joe D. Velopar
 */
public class Hibernate3BeanTransformer extends BeanTransformer
{
    public Hibernate3BeanTransformer() {
        this(BeanPopulator.factory);
    }

    public Hibernate3BeanTransformer(BeanPopulatorSpi.Factory beanPopulatorFactory) {
        super(beanPopulatorFactory);
        this.initCollectionReplicatableFactory(
                Hibernate3CollectionReplicator.getFactory());
        this.initMapReplicatableFactory(
                Hibernate3MapReplicator.getFactory());
        this.initBlobReplicatableFactory(
                Hibernate3BlobReplicator.getFactory());
        this.initBeanReplicatableFactory(
                Hibernate3JavaBeanReplicator.getFactory());
    }
    
    /**
     * Creates a target instance from either the class of the given "from" object or the given toClass, 
     * giving priority to the one which is more specific whenever possible.  Note the class of the given 
     * from object is un-enhanced if it is found to be an enhanced object either via cglib or javassist.
     */
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
