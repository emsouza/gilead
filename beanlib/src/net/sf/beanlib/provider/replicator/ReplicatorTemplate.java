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

import static net.sf.beanlib.utils.ClassUtils.immutable;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.sql.Blob;
import java.util.Calendar;
import java.util.Collection;
import java.util.Comparator;
import java.util.Date;
import java.util.Map;

import net.sf.beanlib.BeanlibException;
import net.sf.beanlib.PropertyInfo;
import net.sf.beanlib.spi.BeanTransformerSpi;
import net.sf.beanlib.spi.CustomBeanTransformerSpi;

import org.apache.log4j.Logger;

/**
 * A useful base class for the replicator implementations.
 * 
 * @author Joe D. Velopar
 */
public abstract class ReplicatorTemplate
{
    protected final Logger log = Logger.getLogger(getClass());
    private final BeanTransformerSpi beanTransformer;
    
    protected ReplicatorTemplate(BeanTransformerSpi beanTransformer) {
        this.beanTransformer = beanTransformer;
    }

    protected ReplicatorTemplate() {
        this.beanTransformer = (BeanTransformerSpi)this;
    }
    
    protected final CustomBeanTransformerSpi getCustomerBeanTransformer() { return beanTransformer.getCustomBeanTransformer(); }
    
    /**
     * Replicate the given from object, recursively if necessary.
     * 
     * Currently a property is replicated if it is an instance
     * of Collection, Map, Timestamp, Date, Blob, Hibernate entity, 
     * JavaBean, or an array.
     */
    protected <T >T replicate(T from)
    {
        if (from == null)
            return null;
        try {
            @SuppressWarnings("unchecked") T ret = (T)replicate(from, from.getClass());
            return ret;
        } catch (SecurityException e) {
            throw new BeanlibException(e);
        }
    }
    
    /**
     * Replicate the given from object, recursively if necessary, 
     * to an instance of the toClass.
     * 
     * If, however, the from object is an enhanced object and the toClass
     * is the same as the from object's class, the class of the un-enhanced object
     * will be used as the toClass instead of the original toClass.
     * 
     * Currently a property is replicated if it is an instance
     * of Collection, Map, Timestamp, Date, Blob, Hibernate entity, 
     * JavaBean, or an array.
     */
    protected <T> T replicate(final Object from, Class<T> toClass) 
        throws SecurityException 
    {
        if (from == null) 
        {
            return toClass.isPrimitive()
                 ? ImmutableReplicator.getDefaultPrimitiveValue(toClass)
                 : null;
        }
        final Object unenhanced = unenhanceObject(from);
        
        if (unenhanced != from
        &&  from.getClass() == toClass) 
        {   // The original to-class is an enhanced class:
            // Use the un-enhanced class instead
            @SuppressWarnings("unchecked")
            Class<T> unenhancedClass = (Class<T>)unenhanced.getClass();
            toClass = unenhancedClass;
        }
        
        if (containsTargetCloned(from)) 
        {   // already transformed
            @SuppressWarnings("unchecked") T to = (T)getTargetCloned(from);
            return to;
            
        }
        // Immutable e.g. String, Enum, primitvies, BigDecimal, etc.
        if (immutable(toClass))
            return beanTransformer.getImmutableReplicatable()
                                  .replicateImmutable(unenhanced, toClass);
        // Collection
        if (unenhanced instanceof Collection<?>)
            return beanTransformer.getCollectionReplicatable()
                                  .replicateCollection((Collection<?>)unenhanced, toClass);
        // Array
        if (unenhanced.getClass().isArray())
            return beanTransformer.getArrayReplicatable()
                                  .replicateArray(unenhanced, toClass);
        // Map
        if (unenhanced instanceof Map<?,?>) {
            @SuppressWarnings("unchecked")
            T ret = (T)beanTransformer.getMapReplicatable()
                                      .replicateMap((Map)unenhanced, toClass);
            return ret;
        }
        // Date or Timestamp
        if (unenhanced instanceof Date)
            return beanTransformer.getDateReplicatable()
                                  .replicateDate((Date)unenhanced, toClass);
        // Calendar
        if (unenhanced instanceof Calendar)
            return beanTransformer.getCalendarReplicatable()
                                  .replicateCalendar((Calendar)unenhanced, toClass);
        // Blob
        if (unenhanced instanceof Blob)
            return beanTransformer.getBlobReplicatable()
                                  .replicateBlob((Blob)unenhanced, toClass);
        // Other objects
        // Note the original from object is expected to be passed along
        return replicateByBeanReplicatable(from, toClass);
    }
    
    protected <T> T replicateByBeanReplicatable(Object from, Class<T> toClass)
    {
        return beanTransformer.getBeanReplicatable()
                              .replicateBean(from, toClass);
    }

    /**
     * Creates a target instance using the class of the given object.
     */
    @SuppressWarnings("unchecked")
    protected final <T> T createToInstance(T from) 
        throws InstantiationException, IllegalAccessException, SecurityException, NoSuchMethodException 
    {
        return (T)createToInstance(from, from.getClass());
    }
    
    /**
     * Creates a target instance from either the class of the given "from" object or the given toClass, 
     * giving priority to the one which is more specific whenever possible.
     */
    protected <T> T createToInstance(Object from, Class<T> toClass)
        throws InstantiationException, IllegalAccessException, SecurityException, NoSuchMethodException 
    {
        Class<T> targetClass = chooseClass(from.getClass(), toClass);
        return newInstanceAsPrivileged(targetClass);
    }
    
    /**
     * Chooses a target class from the given "from" and "to" classes, giving
     * priority to the one which is more specific whenever possible.
     *  
     * @param <T> target type
     * @param fromClass from class
     * @param toClass target class
     * @return the target class if either the "from" class is not assignable to the "to" class,
     * or if the from class is abstract; otherwise, returns the from class.
     */
    protected final <T> Class<T> chooseClass(Class<?> fromClass, Class<T> toClass) 
    {
        if (!toClass.isAssignableFrom(fromClass) 
        ||  Modifier.isAbstract(fromClass.getModifiers()))
            return toClass;
        
        @SuppressWarnings("unchecked") Class<T> ret = (Class<T>)fromClass;
        return ret;
    }
    
    protected <T> T transform(Object in, Class<T> toClass, PropertyInfo propertyInfo) {
        return beanTransformer.transform(in, toClass, propertyInfo);
    }
    
    protected void populateBean(Object fromMember, Object toMember) {
        beanTransformer.getBeanPopulatorSpiFactory()
                       .newBeanPopulator(fromMember, toMember)
                       .initBeanPopulatorBaseConfig(
                                    beanTransformer.getBeanPopulatorBaseConfig())
                       .initTransformer(beanTransformer)
                       .populate();
    }
    
    @SuppressWarnings("unchecked")
    protected <T> T createToInstanceWithComparator(T from, Comparator<?> comparator)
        throws SecurityException, NoSuchMethodException
    {
        return (T)createToInstanceWithComparator(from.getClass(), comparator);
    }

    private <T> T createToInstanceWithComparator(Class<T> toClass, Comparator<?> comparator) 
        throws SecurityException, NoSuchMethodException
    {
        return newInstanceWithComparatorAsPrivileged(toClass, comparator);
    }


    /** 
     * Creates a new instance of the given class via the no-arg constructor,
     * invoking the constructor as a privileged action if it is protected or private.
     * 
     * @param c given class
     * @return a new instance of the given class via the no-arg constructor
     */ 
    protected final <T> T newInstanceAsPrivileged(Class<T> c) 
        throws SecurityException, NoSuchMethodException, InstantiationException, IllegalAccessException
    {
        final Constructor<?> constructor = c.getDeclaredConstructor();
        
        if (Modifier.isPublic(constructor.getModifiers()))
            return c.newInstance();
        return c.cast(AccessController.doPrivileged(
            new PrivilegedAction<Object>() {
                public Object run() {
                    constructor.setAccessible(true);
                    try {
                        return constructor.newInstance();
                    } catch (IllegalAccessException e) {
                        throw new BeanlibException(e);
                    } catch (InvocationTargetException e) {
                        throw new BeanlibException(e.getTargetException());
                    } catch (InstantiationException e) {
                        throw new BeanlibException(e);
                    }
                }
        }));
    }

    private <T> T newInstanceWithComparatorAsPrivileged(Class<T> c, final Comparator<?> comparator) 
        throws SecurityException, NoSuchMethodException
    {
        final Constructor<?> constructor = c.getDeclaredConstructor(Comparator.class);
        
        if (Modifier.isPublic(constructor.getModifiers())) {
            try {
                return c.cast(constructor.newInstance(comparator));
            } catch (InstantiationException e) {
                throw new BeanlibException(e);
            } catch (IllegalAccessException e) {
                throw new BeanlibException(e);
            } catch (InvocationTargetException e) {
                throw new BeanlibException(e.getTargetException());
            }
        }
        return c.cast(AccessController.doPrivileged(
            new PrivilegedAction<Object>() {
                public Object run() {
                    constructor.setAccessible(true);
                    try {
                        return  constructor.newInstance(comparator);
                    } catch (IllegalAccessException e) {
                        throw new BeanlibException(e);
                    } catch (InvocationTargetException e) {
                        throw new BeanlibException(e.getTargetException());
                    } catch (InstantiationException e) {
                        throw new BeanlibException(e);
                    }
                }
        }));
    }

    protected final boolean containsTargetCloned(Object from)
    {
        return beanTransformer.getClonedMap().containsKey(from);
    }

    protected final Object getTargetCloned(Object from)
    {
        return beanTransformer.getClonedMap().get(from);
    }

    protected final Object putTargetCloned(Object from, Object to)
    {
        return beanTransformer.getClonedMap().put(from, to);
    }
    
    /**
     * Returns an equivalent object un-enhanced from the given object.  
     * By default, the input object is returned.
     * Subclass must override this method to perform any such un-enhancement, if necessary.
     */
    protected <T> T unenhanceObject(T object) { return object; }
}
