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
package net.sf.beanlib.provider;

import java.util.IdentityHashMap;
import java.util.Map;

import net.jcip.annotations.NotThreadSafe;
import net.sf.beanlib.BeanlibException;
import net.sf.beanlib.PropertyInfo;
import net.sf.beanlib.provider.replicator.ArrayReplicator;
import net.sf.beanlib.provider.replicator.BeanReplicator;
import net.sf.beanlib.provider.replicator.CalendarReplicator;
import net.sf.beanlib.provider.replicator.CollectionReplicator;
import net.sf.beanlib.provider.replicator.DateReplicator;
import net.sf.beanlib.provider.replicator.ImmutableReplicator;
import net.sf.beanlib.provider.replicator.MapReplicator;
import net.sf.beanlib.provider.replicator.ReplicatorTemplate;
import net.sf.beanlib.provider.replicator.UnsupportedBlobReplicator;
import net.sf.beanlib.spi.BeanMethodCollector;
import net.sf.beanlib.spi.BeanMethodFinder;
import net.sf.beanlib.spi.BeanPopulationExceptionHandler;
import net.sf.beanlib.spi.BeanPopulatorBaseConfig;
import net.sf.beanlib.spi.BeanPopulatorBaseSpi;
import net.sf.beanlib.spi.BeanPopulatorSpi;
import net.sf.beanlib.spi.BeanSourceHandler;
import net.sf.beanlib.spi.BeanTransformerSpi;
import net.sf.beanlib.spi.ChainedCustomBeanTransformer;
import net.sf.beanlib.spi.CustomBeanTransformerSpi;
import net.sf.beanlib.spi.DetailedPropertyFilter;
import net.sf.beanlib.spi.PropertyFilter;
import net.sf.beanlib.spi.TrivialCustomBeanTransformerFactories;
import net.sf.beanlib.spi.replicator.ArrayReplicatorSpi;
import net.sf.beanlib.spi.replicator.BeanReplicatorSpi;
import net.sf.beanlib.spi.replicator.BlobReplicatorSpi;
import net.sf.beanlib.spi.replicator.CalendarReplicatorSpi;
import net.sf.beanlib.spi.replicator.CollectionReplicatorSpi;
import net.sf.beanlib.spi.replicator.DateReplicatorSpi;
import net.sf.beanlib.spi.replicator.ImmutableReplicatorSpi;
import net.sf.beanlib.spi.replicator.MapReplicatorSpi;

/**
 * Default implementation of {@link BeanTransformerSpi}.
 * 
 * A Bean Transformer can be used to transform object input typically in the form of a property
 * that follows the JavaBean getter/setter convention, on a best attempt basis,
 * into an output object typically used to populate the corresponding property of a target JavaBean.
 * <p>
 * This class extends {@link ReplicatorTemplate} as the implementation typically 
 * transforms a JavaBean property by replicating it into a separate instance.
 * The replication is typically recursive in that the whole object graph of the input object is
 * replicated into an equivalent output object graph, resolving circular references as necessary.
 * However, the exact behavior of the replication process including<ul>
 * <li>whether it's recursive or not; and 
 * <li>to what extent the input object graph should be traversed and/or replicated</li>
 * </ul> 
 * can be controlled and/or supplemented by the client code via various options:
 * <p>
 * <ol>
 * <li>All the configurable options of {@link BeanPopulatorBaseSpi} are available, as
 * the replication of JavaBean properties inevitably involves bean population.</li>
 * <p>
 * <li>A Bean Transformer adopts a best effort approach to perform object transformation and replication.
 * The default implementations for replicating the common data types such as
 * collection, map, dates, etc. can be selectively overridden via 
 * the replicator factory initialization methods:
 * <p>
 * <ul>
 * <table>
 *   <tr><th width="20%" align="center">Type</th><th>Configuration method</th>
 *   <tr><td><li>Immutable (such as enum)</li></td>
 *   <td>{@link #initImmutableReplicatableFactory(net.sf.beanlib.spi.replicator.ImmutableReplicatorSpi.Factory) 
 *          #initImmutableReplicatableFactory(ImmutableReplicatorSpi.Factory)}</td>
 *   <tr><td><li>Collection</li></td>
 *   <td>{@link #initCollectionReplicatableFactory(net.sf.beanlib.spi.replicator.CollectionReplicatorSpi.Factory) 
 *          #initCollectionReplicatableFactory(CollectionReplicatorSpi.Factory)}</td>
 *   <tr><td><li>Map</li></td>
 *   <td>{@link #initMapReplicatableFactory(net.sf.beanlib.spi.replicator.MapReplicatorSpi.Factory) 
 *          #initMapReplicatableFactory(MapReplicatorSpi.Factory)}</td>
 *   <tr><td><li>Primitive array</li></td>
 *   <td>{@link #initArrayReplicatableFactory(net.sf.beanlib.spi.replicator.ArrayReplicatorSpi.Factory) 
 *          #initArrayReplicatableFactory(ArrayReplicatorSpi.Factory)}</td>
 *   <tr><td><li>Blob</li></td>
 *   <td>{@link #initBlobReplicatableFactory(net.sf.beanlib.spi.replicator.BlobReplicatorSpi.Factory)
 *          #initBlobReplicatableFactory(BlobReplicatorSpi.Factory)}</td>
 *   <tr><td><li>Date</li></td>
 *   <td>{@link #initDateReplicatableFactory(net.sf.beanlib.spi.replicator.DateReplicatorSpi.Factory)
 *          #initDateReplicatableFactory(DateReplicatorSpi.Factory)}</td>
 *   <tr><td><li>JavaBean (with no-arg constructor)</li></td>
 *   <td>{@link #initBeanReplicatableFactory(net.sf.beanlib.spi.replicator.BeanReplicatorSpi.Factory)
 *          #initBeanReplicatableFactory(BeanReplicatorSpi.Factory))}</td>
 * </table>
 * </ul>
 * </li>
 * <p>
 * <li>For anything else that the existing implementation fails to tranform, client can provide
 * one or multiple custom transformer factories via the constructor 
 * {@link #BeanTransformer(net.sf.beanlib.spi.CustomBeanTransformerSpi.Factory...)
 * new BeanTransformer(CustomBeanTransformerSpi.Factory...)}.
 * </li>
 * </ol>
 * @see CustomBeanTransformerSpi
 * 
 * @author Joe D. Velopar
 */
@NotThreadSafe
public class BeanTransformer extends ReplicatorTemplate implements BeanTransformerSpi
{
    /** Constructs with the default {@link BeanPopulator#factory}. */
    public BeanTransformer() {
        this(BeanPopulator.factory);
    }
    
    /** 
     * Convenient constructor that both defaults to use {@link BeanPopulator#factory},
     * and allows plugging in one or more custom bean transformer factories 
     * that will be chained together.
     * 
     * @see ChainedCustomBeanTransformer
     */
    public BeanTransformer(final CustomBeanTransformerSpi.Factory ... customBeanTransformerFactories) {
        this(BeanPopulator.factory);
        
        if (customBeanTransformerFactories != null && customBeanTransformerFactories.length > 0) 
        {
            this.initCustomTransformerFactory(customBeanTransformerFactories.length == 1
                            ? customBeanTransformerFactories[0]
                            : new ChainedCustomBeanTransformer.Factory(customBeanTransformerFactories))
                            ;
        }
        else
            this.initCustomTransformerFactory(null);
    }
    
    /**
     * Bean populator factory, which is used to create
     * a bean populator which can then be used to determine 
     * whether a specific JavaBean property should be propagated 
     * from a source bean to a target bean.
     */
    private final BeanPopulatorSpi.Factory beanPopulatorFactory;
    
    /**
     * Constructs with the given bean populator factory, which is used to create
     * a bean populator which can then be used to determine 
     * whether a specific JavaBean property should be propagated 
     * from a source bean to a target bean.
     */
    protected BeanTransformer(BeanPopulatorSpi.Factory beanPopulatorFactory) {
        this.beanPopulatorFactory = beanPopulatorFactory;
    }
    
    public BeanPopulatorSpi.Factory getBeanPopulatorSpiFactory() {
        return beanPopulatorFactory;
    }
    
    /** Used to contains those objects that have been replicated. */
    private Map<Object,Object> clonedMap = new IdentityHashMap<Object,Object>();

    /**
     * Used to contain all the configuration options as a single configuration object. 
     */
    private BeanPopulatorBaseConfig baseConfig = new BeanPopulatorBaseConfig();
    
    /** Custom Transformer.  Defaults is a NO_OP. */
    private CustomBeanTransformerSpi customTransformer = TrivialCustomBeanTransformerFactories.getNoopCustomTransformer();
    
    /**
     * Replicator for immutables.
     */
    private ImmutableReplicatorSpi immutableReplicatable = ImmutableReplicator.newImmutableReplicatable(this);

    /**
     * Replicator for collections.
     */
    private CollectionReplicatorSpi collectionReplicatable = CollectionReplicator.newCollectionReplicatable(this);
    
    /**
     * Replicator for maps.
     */
    private MapReplicatorSpi mapReplicatable = MapReplicator.newMapReplicatable(this);
    
    /**
     * Replicator for arrays.
     */
    private ArrayReplicatorSpi arrayReplicatable = ArrayReplicator.newArrayReplicatable(this);

    /**
     * Replicator for blobs.
     */
    private BlobReplicatorSpi blobReplicatable = UnsupportedBlobReplicator.newBlobReplicatable(this);

    /**
     * Replicator for dates.
     */
    private DateReplicatorSpi dateReplicatable = DateReplicator.newDateReplicatable(this);

    /**
     * Replicator for calendars.
     */
    private CalendarReplicatorSpi calendarReplicatable = CalendarReplicator.newCalendarReplicatable(this);
    
    /**
     * Replicator for JavaBeans.
     */
    private BeanReplicatorSpi beanReplicatable = new BeanReplicator(this);
    
    public final void reset() {
        clonedMap = new IdentityHashMap<Object,Object>();
    }
    
    @Override
    public final <T> T transform(Object from, Class<T> toClass, PropertyInfo propertyInfo) 
    {
        try {
            if (customTransformer.isTransformable(from, toClass, propertyInfo))
                return customTransformer.transform(from, toClass, propertyInfo);
            return replicate(from, toClass);
        } catch (SecurityException e) {
            throw new BeanlibException(e);
        }
    }
    
    public final BeanTransformer initCustomTransformerFactory(CustomBeanTransformerSpi.Factory customTransformer) {
        this.customTransformer = customTransformer.newCustomBeanTransformer(this);
        return this;
    }
    
    public final BeanTransformer initPropertyFilter(PropertyFilter propertyFilter) {
        baseConfig.setPropertyFilter(propertyFilter);
        return this;
    }

    public final BeanTransformer initBeanSourceHandler(BeanSourceHandler beanSourceHandler) {
        baseConfig.setBeanSourceHandler(beanSourceHandler);
        return this;
    }

    public final BeanTransformer initDebug(boolean debug) {
        baseConfig.setDebug(debug);
        return this;
    }

    public final BeanTransformer initDetailedPropertyFilter(DetailedPropertyFilter detailedPropertyFilter) 
    {
        baseConfig.setDetailedPropertyFilter(detailedPropertyFilter);
        return this;
    }

    public final BeanTransformer initReaderMethodFinder(BeanMethodFinder readerMethodFinder) {
        baseConfig.setReaderMethodFinder(readerMethodFinder);
        return this;
    }

    public final BeanTransformer initSetterMethodCollector(BeanMethodCollector setterMethodCollector) {
        baseConfig.setSetterMethodCollector(setterMethodCollector);
        return this;
    }
    
    public BeanTransformer initCollectionReplicatableFactory(CollectionReplicatorSpi.Factory factory) {
        this.collectionReplicatable = factory.newCollectionReplicatable(this);
        return this;
    }
    
    public CollectionReplicatorSpi getCollectionReplicatable() {
        return collectionReplicatable;
    }
    
    public BeanTransformer initMapReplicatableFactory(MapReplicatorSpi.Factory factory) {
        this.mapReplicatable = factory.newMapReplicatable(this);
        return this;
    }
    
    public MapReplicatorSpi getMapReplicatable() {
        return mapReplicatable;
    }
    
    @SuppressWarnings("unchecked")
    public <K,V> Map<K,V> getClonedMap() {
        return (Map<K,V>)clonedMap;
    }
    
    public BeanTransformer initImmutableReplicatableFactory(ImmutableReplicatorSpi.Factory immutableReplicatableFactory) 
    {
        this.immutableReplicatable = immutableReplicatableFactory.newImmutableReplicatable(this);
        return this;
    }

    public ImmutableReplicatorSpi getImmutableReplicatable() {
        return immutableReplicatable;
    }

    public BeanTransformer initArrayReplicatableFactory(ArrayReplicatorSpi.Factory arrayReplicatableFactory) {
        this.arrayReplicatable = arrayReplicatableFactory.newArrayReplicatable(this);
        return this;
    }

    public ArrayReplicatorSpi getArrayReplicatable() {
        return arrayReplicatable;
    }

    public BeanTransformer initBlobReplicatableFactory(BlobReplicatorSpi.Factory blobReplicatableFactory) {
        this.blobReplicatable = blobReplicatableFactory.newBlobReplicatable(this);
        return this;
    }

    public BlobReplicatorSpi getBlobReplicatable() {
        return blobReplicatable;
    }

    public BeanTransformer initBeanReplicatableFactory(BeanReplicatorSpi.Factory objectReplicatableFactory) {
        this.beanReplicatable = objectReplicatableFactory.newBeanReplicatable(this);
        return this;
    }

    public BeanReplicatorSpi getBeanReplicatable() {
        return beanReplicatable;
    }

    public BeanTransformerSpi initDateReplicatableFactory(DateReplicatorSpi.Factory dateReplicatableFactory) {
        this.dateReplicatable = dateReplicatableFactory.newDateReplicatable(this);
        return this;
    }

    public BeanTransformerSpi initCalendarReplicatableFactory(CalendarReplicatorSpi.Factory calendarReplicatableFactory) {
        this.calendarReplicatable = calendarReplicatableFactory.newCalendarReplicatable(this);
        return this;
    }

    public DateReplicatorSpi getDateReplicatable() {
        return dateReplicatable;
    }

    public CalendarReplicatorSpi getCalendarReplicatable() {
        return calendarReplicatable;
    }

    public BeanTransformer initBeanPopulationExceptionHandler(BeanPopulationExceptionHandler beanPopulationExceptionHandler) {
        baseConfig.setBeanPopulationExceptionHandler(beanPopulationExceptionHandler);
        return this;
    }

    public BeanTransformerSpi initBeanPopulatorBaseConfig(BeanPopulatorBaseConfig baseConfig) 
    {
        this.baseConfig = baseConfig;
        return this;
    }

    public BeanPopulatorBaseConfig getBeanPopulatorBaseConfig() {
        return baseConfig;
    }

    public PropertyFilter getPropertyFilter() { 
        return baseConfig.getPropertyFilter(); 
    }

    public BeanPopulationExceptionHandler getBeanPopulationExceptionHandler() {
        return baseConfig.getBeanPopulationExceptionHandler();
    }

    public BeanSourceHandler getBeanSourceHandler() {
        return baseConfig.getBeanSourceHandler();
    }

    public boolean isDebug() {
        return baseConfig.isDebug();
    }

    public DetailedPropertyFilter getDetailedPropertyFilter() {
        return baseConfig.getDetailedPropertyFilter();
    }

    public BeanMethodFinder getReaderMethodFinder() {
        return baseConfig.getReaderMethodFinder();
    }

    public BeanMethodCollector getSetterMethodCollector() {
        return baseConfig.getSetterMethodCollector();
    }

    public CustomBeanTransformerSpi getCustomBeanTransformer() { return this.customTransformer; }
}
