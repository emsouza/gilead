/**
 *
 */
package net.sf.gilead.core.beanlib.clone;

import java.lang.reflect.InvocationTargetException;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Set;

import net.sf.beanlib.hibernate4.Hibernate4CollectionReplicator;
import net.sf.beanlib.spi.BeanTransformerSpi;
import net.sf.beanlib.spi.replicator.CollectionReplicatorSpi;
import net.sf.gilead.core.IPersistenceUtil;
import net.sf.gilead.util.CollectionHelper;

/**
 * @author bruno.marchesson
 */
public class CloneCollectionReplicator extends Hibernate4CollectionReplicator {

    // ----
    // Factory
    // ----
    public static final Factory factory = new Factory();

    /**
     * Factory for {@link CloneClassBeanReplicator}
     * 
     * @author bruno.marchesson
     */
    private static class Factory implements CollectionReplicatorSpi.Factory {
        private Factory() {}

        @Override
        public CloneCollectionReplicator newCollectionReplicatable(BeanTransformerSpi beanTransformer) {
            return new CloneCollectionReplicator(beanTransformer);
        }
    }

    public static CloneCollectionReplicator newCollectionReplicatable(BeanTransformerSpi beanTransformer) {
        return factory.newCollectionReplicatable(beanTransformer);
    }

    protected CloneCollectionReplicator(BeanTransformerSpi beanTransformer) {
        super(beanTransformer);
    }

    // ----
    // Attributes
    // ----
    /**
     * Persistent util
     */
    protected IPersistenceUtil _persistenceUtil;

    // ----
    // Properties
    // ----
    /**
     * @return the _persistenceUtil
     */
    public IPersistenceUtil getPersistenceUtil() {
        return _persistenceUtil;
    }

    /**
     * @param util the _persistenceUtil to set
     */
    public void setPersistenceUtil(IPersistenceUtil util) {
        _persistenceUtil = util;
    }

    // -------------------------------------------------------------------------
    //
    // Public interface
    //
    // -------------------------------------------------------------------------
    @Override
    @SuppressWarnings("unchecked")
    protected <T> T createToInstance(Object from, Class<T> toClass)
            throws InstantiationException, IllegalAccessException, SecurityException, NoSuchMethodException {
        // Unmodifiable collection handling
        //
        if (CollectionHelper.isUnmodifiableCollection(from)) {
            from = CollectionHelper.getUnmodifiableCollection(from);
            toClass = (Class<T>) from.getClass();
        }
        return super.createToInstance(from, toClass);
    }

    @Override
    protected <T> Collection<T> createToCollection(Collection<T> from)
            throws InstantiationException, IllegalAccessException, SecurityException, NoSuchMethodException, InvocationTargetException {
        if (from instanceof Set) {
            // PersistentSet with LinkHashSet handling
            //
            Collection<?> underlyingSet = _persistenceUtil.getUnderlyingCollection(from);
            if ((underlyingSet != null) && (underlyingSet instanceof LinkedHashSet)) {
                return new LinkedHashSet<T>();
            }
        }

        // Fallback to normal behavior
        return super.createToCollection(from);
    }
}
