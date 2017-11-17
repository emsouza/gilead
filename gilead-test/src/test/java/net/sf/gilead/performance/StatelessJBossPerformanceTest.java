package net.sf.gilead.performance;

import net.sf.gilead.core.TestHelper;
import net.sf.gilead.core.serialization.JBossProxySerialization;
import net.sf.gilead.core.store.stateless.StatelessProxyStore;

/**
 * Performance test for stateless mode
 * 
 * @author bruno.marchesson
 */
public class StatelessJBossPerformanceTest extends PerformanceTest {

    /**
     * Test setup
     */
    @Override
    protected void setUp() throws Exception {
        // Init bean manager
        beanManager = TestHelper.initLegacyStatelessBeanManager();

        ((StatelessProxyStore) beanManager.getProxyStore()).setProxySerializer(new JBossProxySerialization());

        // Call base setup
        super.setUp();
    }
}
