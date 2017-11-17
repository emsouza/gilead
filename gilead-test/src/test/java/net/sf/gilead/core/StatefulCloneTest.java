package net.sf.gilead.core;

import net.sf.gilead.test.domain.interfaces.IMessage;
import net.sf.gilead.test.domain.interfaces.IUser;

/**
 * Clone test for stateful mode
 * 
 * @author bruno.marchesson
 */
public class StatefulCloneTest extends CloneTest {

    /**
     * Test setup
     */
    @Override
    protected void setUp() throws Exception {
        // Init bean manager
        beanManager = TestHelper.initStatefulBeanManager();

        // Init domain and clone classes
        domainMessageClass = net.sf.gilead.test.domain.stateful.Message.class;
        domainUserClass = net.sf.gilead.test.domain.stateful.User.class;
        domainEmployeeClass = net.sf.gilead.test.domain.stateful.Employee.class;

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
        ((net.sf.gilead.test.domain.stateful.Message) message).setAuthor((net.sf.gilead.test.domain.stateful.User) user);
    }

    /**
     * Change the author of the message
     */
    @Override
    protected void changeAuthorForDomain(IMessage message, IUser user) {
        ((net.sf.gilead.test.domain.stateful.Message) message).setAuthor((net.sf.gilead.test.domain.stateful.User) user);
    }
}
