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
package net.sf.beanlib.provider.replicator;

import static net.sf.beanlib.utils.ClassUtils.isJavaPackage;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

import net.jcip.annotations.ThreadSafe;
import net.sf.beanlib.BeanlibException;
import net.sf.beanlib.spi.BeanTransformerSpi;
import net.sf.beanlib.spi.CustomBeanTransformerSpi;
import net.sf.beanlib.spi.replicator.MapReplicatorSpi;

/**
 * Default implementation of {@link net.sf.beanlib.spi.replicator.MapReplicatorSpi}.
 * 
 * @author Joe D. Velopar
 */
public class MapReplicator extends ReplicatorTemplate implements MapReplicatorSpi
{
    private static final Factory factory = new Factory();
    
    /**
     * Factory for {@link MapReplicator}
     * 
     * @author Joe D. Velopar
     */
    @ThreadSafe
    private static class Factory implements MapReplicatorSpi.Factory {
        private Factory() {}
        
        public MapReplicator newMapReplicatable(BeanTransformerSpi beanTransformer) {
            return new MapReplicator(beanTransformer);
        }
    }

    public static MapReplicator newMapReplicatable(BeanTransformerSpi beanTransformer) {
        return factory.newMapReplicatable(beanTransformer);
    }

    protected MapReplicator(BeanTransformerSpi beanTransformer) 
    {
        super(beanTransformer);
    }
    
    public <K,V,T> T replicateMap(Map<K,V> from, Class<T> toClass)
    {
        if (!toClass.isAssignableFrom(from.getClass()))
            return null;
        final Map<Object,Object> toMap;
        try {
            @SuppressWarnings("unchecked")
            final Map<Object,Object> m = (Map<Object,Object>)createToMap(from);
            
            toMap = m;
        } catch (SecurityException e) {
            throw new BeanlibException(e);
        } catch (InstantiationException e) {
            throw new BeanlibException(e);
        } catch (IllegalAccessException e) {
            throw new BeanlibException(e);
        } catch (NoSuchMethodException e) {
            throw new BeanlibException(e);
        }
        putTargetCloned(from, toMap);
        final Map<K,V> fromMap = from;
        final CustomBeanTransformerSpi customTransformer = getCustomerBeanTransformer();
        // recursively populate member objects.
        for (Map.Entry<K,V> fromEntry: fromMap.entrySet()) 
        {
            final K fromKey = fromEntry.getKey();
            final Object key;
            
            setKey: {
                if (fromKey == null) {
                    key = null;
                    break setKey;
                }
                
                if (super.containsTargetCloned(fromKey)) {
                    final Object targetCloned = super.getTargetCloned(fromKey);
                    
                    if (targetCloned != null) {
                        key = targetCloned;
                        break setKey;
                    }
                    // converting a non-null key to a null key
                    // is likely not what the client really wants;
                    // So let's move on (as a best effort)
                }
                
                if (customTransformer != null) {
                    final Class<?> fromKeyClass = fromKey.getClass();
                    
                    if (customTransformer.isTransformable(fromKey, fromKeyClass, null)) {
                        final Object transformed = customTransformer.transform(fromKey, fromKeyClass, null);
                        super.putTargetCloned(fromKey, transformed);
                        
                        if (transformed != null) {
                            key = transformed;
                            break setKey;
                        }
                        // converting a non-null key to a null key
                        // is likely not what the client really wants;
                        // So let's move on (as a best effort)
                    }
                }
                key = replicate(fromKey);
                // cloned target is already placed in the target cloned map in replicate
            }
            final V fromValue = fromEntry.getValue();
            final Object value;
            
            setValue: {
                if (fromValue == null) {
                    value = null;
                    break setValue;
                }
                
                if (super.containsTargetCloned(fromValue)) {
                    value = super.getTargetCloned(fromValue);
                    break setValue;
                }
                
                if (customTransformer != null) {
                    final Class<?> fromValueClass = fromValue.getClass();
                    
                    if (customTransformer.isTransformable(fromValue, fromValueClass, null)) {
                        value = customTransformer.transform(fromValue, fromValueClass, null);
                        super.putTargetCloned(fromValue, value);
                        break setValue;
                    }
                }
                value = replicate(fromValue);
                // cloned target is already placed in the target cloned map in replicate
            }
            try {
                toMap.put(key, value);
            } catch(RuntimeException ex) {
                // probably due to either the key or value being null, but the target map doesn't allow it.
                log.warn("Failed to put {" + key + "," + value + "} to " + toMap.getClass().getName(), ex);
            }
        }
        return toClass.cast(toMap);
    }
    
    private <K,V> Map<K,V> createToMap(Map<K,V> from) 
        throws InstantiationException, IllegalAccessException, SecurityException, NoSuchMethodException 
    {
        Class<?> fromClass = from.getClass();
        
        if (isJavaPackage(fromClass)) {
            if (from instanceof SortedMap) {
                SortedMap<K,V> fromSortedMap = (SortedMap<K,V>)from;
                Comparator<K> toComparator = createToComparator(fromSortedMap);
                
                if (toComparator != null)
                    return this.createToSortedMapWithComparator(fromSortedMap, toComparator);
            }
            return createToInstanceAsMap(from);
        }
        if (from instanceof SortedMap) {
            SortedMap<K,V> fromSortedMap = (SortedMap<K,V>)from;
            Comparator<K> toComparator = createToComparator(fromSortedMap);
            return new TreeMap<K,V>(toComparator);
        }
        return new HashMap<K,V>();
    }
    
    private <K,V> Map<K,V> createToInstanceAsMap(Map<K,V> from) 
        throws InstantiationException, IllegalAccessException, NoSuchMethodException
    {
        return createToInstance(from);
    }
    
    /** Returns a replicated comparator of the given sorted map, or null if there is no comparator. */
    private <K,V> Comparator<K> createToComparator(SortedMap<K,V> fromSortedMap)
    {
        Comparator<? super K> fromComparator = fromSortedMap.comparator();

        if (fromComparator == null)
            return null;
        
        @SuppressWarnings("unchecked")
        Comparator<K> toComparator = replicateByBeanReplicatable(fromComparator, Comparator.class);
        return toComparator;
    }

    private <K,V> SortedMap<K,V> createToSortedMapWithComparator(SortedMap<K,V> from, Comparator<? super K> comparator) 
        throws NoSuchMethodException, SecurityException
    {
        return createToInstanceWithComparator(from, comparator);
    }
}
