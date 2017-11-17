package net.sf.gilead.performance;

import net.sf.gilead.core.TestHelper;

/**
 * Performance test for stateless mode
 * 
 * @author bruno.marchesson
 */
public class StatelessGwtSerializationPerformanceTest extends PerformanceTest {

    /**
     * Test setup
     */
    @Override
    protected void setUp() throws Exception {
        // Init bean manager
        beanManager = TestHelper.initGwtStatelessBeanManager();

        // Call base setup
        super.setUp();
    }
}
