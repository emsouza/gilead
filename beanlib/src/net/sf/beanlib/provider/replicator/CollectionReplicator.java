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

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.SortedSet;

import net.jcip.annotations.ThreadSafe;
import net.sf.beanlib.BeanlibException;
import net.sf.beanlib.spi.BeanTransformerSpi;
import net.sf.beanlib.spi.CustomBeanTransformerSpi;
import net.sf.beanlib.spi.replicator.CollectionReplicatorSpi;

/**
 * Default implementation of {@link net.sf.beanlib.spi.replicator.CollectionReplicatorSpi}.
 * 
 * @author Joe D. Velopar
 */
public class CollectionReplicator extends ReplicatorTemplate implements CollectionReplicatorSpi 
{
    private static final Factory factory = new Factory();
    
    /**
     * Factory for {@link CollectionReplicator}
     * 
     * @author Joe D. Velopar
     */
    @ThreadSafe
    private static class Factory implements CollectionReplicatorSpi.Factory {
        private Factory() {}
        
        public CollectionReplicator newCollectionReplicatable(BeanTransformerSpi beanTransformer) {
            return new CollectionReplicator(beanTransformer);
        }
    }
    
    public static CollectionReplicator newCollectionReplicatable(BeanTransformerSpi beanTransformer) {
        return factory.newCollectionReplicatable(beanTransformer);
    }
    
    static final Class<?> CLASS_ARRAY_ARRAYLIST;
    static {
        try {
            CLASS_ARRAY_ARRAYLIST = Class.forName("java.util.Arrays$ArrayList");
        } catch (ClassNotFoundException e) {
            throw new IllegalStateException(e);
        }
    }
    
    protected CollectionReplicator(BeanTransformerSpi beanTransformer) 
    {
        super(beanTransformer);
    }
    
    public <V,T> T replicateCollection(Collection<V> fromCollection, Class<T> toClass)
    {
        if (!toClass.isAssignableFrom(fromCollection.getClass()))
            return null;
        final Collection<Object> toCollection;
        try {
            @SuppressWarnings("unchecked")
            final Collection<Object> col = (Collection<Object>)this.createToCollection(fromCollection);
            
            toCollection = col;
            putTargetCloned(fromCollection, toCollection);
            final CustomBeanTransformerSpi customTransformer = getCustomerBeanTransformer();
            // recursively populate member objects.
            for (final V fromMember : fromCollection) 
            {
                if (fromMember == null) {
                    toCollection.add(null);
                    continue;
                }
                
                if (super.containsTargetCloned(fromMember)) {
                    final Object targetCloned = super.getTargetCloned(fromMember);
                    toCollection.add(targetCloned);
                    continue;
                }
                
                if (customTransformer != null) {
                    final Class<?> fromMemberClass = fromMember.getClass();
                    
                    if (customTransformer.isTransformable(fromMember, fromMemberClass, null)) {
                        final Object toMember = customTransformer.transform(fromMember, fromMemberClass, null);
                        super.putTargetCloned(fromMember, toMember);
                        toCollection.add(toMember);
                        continue;
                    }
                }
                final V toMember = replicate(fromMember);
                // cloned target is already placed in the target cloned map in replicate
                toCollection.add(toMember);
            }
        } catch (SecurityException e) {
            throw new BeanlibException(e);
        } catch (InstantiationException e) {
            throw new BeanlibException(e);
        } catch (IllegalAccessException e) {
            throw new BeanlibException(e);
        } catch (NoSuchMethodException e) {
            throw new BeanlibException(e);
        } catch (InvocationTargetException e) {
            throw new BeanlibException(e.getTargetException());
        }
        return toClass.cast(toCollection);
    }

    // Use the same comparator or otherwise ClassCastException.
    // http://sourceforge.net/forum/forum.php?thread_id=1462253&forum_id=470286
    // Thanks to Jam Flava for finding this bug.
    protected <T> Collection<T> createToCollection(Collection<T> from)
        throws InstantiationException, IllegalAccessException, SecurityException, 
                NoSuchMethodException, InvocationTargetException
    {
        Class<?> fromClass = from.getClass();
        
        if (isJavaPackage(fromClass)) {
            if (from instanceof SortedSet) {
                SortedSet<T> fromSortedSet = (SortedSet<T>)from;
                Comparator<T> toComparator = createToComparator(fromSortedSet);
                
                if (toComparator != null)
                    return this.createToSortedSetWithComparator(fromSortedSet, toComparator);
            }
            return createToInstanceAsCollection(from);
        }
        if (from instanceof SortedSet) {
            SortedSet<T> fromSortedSet = (SortedSet<T>)from;
            Comparator<T> toComparator = createToComparator(fromSortedSet);
            
            Constructor<?> constructor = fromClass.getConstructor(Comparator.class);
            Object[] initargs = {toComparator};
            
            @SuppressWarnings("unchecked")
            Collection<T> ret = (Collection<T>) constructor.newInstance(initargs);
            return ret;
        }
        if (from instanceof Set
        ||  from instanceof List) 
        {
            @SuppressWarnings("unchecked")
            Collection<T> ret = (Collection<T>)fromClass.newInstance();
            return ret;
        }
        // don't know what collection, so use list
        log.warn("Don't know what collection object:" + fromClass + ", so assume List.");
        return new ArrayList<T>(from.size());
    }
    
    protected final <T> Collection<T> createToInstanceAsCollection(Collection<T> from) 
        throws InstantiationException, IllegalAccessException, NoSuchMethodException
    {
        
        if (from.getClass() == CLASS_ARRAY_ARRAYLIST) {
            List<T> list = (List<T>)from;
            return new ArrayList<T>(list.size());
        }
        
        Collection<T> ret = createToInstance(from);
        return ret;
    }
    
    protected final <T> SortedSet<T> createToSortedSetWithComparator(SortedSet<T> from, Comparator<?> comparator) 
        throws NoSuchMethodException, SecurityException
    {
        return (SortedSet<T>)createToInstanceWithComparator(from, comparator);
    }
    
    /** Returns a replicated comparator of the given sorted set, or null if there is no comparator. */
    protected <T> Comparator<T> createToComparator(SortedSet<T> fromSortedSet)
    {
        Comparator<? super T> fromComparator = fromSortedSet.comparator();
        
        if (fromComparator == null)
            return null;
        
        @SuppressWarnings("unchecked")
        Comparator<T> ret = replicateByBeanReplicatable(fromComparator, Comparator.class);
        return ret;
    }
}
