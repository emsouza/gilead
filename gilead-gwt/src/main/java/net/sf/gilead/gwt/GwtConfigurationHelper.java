package net.sf.gilead.gwt;

import net.sf.gilead.core.PersistenceUtil;
import net.sf.gilead.core.PersistentBeanManager;
import net.sf.gilead.core.beanlib.mapper.ProxyClassMapper;
import net.sf.gilead.core.serialization.GwtProxySerialization;
import net.sf.gilead.core.store.stateless.StatelessProxyStore;

/**
 * @author bruno.marchesson
 */
public class GwtConfigurationHelper {

    /**
     * Init bean manager for stateless mode for GWT
     */
    public static PersistentBeanManager initGwtStatelessBeanManager(PersistenceUtil persistenceUtil) {
        if (PersistentBeanManager.getInstance().getPersistenceUtil() == null) {
            PersistentBeanManager beanManager = PersistentBeanManager.getInstance();
            beanManager.setPersistenceUtil(persistenceUtil);

            StatelessProxyStore proxyStore = new StatelessProxyStore();
            proxyStore.setProxySerializer(new GwtProxySerialization());
            beanManager.setProxyStore(proxyStore);

            beanManager.setClassMapper(null);
        }

        return PersistentBeanManager.getInstance();
    }

    public static PersistentBeanManager initGwtProxyBeanManager(PersistenceUtil persistenceUtil) {
        if (PersistentBeanManager.getInstance().getPersistenceUtil() == null) {
            PersistentBeanManager beanManager = PersistentBeanManager.getInstance();
            beanManager.setPersistenceUtil(persistenceUtil);

            StatelessProxyStore proxyStore = new StatelessProxyStore();
            proxyStore.setProxySerializer(new GwtProxySerialization());
            beanManager.setProxyStore(proxyStore);

            ProxyClassMapper classMapper = new ProxyClassMapper();
            classMapper.setPersistenceUtil(persistenceUtil);
            beanManager.setClassMapper(classMapper);
        }

        return PersistentBeanManager.getInstance();
    }
}
