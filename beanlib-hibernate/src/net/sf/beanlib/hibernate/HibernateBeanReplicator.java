/*
 * Copyright 2005 The Apache Software Foundation.
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
package net.sf.beanlib.hibernate;

import java.util.Collections;
import java.util.Set;

import net.jcip.annotations.NotThreadSafe;
import net.sf.beanlib.CollectionPropertyName;
import net.sf.beanlib.provider.BeanPopulator;
import net.sf.beanlib.spi.BeanMethodCollector;
import net.sf.beanlib.spi.BeanMethodFinder;
import net.sf.beanlib.spi.BeanPopulationExceptionHandler;
import net.sf.beanlib.spi.BeanPopulatorBaseConfig;
import net.sf.beanlib.spi.BeanPopulatorBaseSpi;
import net.sf.beanlib.spi.BeanSourceHandler;
import net.sf.beanlib.spi.BeanTransformerSpi;
import net.sf.beanlib.spi.ChainedCustomBeanTransformer;
import net.sf.beanlib.spi.CustomBeanTransformerSpi;
import net.sf.beanlib.spi.DetailedPropertyFilter;
import net.sf.beanlib.spi.PropertyFilter;

/**
 * Hibernate Bean Replicator.
 * <p> 
 * This class can be used to conveniently replicate Hibernate objects 
 * that follow the JavaBean getter/setter convention on a best attempt basis.
 *   
 * The replication is typically recursive in that 
 * the whole object graph of the input object is replicated into an equivalent output object graph, 
 * resolving circular references, and eagerly fetching proxied instances as necessary.
 * 
 * However, the exact behavior of the replication process including<ul>
 * <li>to what extent the input object graph should be traversed and/or replicated; and </li>
 * <li>whether proxied instances should be eagerly fetched or not</li>
 * </ul> 
 * can be controlled and/or supplemented by the client code via various options:
 * <p>
 * <ol>
 * <li>All the configurable options of {@link BeanPopulatorBaseSpi} are available, as
 * the replication of JavaBean properties inevitably involves bean population.</li>
 * <p>
 * <li>If the default property filter {@link HibernatePropertyFilter} is used,
 * <ul>
 *   <li>An application package prefix used to determine if a property 
 *       with a type of an entity bean class will be included for replication;</li>
 *   <li>The set of entity bean classes for matching properties that will be replicated;</li>
 *   <li>The set of collection and map properties that will be replicated;</li>
 *   <li>A {@link PropertyFilter vetoer} used to veto the propagation of a property</li>
 * </ul>
 * <p>
 * <li>For anything else that the existing implementation fails to transform, client can provide
 * one or multiple custom transformer factories via  
 * {@link #initCustomTransformerFactory(net.sf.beanlib.spi.CustomBeanTransformerSpi.Factory...)}.
 * </li>
 * </ol>
 * <p>
 * This was originally the base class for both the Hibernate 2.x and Hibernate 3.x
 * replicators, but now Hibernate 2 is no longer supported.
 * 
 * @see CustomBeanTransformerSpi
 * 
 * @see net.sf.beanlib.hibernate3.Hibernate3BeanReplicator 
 *  
 * @author Joe D. Velopar
 */
@NotThreadSafe
public abstract class HibernateBeanReplicator implements BeanPopulatorBaseSpi 
{
    /** Used to do the heavy lifting of Hibernate object transformation and replication. */
    private final BeanTransformerSpi hibernateBeanTransformer;

    /**
     * You probably want to construct a 
     * {@link net.sf.beanlib.hibernate3.Hibernate3BeanReplicator Hibernate3BeanReplicator}
     * directly instead of this ?
     */
    protected HibernateBeanReplicator(BeanTransformerSpi hibernateBeanTransformer) 
    {
        if (hibernateBeanTransformer == null)
            throw new IllegalArgumentException("Argument hibernateBeanTransformer must not be null");
        this.hibernateBeanTransformer = hibernateBeanTransformer;
    }

    /** 
     * Returns a copy of the given object.
     * 
     * The exact behavior of the copy depends on how the replicator has been configured 
     * via the init* methods.
     * <p>
     * In the case when none of the init* methods is invoked, this method behaves
     * like {@link HibernateBeanReplicator#deepCopy(Object)}, but without using the {@link HibernatePropertyFilter}.
     * 
     * @param <T> type of the given object.
     * @param from given object.
     */
    @SuppressWarnings("unchecked")
    public final <T> T copy(T from) {
        return (T)(from == null 
                         ? null 
                         : copy(from, UnEnhancer.getActualClass(from)));
    }

    /** 
     * Returns an instance of the given class with values copied from the given object.
     * 
     * The exact behavior of the copy depends on how the replicator has been configured 
     * via the init* methods.
     * <p>
     * In the case when none of the init* methods is invoked, this method behaves
     * exactly like {@link HibernateBeanReplicator#deepCopy(Object, Class)}.
     * 
     * @param <T> type of the given  object.
     * @param from given object.
     * @param toClass target class of the returned object.
     */
    public final <T> T copy(Object from, Class<T> toClass) {
        if (from == null)
            return null;
        try {
            return hibernateBeanTransformer.transform(from, toClass, null);
        } finally {
            hibernateBeanTransformer.reset();
        }
    }
    
    /** 
     * Convenient method to deep copy the given object using the default behavior.
     * <p>
     * Notes:
     * <ul>
     * <li>
     * Use {@link HibernateBeanReplicator#copy(Object, Class)} instead of this method 
     * if you want to plug in a different (detailed) property filter.
     * </li>
     * </ul>
     * 
     * @param <T> from object type.
     * @param from given object to be copied.
     * @return a deep clone of the from object.
     */
    @SuppressWarnings("unchecked")
    public final <T> T deepCopy(T from) {
        return (T)(from == null 
                         ? null 
                         : deepCopy(from, UnEnhancer.getActualClass(from)));
    }

    /** 
     * Convenient method to deep copy the given object 
     * to an instance of the given class using the default behavior.
     * <p>
     * Notes:
     * <ul>
     * <li>
     * Use {@link HibernateBeanReplicator#copy(Object, Class)} instead of this method 
     * if you want to plug in a different (detailed) property filter.
     * </li>
     * </ul>
     * 
     * @param <T> to object type.
     * @param from given object to be copied.
     * @param toClass target class of the returned object.
     * @return an instance of the given class with values deeply copied from the given object.
     */
    public final <T> T deepCopy(Object from, Class<T> toClass) {
        hibernateBeanTransformer.initPropertyFilter(new HibernatePropertyFilter());
        return this.copy(from, toClass);
    }
    
    /** 
     * Convenient method to shallow copy the given object using the default behavior.
     * Shallow copy means skipping those properties that are of type collection, map 
     * or under a package that doesn't start with "java.".
     * <p>
     * Notes:
     * <ul>
     * <li>
     * Use {@link HibernateBeanReplicator#copy(Object, Class)} instead of this method 
     * if you want to plug in a different (detailed) property filter.
     * </li>
     * </ul>
     * 
     * @see HibernatePropertyFilter
     * 
     * @param <T> from object type.
     * @param from given object to be copied.
     * @return a shallow clone of the from object.
     */
    @SuppressWarnings("unchecked")
    public final <T> T shallowCopy(T from) {
        return (T)(from == null 
                         ? null 
                         : shallowCopy(from, UnEnhancer.getActualClass(from)));
    }

    /** 
     * Convenient method to shallow copy the given object 
     * to an instance of the given class using the default behavior.
     * Shallow copy means skipping those properties that are of type collection, map 
     * or under a package that doesn't start with "java.".
     * <p>
     * Notes:
     * <ul>
     * <li>
     * Use {@link HibernateBeanReplicator#copy(Object, Class)} instead of this method 
     * if you want to plug in a different (detailed) property filter.
     * </li>
     * </ul>
     * 
     * @param <T> to object type.
     * @param from given object to be copied.
     * @return an instance of the given class with values shallow copied from the given object.
     */
    public final <T> T shallowCopy(Object from, Class<T> toClass) {
        Set<? extends CollectionPropertyName<?>> emptyCollectionPropertyNameSet = Collections.emptySet();
        Set<Class<?>> emptyEntityBeanClassSet = Collections.emptySet();
        
        hibernateBeanTransformer.initPropertyFilter(
                new HibernatePropertyFilter()
                    .withCollectionPropertyNameSet(emptyCollectionPropertyNameSet)
                    .withEntityBeanClassSet(emptyEntityBeanClassSet));
        return this.copy(from, toClass);
    }
    
    /** 
     * Initializes with one or more custom bean transformer factories 
     * that will be chained together.
     * 
     * @see ChainedCustomBeanTransformer
     */
    public final HibernateBeanReplicator initCustomTransformerFactory(
            CustomBeanTransformerSpi.Factory ...customBeanTransformerFactories) 
    {
        if (customBeanTransformerFactories != null && customBeanTransformerFactories.length > 0) 
        {
            hibernateBeanTransformer.initCustomTransformerFactory(customBeanTransformerFactories.length == 1
                            ? customBeanTransformerFactories[0]
                            : new ChainedCustomBeanTransformer.Factory(customBeanTransformerFactories))
                            ;
        }
        else
            hibernateBeanTransformer.initCustomTransformerFactory(null);
        return this;
    }

    // ========================== Bean Population configuration ========================== 

    /**
     * Returns the property filter that is used to control 
     * what properties get propagated across and what get skipped.
     */
    public final PropertyFilter getPropertyFilter() {
        return hibernateBeanTransformer.getPropertyFilter();
    }
    
    /**
     * Note this method is only applicable if either the {@link #copy(Object)} 
     * or {@link #copy(Object, Class)} is directly invoked, 
     * and is ignored otherwise (ie ignored if deep or shallow copy is invoked instead).
     * 
     * @param propertyFilter is similar to {@link DetailedPropertyFilter} but with a simpler API
     * that is used to control whether a specific JavaBean property should be propagated
     * from a source bean to a target bean.
     * 
     * @return the current object (ie this) for method chaining purposes.
     */
    public final HibernateBeanReplicator initPropertyFilter(PropertyFilter propertyFilter) {
        this.hibernateBeanTransformer.initPropertyFilter(propertyFilter);
        return this;
    }

    /**
     * Note this method is only applicable if either the {@link #copy(Object)} 
     * or {@link #copy(Object, Class)} is directly invoked, 
     * and is ignored otherwise (ie ignored if deep or shallow copy is invoked instead).
     * 
     * @param detailedPropertyFilter is used to control whether a specific JavaBean property
     * should be propagated from the source bean to the target bean.
     * 
     * @return the current object (ie this) for method chaining purposes.
     */
    public final HibernateBeanReplicator initDetailedPropertyFilter(DetailedPropertyFilter detailedPropertyFilter) 
    {
        this.hibernateBeanTransformer.initDetailedPropertyFilter(detailedPropertyFilter);
        return this;
    }
    
    /**
     * Used to configure a call-back 
     * (to produce whatever side-effects deemed necessary) that is invoked
     * after the property value has been retrieved from the source bean, 
     * but before being propagated across to the target bean.
     * 
     * @param beanSourceHandler can be used to act as a call-back 
     * (to produce whatever side-effects deemed necessary)
     * after the property value has been retrieved from the source bean, 
     * but before being propagated across to the target bean.
     * 
     * @return the current object (ie this) for method chaining purposes.
     */
    public final HibernateBeanReplicator initBeanSourceHandler(BeanSourceHandler beanSourceHandler) {
        this.hibernateBeanTransformer.initBeanSourceHandler(beanSourceHandler);
        return this;
    }

    /**
     * Used to control whether debug messages should be logged.
     * 
     * @return the current object (ie this) for method chaining purposes.
     */
    public final HibernateBeanReplicator initDebug(boolean debug) {
        this.hibernateBeanTransformer.initDebug(debug);
        return this;
    }

    /**
     * Used to configure a finder to find the property getter methods of a source JavaBean.
     * 
     * @param readerMethodFinder can be used to find the property getter methods of a source JavaBean.
     * 
     * @return the current object (ie this) for method chaining purposes.
     */
    public final HibernateBeanReplicator initReaderMethodFinder(BeanMethodFinder readerMethodFinder) {
        this.hibernateBeanTransformer.initReaderMethodFinder(readerMethodFinder);
        return this;
    }

    /**
     * Used to configure a collector to collect the property setter methods of a target JavaBean.
     * 
     * @param setterMethodCollector can be used to collect the property setter methods 
     * of a target JavaBean.
     * 
     * @return the current object (ie this) for method chaining purposes.
     */
    public final HibernateBeanReplicator initSetterMethodCollector(
                                            BeanMethodCollector setterMethodCollector) 
    {
        this.hibernateBeanTransformer.initSetterMethodCollector(setterMethodCollector);
        return this;
    }

    public HibernateBeanReplicator initBeanPopulationExceptionHandler(
            BeanPopulationExceptionHandler beanPopulationExceptionHandler) 
    {
        this.hibernateBeanTransformer.initBeanPopulationExceptionHandler(beanPopulationExceptionHandler);
        return this;
    }

    /**
     * Used to conveniently provide the bean population related configuration options as a single 
     * configuration object.
     * 
     * @param baseConfig is used to conveniently group all the other initializable options into a single unit.
     * 
     * @return the current object (ie this) for method chaining purposes.
     */
    public BeanPopulatorBaseSpi initBeanPopulatorBaseConfig(
            BeanPopulatorBaseConfig baseConfig) 
    {
        this.hibernateBeanTransformer.initBeanPopulatorBaseConfig(baseConfig);
        return this;
    }

    public BeanPopulationExceptionHandler getBeanPopulationExceptionHandler() {
        return hibernateBeanTransformer.getBeanPopulationExceptionHandler();
    }

    /**
     * Notes if the returned base config is modified, a subsequent 
     * {@link BeanPopulator#initBeanPopulatorBaseConfig(BeanPopulatorBaseConfig)}
     * needs to be invoked to keep the configuration in sync.
     */
    public BeanPopulatorBaseConfig getBeanPopulatorBaseConfig() {
        return hibernateBeanTransformer.getBeanPopulatorBaseConfig();
    }

    public BeanSourceHandler getBeanSourceHandler() {
        return hibernateBeanTransformer.getBeanSourceHandler();
    }

    public boolean isDebug() {
        return hibernateBeanTransformer.isDebug();
    }

    public DetailedPropertyFilter getDetailedPropertyFilter() {
        return hibernateBeanTransformer.getDetailedPropertyFilter();
    }

    public BeanMethodFinder getReaderMethodFinder() {
        return hibernateBeanTransformer.getReaderMethodFinder();
    }

    public BeanMethodCollector getSetterMethodCollector() {
        return hibernateBeanTransformer.getSetterMethodCollector();
    }
    
    /** 
     * Convenient method to return the current property filter as HibernatePropertyFilter 
     * if the type matches; or null otherwise. 
     */
    public HibernatePropertyFilter getHibernatePropertyFilter() {
        PropertyFilter f = hibernateBeanTransformer.getPropertyFilter();
        return f instanceof HibernatePropertyFilter ? (HibernatePropertyFilter)f : null;
    }
}
