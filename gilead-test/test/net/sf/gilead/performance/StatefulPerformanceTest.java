/**
 * 
 */
package net.sf.gilead.performance;

import net.sf.gilead.core.TestHelper;

/**
 * Performance test for stateful mode
 * 
 * @author bruno.marchesson
 */
public class StatefulPerformanceTest extends PerformanceTest {
    /**
     * Test setup
     */
    @Override
    protected void setUp() throws Exception {
        // Init bean manager
        //
        _beanManager = TestHelper.initStatefulBeanManager();

        // Call base setup
        //
        super.setUp();
    }
}
