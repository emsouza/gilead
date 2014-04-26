/**
 *
 */
package net.sf.gilead.core;

import net.sf.gilead.proxy.ProxyManager;
import net.sf.gilead.proxy.xml.AdditionalCode;
import net.sf.gilead.proxy.xml.AdditionalCodeReader;
import net.sf.gilead.test.domain.interfaces.IMessage;
import net.sf.gilead.test.domain.interfaces.IUser;

/**
 * Clone test for dynamic proxy mode (Java 5)
 * 
 * @author bruno.marchesson
 */
public class Java5DynamicProxyCloneTest extends CloneTest {
    /**
     * Test setup
     */
    @Override
    protected void setUp() throws Exception {
        // Init bean manager and domain classes
        //
        _beanManager = TestHelper.initProxyBeanManager();

        _domainMessageClass = net.sf.gilead.test.domain.proxy.Message.class;
        _domainUserClass = net.sf.gilead.test.domain.proxy.User.class;
        _domainEmployeeClass = net.sf.gilead.test.domain.proxy.Employee.class;

        // Read additional code
        //
        AdditionalCode additionalCode = AdditionalCodeReader.readFromFile(ProxyManager.JAVA_5_LAZY_POJO);

        // Proxy generation test
        //
        Class<?> cloneMessageClass = ProxyManager.getInstance().generateProxyClass(_domainMessageClass, additionalCode);
        if ((cloneMessageClass != null) && (cloneMessageClass.getName().endsWith(additionalCode.getSuffix()) == false)) {
            // Bad proxy generation (already generated with another additional code : clean it
            //
            ProxyManager.getInstance().clear();
        }

        _cloneMessageClass = ProxyManager.getInstance().generateProxyClass(_domainMessageClass, additionalCode);
        _cloneUserClass = ProxyManager.getInstance().generateProxyClass(_domainUserClass, additionalCode);
        _cloneEmployeeClass = ProxyManager.getInstance().generateProxyClass(_domainEmployeeClass, additionalCode);

        assertTrue(_cloneMessageClass.getName().endsWith(additionalCode.getSuffix()));
        assertTrue(_cloneUserClass.getName().endsWith(additionalCode.getSuffix()));
        assertTrue(_cloneEmployeeClass.getName().endsWith(additionalCode.getSuffix()));

        // other Domain class proxy generation
        ProxyManager.getInstance().generateProxyClass(net.sf.gilead.test.domain.proxy.Address.class, additionalCode);

        // Call base setup
        //
        super.setUp();
    }

    /**
     * Change the author for clone message
     */
    @Override
    protected void changeAuthorForClone(IMessage message, IUser user) {
        ((net.sf.gilead.test.domain.proxy.Message) message).setAuthor((net.sf.gilead.test.domain.proxy.User) user);
    }

    /**
     * Change the author of the message
     */
    @Override
    protected void changeAuthorForDomain(IMessage message, IUser user) {
        ((net.sf.gilead.test.domain.proxy.Message) message).setAuthor((net.sf.gilead.test.domain.proxy.User) user);
    }
}
