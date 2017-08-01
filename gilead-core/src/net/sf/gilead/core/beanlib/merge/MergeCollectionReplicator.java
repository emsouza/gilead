/**
 *
 */
package net.sf.gilead.core.beanlib.merge;

import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.util.Collection;
import java.util.Map;

import net.sf.beanlib.hibernate4.Hibernate4CollectionReplicator;
import net.sf.beanlib.spi.BeanTransformerSpi;
import net.sf.beanlib.spi.replicator.CollectionReplicatorSpi;
import net.sf.gilead.core.PersistenceUtil;
import net.sf.gilead.util.CollectionHelper;

/**
 * Encapsulation of the collection replicator
 *
 * @author bruno.marchesson
 */
public class MergeCollectionReplicator extends Hibernate4CollectionReplicator {

    // ----
    // Factory
    // ----
    public static final Factory factory = new Factory();

    /**
     * Factory for {@link MergeClassBeanReplicator}
     *
     * @author bruno.marchesson
     */
    private static class Factory implements CollectionReplicatorSpi.Factory {
        private Factory() {}

        @Override
        public Hibernate4CollectionReplicator newCollectionReplicatable(BeanTransformerSpi beanTransformer) {
            return new MergeCollectionReplicator(beanTransformer);
        }
    }

    public static Hibernate4CollectionReplicator newCollectionReplicatable(BeanTransformerSpi beanTransformer) {
        return factory.newCollectionReplicatable(beanTransformer);
    }

    // ----
    // Attributes
    // ----
    /**
     * The associated persistence util
     */
    private PersistenceUtil _persistenceUtil;

    // ----
    // Properties
    // ----
    /**
     * @return the _persistenceUtil
     */
    public PersistenceUtil getPersistenceUtil() {
        return _persistenceUtil;
    }

    /**
     * @param util the _persistenceUtil to set
     */
    public void setPersistenceUtil(PersistenceUtil util) {
        _persistenceUtil = util;
    }

    // ----
    // Constructor
    // ----
    /**
     * Constructor
     *
     * @param beanTransformer
     */
    protected MergeCollectionReplicator(BeanTransformerSpi beanTransformer) {
        super(beanTransformer);
    }

    // ----
    // Overrides
    // ----
    @Override
    protected Object replicate(Object from) {
        // Reset bean local
        //
        BeanlibCache.setProxyInformations(null);

        return super.replicate(from);
    }

    /**
     * Replicate collection override
     */
    @SuppressWarnings("unchecked")
    @Override
    public <V, T> T replicateCollection(Collection<V> from, Class<T> toClass) {
        // Get and reset proxy informations if any
        //
        Map<String, Serializable> proxyInformations = BeanlibCache.getProxyInformations();
        BeanlibCache.setProxyInformations(null);

        // Clone collection
        T collection = super.replicateCollection(from, toClass);

        // Turn into persistent collection if needed
        if (proxyInformations != null) {
            Object parent = BeanlibCache.getToBeanStack().peek();
            return (T) _persistenceUtil.createPersistentCollection(parent, proxyInformations, (Collection<?>) collection);
        } else {
            return collection;
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    protected <T> Collection<T> createToCollection(Collection<T> from)
            throws InstantiationException, IllegalAccessException, SecurityException, NoSuchMethodException, InvocationTargetException {
        // Unmodifiable collection handling
        if (CollectionHelper.isUnmodifiableCollection(from)) {
            from = (Collection<T>) CollectionHelper.getUnmodifiableCollection(from);
        }
        return super.createToCollection(from);
    }
}
