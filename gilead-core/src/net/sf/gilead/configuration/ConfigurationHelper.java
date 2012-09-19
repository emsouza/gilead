/**
 * 
 */
package net.sf.gilead.configuration;

import net.sf.gilead.core.IPersistenceUtil;
import net.sf.gilead.core.PersistentBeanManager;
import net.sf.gilead.core.serialization.JBossProxySerialization;
import net.sf.gilead.core.store.NoProxyStore;
import net.sf.gilead.core.store.stateful.HttpSessionProxyStore;
import net.sf.gilead.core.store.stateless.StatelessProxyStore;

/**
 * Configuration helper to simplify Gilead configuration.
 * 
 * @author bruno.marchesson
 */
public class ConfigurationHelper {
	// -------------------------------------------------------------------------
	//
	// Static helpers
	//
	// -------------------------------------------------------------------------
	/**
	 * Init bean manager for stateless mode
	 */
	public static PersistentBeanManager initStatelessBeanManager(IPersistenceUtil persistenceUtil) {
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
	public static PersistentBeanManager initLegacyStatelessBeanManager(IPersistenceUtil persistenceUtil) {
		if (PersistentBeanManager.getInstance().getPersistenceUtil() == null) {
			PersistentBeanManager beanManager = PersistentBeanManager.getInstance(); // new PersistentBeanManager();
			beanManager.setPersistenceUtil(persistenceUtil);

			StatelessProxyStore proxyStore = new StatelessProxyStore();
			proxyStore.setProxySerializer(new JBossProxySerialization());
			beanManager.setProxyStore(proxyStore);

			beanManager.setClassMapper(null);
		}

		return PersistentBeanManager.getInstance();
	}

	/**
	 * Init bean manager for stateful mode
	 */
	public static PersistentBeanManager initStatefulBeanManager(IPersistenceUtil persistenceUtil) {
		if (PersistentBeanManager.getInstance().getPersistenceUtil() == null) {
			HttpSessionProxyStore proxyStore = new HttpSessionProxyStore();
			proxyStore.setPersistenceUtil(persistenceUtil);

			PersistentBeanManager beanManager = PersistentBeanManager.getInstance(); // new PersistentBeanManager();
			beanManager.setPersistenceUtil(persistenceUtil);
			beanManager.setProxyStore(proxyStore);
			beanManager.setClassMapper(null);
		}

		return PersistentBeanManager.getInstance();
	}

	/**
	 * Init bean manager for clone only mode
	 */
	public static PersistentBeanManager initBeanManagerForCloneOnly(IPersistenceUtil persistenceUtil) {
		if (PersistentBeanManager.getInstance().getPersistenceUtil() == null) {
			PersistentBeanManager beanManager = PersistentBeanManager.getInstance();
			beanManager.setPersistenceUtil(persistenceUtil);
			beanManager.setProxyStore(new NoProxyStore());
			beanManager.setClassMapper(null);
		}

		return PersistentBeanManager.getInstance();
	}
}
