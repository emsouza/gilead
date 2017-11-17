package net.sf.gilead.core.beanlib.clone;

import java.lang.reflect.InvocationTargetException;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Set;

import net.sf.beanlib.hibernate4.Hibernate4CollectionReplicator;
import net.sf.beanlib.spi.BeanTransformerSpi;
import net.sf.beanlib.spi.replicator.CollectionReplicatorSpi;
import net.sf.gilead.core.PersistenceUtil;
import net.sf.gilead.util.CollectionHelper;

/**
 * @author bruno.marchesson
 */
public class CloneCollectionReplicator extends Hibernate4CollectionReplicator {

    public static final Factory factory = new Factory();

    protected CloneCollectionReplicator(BeanTransformerSpi beanTransformer) {
        super(beanTransformer);
    }

    /**
     * Persistent util
     */
    protected PersistenceUtil persistenceUtil;

    /**
     * @return the _persistenceUtil
     */
    public PersistenceUtil getPersistenceUtil() {
        return persistenceUtil;
    }

    /**
     * @param util the _persistenceUtil to set
     */
    public void setPersistenceUtil(PersistenceUtil persistenceUtil) {
        this.persistenceUtil = persistenceUtil;
    }

    @Override
    @SuppressWarnings("unchecked")
    protected <T> T createToInstance(Object from, Class<T> toClass)
            throws InstantiationException, IllegalAccessException, SecurityException, NoSuchMethodException {
        // Unmodifiable collection handling
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
            Collection<?> underlyingSet = persistenceUtil.getUnderlyingCollection(from);
            if ((underlyingSet != null) && (underlyingSet instanceof LinkedHashSet)) {
                return new LinkedHashSet<>();
            }
        }

        // Fallback to normal behavior
        return super.createToCollection(from);
    }

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
}
