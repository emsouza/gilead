/**
 *
 */
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

    // ----
    // Factory
    // ----
    public static final Factory factory = new Factory();

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

    public static Hibernate4MapReplicator newMapReplicatable(BeanTransformerSpi beanTransformer) {
        return factory.newMapReplicatable(beanTransformer);
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
    protected MergeMapReplicator(BeanTransformerSpi beanTransformer) {
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
     * Map replication override
     */
    @SuppressWarnings("unchecked")
    @Override
    public <K, V, T> T replicateMap(Map<K, V> from, Class<T> toClass) {
        LOGGER.trace("Merge map from " + from + " to class " + toClass);

        // Get and reset persistent collection class if any
        //
        Map<String, Serializable> proxyInformations = BeanlibCache.getProxyInformations();
        BeanlibCache.setProxyInformations(null);

        // AS Object BlazeDS map handling
        //
        if (from.getClass().getName().endsWith(".ASObject")) {
            // Turn it into regular hash map
            //
            String type = null;
            try {
                type = (String) from.getClass().getMethod("getType", (Class<?>[]) null).invoke(from, (Object[]) null);
            } catch (Exception e) {
                type = e.getMessage();
            }
            LOGGER.warn("Invalid map : " + from.toString() + " for type " + type);
            toClass = (Class<T>) java.util.HashMap.class;
        }

        // Clone map
        //
        T map = super.replicateMap(from, toClass);

        // Turn into persistent map if needed
        //
        if (proxyInformations != null) {
            Object parent = BeanlibCache.getToBeanStack().peek();
            return (T) _persistenceUtil.createPersistentMap(parent, proxyInformations, (Map<?, ?>) map);
        } else {
            return map;
        }
    }
}
