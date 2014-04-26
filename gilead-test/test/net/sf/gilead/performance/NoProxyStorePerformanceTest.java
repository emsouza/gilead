/**
 * 
 */
package net.sf.gilead.performance;

import net.sf.gilead.core.TestHelper;
import net.sf.gilead.core.store.NoProxyStore;

/**
 * Performance test for stateless mode
 * 
 * @author bruno.marchesson
 */
public class NoProxyStorePerformanceTest extends PerformanceTest {
    /**
     * Test setup
     */
    @Override
    protected void setUp() throws Exception {
        // Init bean manager
        //
        _beanManager = TestHelper.initStatelessBeanManager();
        _beanManager.setProxyStore(new NoProxyStore());

        _merge = false;

        // Call base setup
        //
        super.setUp();
    }
}
