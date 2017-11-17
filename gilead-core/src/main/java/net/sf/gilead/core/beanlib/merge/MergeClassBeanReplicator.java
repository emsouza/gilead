package net.sf.gilead.core.beanlib.merge;

import java.io.Serializable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.sf.beanlib.hibernate.UnEnhancer;
import net.sf.beanlib.hibernate4.Hibernate4JavaBeanReplicator;
import net.sf.beanlib.spi.BeanTransformerSpi;
import net.sf.beanlib.spi.replicator.BeanReplicatorSpi;
import net.sf.gilead.core.PersistenceUtil;
import net.sf.gilead.core.annotations.AnnotationsManager;
import net.sf.gilead.core.beanlib.ClassMapper;
import net.sf.gilead.core.store.ProxyStore;
import net.sf.gilead.exception.NotPersistentObjectException;
import net.sf.gilead.exception.TransientObjectException;

/**
 * Bean replicator with different from and to classes for merge operation
 *
 * @author bruno.marchesson
 */
public class MergeClassBeanReplicator extends Hibernate4JavaBeanReplicator {

    private static final Logger LOGGER = LoggerFactory.getLogger(MergeClassBeanReplicator.class);

    public static final Factory factory = new Factory();

    /**
     * The class mapper (can be null)
     */
    private ClassMapper classMapper;

    /**
     * The persistent util class
     */
    private PersistenceUtil persistenceUtil;

    /**
     * The current proxy store
     */
    private ProxyStore proxyStore;

    protected MergeClassBeanReplicator(BeanTransformerSpi beanTransformer) {
        super(beanTransformer);
    }

    /**
     * @return the Class Mapper
     */
    public ClassMapper getClassMapper() {
        return classMapper;
    }

    /**
     * @param mapper the classMapper to set
     */
    public void setClassMapper(ClassMapper mapper) {
        classMapper = mapper;
    }

    /**
     * @return the persistenceUtil
     */
    public PersistenceUtil getPersistenceUtil() {
        return persistenceUtil;
    }

    /**
     * @param util the persistence Util to set
     */
    public void setPersistenceUtil(PersistenceUtil persistenceUtil) {
        this.persistenceUtil = persistenceUtil;
    }

    /**
     * @return the proxy store
     */
    public ProxyStore getProxyStore() {
        return proxyStore;
    }

    /**
     * @param store the proxy Store to set
     */
    public void setProxyStore(ProxyStore proxyStore) {
        this.proxyStore = proxyStore;
    }

    @Override
    public <V extends Object, T extends Object> T replicateBean(V from, java.lang.Class<T> toClass) {
        // Reset bean local
        BeanlibCache.setProxyInformations(null);

        // Force persistence map computation (useful for subclass)
        persistenceUtil.isPersistentPojo(from);

        // Add current bean to stack
        BeanlibCache.getFromBeanStack().push(from);
        T result = super.replicateBean(from, toClass);
        BeanlibCache.getFromBeanStack().pop();

        return result;
    }

    @Override
    @SuppressWarnings("unchecked")
    protected <T extends Object> T createToInstance(Object from, java.lang.Class<T> toClass)
            throws InstantiationException, IllegalAccessException, SecurityException, NoSuchMethodException {
        // Clone mapper indirection
        if (classMapper != null) {
            Class<T> sourceClass = (Class<T>) classMapper.getSourceClass(UnEnhancer.unenhanceClass(from.getClass()));
            if (sourceClass != null) {
                LOGGER.trace("Creating mapped class " + sourceClass);
                toClass = sourceClass;
            } else {
                LOGGER.trace("Creating merged class " + toClass);
            }
        }

        // Get real target class
        if (toClass.isInterface()) {
            // Keep the from class
            toClass = (Class<T>) from.getClass();
        }

        T result = null;

        // Proxy informations
        if ((AnnotationsManager.hasGileadAnnotations(from.getClass())) || (AnnotationsManager.hasGileadAnnotations(toClass))) {
            // Load entity
            try {
                Serializable id = persistenceUtil.getId(from, toClass);
                toClass = chooseClass(from.getClass(), toClass);
                result = (T) persistenceUtil.load(id, toClass);
            } catch (NotPersistentObjectException e) {
                LOGGER.warn("Not an hibernate class (" + toClass + ") : annotated values will not be restored.");
                return (T) from;
            } catch (TransientObjectException e) {
                LOGGER.warn("Transient value of class " + toClass + " : annotated values will not be restored.");
                return (T) from;
            }
        }

        if (result == null) {
            result = super.createToInstance(from, toClass);

            // Dynamic proxy workaround : for inheritance purpose beanlib returns an instance of the proxy class since
            // it inherits from the source class...
            if ((classMapper != null) && (classMapper.getSourceClass(result.getClass()) != null)) {
                result = newInstanceAsPrivileged(toClass);
            }
        }

        // Add the bean to stack
        // BeanlibCache.getToBeanStack().push(result);

        return result;
    }

    /**
     * Factory for {@link MergeClassBeanReplicator}
     *
     * @author bruno.marchesson
     */
    public static class Factory implements BeanReplicatorSpi.Factory {
        private Factory() {}

        @Override
        public MergeClassBeanReplicator newBeanReplicatable(BeanTransformerSpi beanTransformer) {
            return new MergeClassBeanReplicator(beanTransformer);
        }
    }
}
