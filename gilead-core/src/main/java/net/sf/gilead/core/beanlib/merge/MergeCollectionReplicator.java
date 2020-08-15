package net.sf.gilead.core.beanlib.merge;

import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.util.Collection;
import java.util.Map;

import net.sf.beanlib.hibernate5.Hibernate5CollectionReplicator;
import net.sf.beanlib.spi.BeanTransformerSpi;
import net.sf.beanlib.spi.replicator.CollectionReplicatorSpi;
import net.sf.gilead.core.PersistenceUtil;
import net.sf.gilead.util.CollectionHelper;

/**
 * Encapsulation of the collection replicator
 *
 * @author bruno.marchesson
 */
public class MergeCollectionReplicator extends Hibernate5CollectionReplicator {

    public static final Factory factory = new Factory();

    /**
     * The associated persistence util
     */
    private PersistenceUtil persistenceUtil;

    /**
     * Constructor
     *
     * @param beanTransformer
     */
    protected MergeCollectionReplicator(BeanTransformerSpi beanTransformer) {
        super(beanTransformer);
    }

    /**
     * @return the persistenceUtil
     */
    public PersistenceUtil getPersistenceUtil() {
        return persistenceUtil;
    }

    /**
     * @param util the persistenceUtil to set
     */
    public void setPersistenceUtil(PersistenceUtil persistenceUtil) {
        this.persistenceUtil = persistenceUtil;
    }

    @Override
    @SuppressWarnings("unchecked")
    protected Object replicate(Object from) {
        // Reset bean local
        BeanlibCache.setProxyInformations(null);
        return super.replicate(from);
    }

    /**
     * Replicate collection override
     */
    @Override
    @SuppressWarnings("unchecked")
    public <V, T> T replicateCollection(Collection<V> from, Class<T> toClass) {
        // Get and reset proxy informations if any
        Map<String, Serializable> proxyInformations = BeanlibCache.getProxyInformations();
        BeanlibCache.setProxyInformations(null);

        // Clone collection
        T collection = super.replicateCollection(from, toClass);

        // Turn into persistent collection if needed
        if (proxyInformations != null) {
            Object parent = BeanlibCache.getToBeanStack().peek();
            return (T) persistenceUtil.createPersistentCollection(parent, proxyInformations, (Collection<?>) collection);
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

    /**
     * Factory for {@link MergeClassBeanReplicator}
     *
     * @author bruno.marchesson
     */
    private static class Factory implements CollectionReplicatorSpi.Factory {
        private Factory() {}

        @Override
        public Hibernate5CollectionReplicator newCollectionReplicatable(BeanTransformerSpi beanTransformer) {
            return new MergeCollectionReplicator(beanTransformer);
        }
    }
}
