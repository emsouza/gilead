package net.sf.gilead.core.beanlib.clone;

import net.sf.beanlib.hibernate.HibernateBeanReplicator;
import net.sf.beanlib.hibernate4.Hibernate4BeanTransformer;
import net.sf.beanlib.hibernate4.Hibernate4BlobReplicator;
import net.sf.beanlib.hibernate4.Hibernate4MapReplicator;
import net.sf.beanlib.spi.BeanTransformerSpi;
import net.sf.beanlib.spi.CustomBeanTransformerSpi;
import net.sf.gilead.core.PersistenceUtil;
import net.sf.gilead.core.beanlib.ClassMapper;
import net.sf.gilead.core.beanlib.finder.FastPrivateReaderMethodFinder;
import net.sf.gilead.core.beanlib.finder.FastPrivateSetterMethodCollector;
import net.sf.gilead.core.beanlib.transformer.CustomTransformersFactory;
import net.sf.gilead.core.store.ProxyStore;

/**
 * Hibernate Bean Replicator override to inject the class mapper used for clone to a different class.
 * 
 * @author bruno.marchesson
 */
public class CloneBeanReplicator extends HibernateBeanReplicator {

    public CloneBeanReplicator(ClassMapper classMapper, PersistenceUtil persistenceUtil, ProxyStore proxyStore) {
        super(newBeanTransformer(classMapper, persistenceUtil, proxyStore));
    }

    private static Hibernate4BeanTransformer newBeanTransformer(ClassMapper classMapper, PersistenceUtil persistenceUtil, ProxyStore proxyStore) {
        Hibernate4BeanTransformer transformer = new Hibernate4BeanTransformer();

        // Custom collection replicator
        transformer.initCollectionReplicatableFactory(CloneCollectionReplicator.factory);

        // Set associated PersistenceUtil
        ((CloneCollectionReplicator) transformer.getCollectionReplicatable()).setPersistenceUtil(persistenceUtil);

        transformer.initMapReplicatableFactory(Hibernate4MapReplicator.getFactory());
        transformer.initBlobReplicatableFactory(Hibernate4BlobReplicator.getFactory());

        // Custom bean replicatable
        transformer.initBeanReplicatableFactory(CloneClassBeanReplicator.factory);

        // Set the associated class mapper
        ((CloneClassBeanReplicator) transformer.getBeanReplicatable()).setClassMapper(classMapper);
        ((CloneClassBeanReplicator) transformer.getBeanReplicatable()).setPersistenceUtil(persistenceUtil);

        // Custom transformers (timestamp handling)
        transformer.initCustomTransformerFactory(new CustomBeanTransformerSpi.Factory() {
            @Override
            public CustomBeanTransformerSpi newCustomBeanTransformer(final BeanTransformerSpi beanTransformer) {
                return CustomTransformersFactory.getInstance().createUnionCustomBeanTransformerForClone(beanTransformer);
            }
        });

        // Lazy properties handling
        transformer.initDetailedPropertyFilter(new ClonePropertyFilter(persistenceUtil, proxyStore));

        // Protected and private setter collection
        transformer.initSetterMethodCollector(new FastPrivateSetterMethodCollector());
        transformer.initReaderMethodFinder(new FastPrivateReaderMethodFinder());

        return transformer;
    }
}
