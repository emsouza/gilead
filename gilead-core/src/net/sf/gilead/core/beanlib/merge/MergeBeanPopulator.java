package net.sf.gilead.core.beanlib.merge;

import net.sf.beanlib.provider.BeanPopulator;
import net.sf.beanlib.spi.BeanTransformerSpi;
import net.sf.beanlib.spi.CustomBeanTransformerSpi;
import net.sf.beanlib.spi.DetailedPropertyFilter;
import net.sf.gilead.core.PersistenceUtil;
import net.sf.gilead.core.beanlib.ClassMapper;
import net.sf.gilead.core.beanlib.finder.FastPrivateReaderMethodFinder;
import net.sf.gilead.core.beanlib.finder.FastPrivateSetterMethodCollector;
import net.sf.gilead.core.beanlib.transformer.CustomTransformersFactory;
import net.sf.gilead.core.store.ProxyStore;

/**
 * Bean populator for merge operation
 *
 * @author bruno.marchesson
 */
public class MergeBeanPopulator {
    /**
     * Create a new populator for merge operation
     *
     * @param from the source class
     * @param to the target class
     * @param classMapper the associated class mapper
     * @return the created populator
     */
    public static BeanPopulator newBeanPopulator(Object from, Object to, ClassMapper classMapper, PersistenceUtil persistenceUtil,
            ProxyStore proxyStore) {
        BeanPopulator replicator = new BeanPopulator(from, to);

        // Change bean class replicator
        BeanTransformerSpi transformer = (BeanTransformerSpi) replicator.getTransformer();
        transformer.initBeanReplicatableFactory(MergeClassBeanReplicator.factory);
        ((MergeClassBeanReplicator) transformer.getBeanReplicatable()).setClassMapper(classMapper);
        ((MergeClassBeanReplicator) transformer.getBeanReplicatable()).setPersistenceUtil(persistenceUtil);
        ((MergeClassBeanReplicator) transformer.getBeanReplicatable()).setProxyStore(proxyStore);

        transformer.initCollectionReplicatableFactory(MergeCollectionReplicator.factory);
        ((MergeCollectionReplicator) transformer.getCollectionReplicatable()).setPersistenceUtil(persistenceUtil);

        transformer.initMapReplicatableFactory(MergeMapReplicator.factory);
        ((MergeMapReplicator) transformer.getMapReplicatable()).setPersistenceUtil(persistenceUtil);

        // Custom transformers (timestamp handling)
        transformer.initCustomTransformerFactory(new CustomBeanTransformerSpi.Factory() {
            @Override
            public CustomBeanTransformerSpi newCustomBeanTransformer(final BeanTransformerSpi beanTransformer) {
                return CustomTransformersFactory.getInstance().createUnionCustomBeanTransformerForMerge(beanTransformer);
            }
        });

        // Lazy properties handling
        DetailedPropertyFilter hibernateFilter = new MergePropertyFilter(persistenceUtil, proxyStore);
        replicator.initDetailedPropertyFilter(hibernateFilter);

        // Merge based on protected and private setters
        replicator.initSetterMethodCollector(new FastPrivateSetterMethodCollector());
        replicator.initReaderMethodFinder(new FastPrivateReaderMethodFinder());

        return replicator;
    }
}
