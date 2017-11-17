package net.sf.gilead.core.beanlib.merge;

import java.io.Serializable;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.sf.beanlib.hibernate4.Hibernate4MapReplicator;
import net.sf.beanlib.spi.BeanTransformerSpi;
import net.sf.beanlib.spi.replicator.MapReplicatorSpi;
import net.sf.gilead.core.PersistenceUtil;

/**
 * Encapsulation of the collection replicator
 *
 * @author bruno.marchesson
 */
public class MergeMapReplicator extends Hibernate4MapReplicator {

    private static final Logger LOGGER = LoggerFactory.getLogger(MergeMapReplicator.class);

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
    protected MergeMapReplicator(BeanTransformerSpi beanTransformer) {
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
     * Map replication override
     */
    @Override
    @SuppressWarnings("unchecked")
    public <K, V, T> T replicateMap(Map<K, V> from, Class<T> toClass) {
        LOGGER.trace("Merge map from " + from + " to class " + toClass);

        // Get and reset persistent collection class if any
        Map<String, Serializable> proxyInformations = BeanlibCache.getProxyInformations();
        BeanlibCache.setProxyInformations(null);

        // Clone map
        T map = super.replicateMap(from, toClass);

        // Turn into persistent map if needed
        if (proxyInformations != null) {
            Object parent = BeanlibCache.getToBeanStack().peek();
            return (T) persistenceUtil.createPersistentMap(parent, proxyInformations, (Map<?, ?>) map);
        } else {
            return map;
        }
    }

    /**
     * Factory for {@link MergeClassBeanReplicator}
     *
     * @author bruno.marchesson
     */
    private static class Factory implements MapReplicatorSpi.Factory {
        private Factory() {}

        @Override
        public Hibernate4MapReplicator newMapReplicatable(BeanTransformerSpi beanTransformer) {
            return new MergeMapReplicator(beanTransformer);
        }
    }
}
