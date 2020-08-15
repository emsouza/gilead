package net.sf.gilead.configuration;

import net.sf.gilead.core.PersistenceUtil;
import net.sf.gilead.core.PersistentBeanManager;
import net.sf.gilead.core.serialization.DefaultProxySerialization;
import net.sf.gilead.core.store.NoProxyStore;
import net.sf.gilead.core.store.stateful.HttpSessionProxyStore;
import net.sf.gilead.core.store.stateless.StatelessProxyStore;

/**
 * Configuration helper to simplify Gilead configuration.
 * 
 * @author bruno.marchesson
 */
public class ConfigurationHelper {

    /**
     * Init bean manager for stateless mode
     */
    public static PersistentBeanManager initStatelessBeanManager(PersistenceUtil persistenceUtil) {
        if (PersistentBeanManager.getInstance().getPersistenceUtil() == null) {
            PersistentBeanManager beanManager = PersistentBeanManager.getInstance();
            beanManager.setPersistenceUtil(persistenceUtil);
            beanManager.setProxyStore(new StatelessProxyStore());
            beanManager.setClassMapper(null);
        }

        return PersistentBeanManager.getInstance();
    }

    /**
     * Init bean manager for stateless mode for legacy Gilead 1.2 (encoded proxy info)
     */
    public static PersistentBeanManager initLegacyStatelessBeanManager(PersistenceUtil persistenceUtil) {
        if (PersistentBeanManager.getInstance().getPersistenceUtil() == null) {
            PersistentBeanManager beanManager = PersistentBeanManager.getInstance();
            beanManager.setPersistenceUtil(persistenceUtil);

            StatelessProxyStore proxyStore = new StatelessProxyStore();
            proxyStore.setProxySerializer(new DefaultProxySerialization());
            beanManager.setProxyStore(proxyStore);

            beanManager.setClassMapper(null);
        }

        return PersistentBeanManager.getInstance();
    }

    /**
     * Init bean manager for stateful mode
     */
    public static PersistentBeanManager initStatefulBeanManager(PersistenceUtil persistenceUtil) {
        if (PersistentBeanManager.getInstance().getPersistenceUtil() == null) {
            HttpSessionProxyStore proxyStore = new HttpSessionProxyStore();
            proxyStore.setPersistenceUtil(persistenceUtil);

            PersistentBeanManager beanManager = PersistentBeanManager.getInstance();
            beanManager.setPersistenceUtil(persistenceUtil);
            beanManager.setProxyStore(proxyStore);
            beanManager.setClassMapper(null);
        }

        return PersistentBeanManager.getInstance();
    }

    /**
     * Init bean manager for clone only mode
     */
    public static PersistentBeanManager initBeanManagerForCloneOnly(PersistenceUtil persistenceUtil) {
        if (PersistentBeanManager.getInstance().getPersistenceUtil() == null) {
            PersistentBeanManager beanManager = PersistentBeanManager.getInstance();
            beanManager.setPersistenceUtil(persistenceUtil);
            beanManager.setProxyStore(new NoProxyStore());
            beanManager.setClassMapper(null);
        }

        return PersistentBeanManager.getInstance();
    }
}
