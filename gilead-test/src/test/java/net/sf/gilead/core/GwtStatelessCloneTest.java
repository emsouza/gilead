package net.sf.gilead.core;

import net.sf.gilead.test.domain.interfaces.IMessage;
import net.sf.gilead.test.domain.interfaces.IUser;

/**
 * Clone test for stateless mode
 * 
 * @author bruno.marchesson
 */
public class GwtStatelessCloneTest extends CloneTest {

    /**
     * Test setup
     */
    @Override
    protected void setUp() throws Exception {
        // Init bean manager
        beanManager = TestHelper.initGwtStatelessBeanManager();

        // Init domain and clone classes
        domainMessageClass = net.sf.gilead.test.domain.gwt.Message.class;
        domainUserClass = net.sf.gilead.test.domain.gwt.User.class;
        domainEmployeeClass = net.sf.gilead.test.domain.gwt.Employee.class;

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
        ((net.sf.gilead.test.domain.gwt.Message) message).setAuthor(user);
    }

    /**
     * Change the author of the message
     */
    @Override
    protected void changeAuthorForDomain(IMessage message, IUser user) {
        ((net.sf.gilead.test.domain.gwt.Message) message).setAuthor(user);
    }
}
