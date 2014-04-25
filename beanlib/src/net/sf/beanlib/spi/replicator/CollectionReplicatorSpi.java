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
package net.sf.beanlib.spi.replicator;

import java.util.Collection;

import net.sf.beanlib.spi.BeanTransformerSpi;

/**
 * Collection Replicator API.
 *  
 * @author Joe D. Velopar
 */
public interface CollectionReplicatorSpi 
{
    /**
     * Collection Replicator Factory SPI.
     *  
     * @author Joe D. Velopar
     */
    public static interface Factory {
        CollectionReplicatorSpi newCollectionReplicatable(BeanTransformerSpi beanTransformer);
    }
    
    /** 
     * Returns a replicated collection. 
     * 
     * @param <V> component type of the from collection
     * @param <T> target class type
     * @param fromCollection from collection
     * @param toClass target class
     */
    public <V,T> T replicateCollection(Collection<V> fromCollection, Class<T> toClass);
}
