package net.sf.gilead.core;

import net.sf.gilead.test.domain.interfaces.IMessage;
import net.sf.gilead.test.domain.interfaces.IUser;

/**
 * Clone test for stateless mode
 * 
 * @author bruno.marchesson
 */
public class StatelessCloneTest extends CloneTest {
    /**
     * Test setup
     */
    @Override
    protected void setUp() throws Exception {
        // Init bean manager
        beanManager = TestHelper.initStatelessBeanManager();

        // Init domain and clone classes
        domainMessageClass = net.sf.gilead.test.domain.stateless.Message.class;
        domainUserClass = net.sf.gilead.test.domain.stateless.User.class;
        domainEmployeeClass = net.sf.gilead.test.domain.stateless.Employee.class;

        cloneMessageClass = domainMessageClass;
        cloneUserClass = domainUserClass;
        cloneEmployeeClass = domainEmployeeClass;

        // Call base setup
        super.setUp();
    }

    /**
     * Change the author for clone message
     */
    @Override
    protected void changeAuthorForClone(IMessage message, IUser user) {
        ((net.sf.gilead.test.domain.stateless.Message) message).setAuthor(user);
    }

    /**
     * Change the author of the message
     */
    @Override
    protected void changeAuthorForDomain(IMessage message, IUser user) {
        ((net.sf.gilead.test.domain.stateless.Message) message).setAuthor(user);
    }
}
