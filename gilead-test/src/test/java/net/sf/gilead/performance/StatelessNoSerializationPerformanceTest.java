package net.sf.gilead.performance;

import net.sf.gilead.core.TestHelper;
import net.sf.gilead.core.store.stateless.StatelessProxyStore;

/**
 * Performance test for stateless mode
 * 
 * @author bruno.marchesson
 */
public class StatelessNoSerializationPerformanceTest extends PerformanceTest {

    /**
     * Test setup
     */
    @Override
    protected void setUp() throws Exception {
        // Init bean manager
        beanManager = TestHelper.initStatelessBeanManager();
        ((StatelessProxyStore) beanManager.getProxyStore()).setProxySerializer(null);

        // Call base setup
        super.setUp();
    }
}
