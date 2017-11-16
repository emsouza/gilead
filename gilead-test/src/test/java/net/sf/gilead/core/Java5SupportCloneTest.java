/**
 * 
 */
package net.sf.gilead.core;

import net.sf.gilead.test.domain.interfaces.IMessage;
import net.sf.gilead.test.domain.interfaces.IUser;

/**
 * Clone test for Java5 support mode
 * 
 * @author bruno.marchesson
 */
public class Java5SupportCloneTest extends CloneTest {
    /**
     * Test setup
     */
    @Override
    protected void setUp() throws Exception {
        // Init bean manager
        //
        _beanManager = TestHelper.initJava5SupportBeanManager();

        // Init domain and clone classes
        //
        _domainMessageClass = net.sf.gilead.test.domain.java5.Message.class;
        _domainUserClass = net.sf.gilead.test.domain.java5.User.class;
        _domainEmployeeClass = net.sf.gilead.test.domain.java5.Employee.class;

        _cloneMessageClass = net.sf.gilead.test.domain.dto.MessageDTO.class;
        _cloneUserClass = net.sf.gilead.test.domain.dto.UserDTO.class;
        _cloneEmployeeClass = net.sf.gilead.test.domain.dto.EmployeeDTO.class;

        // Do not test component type : nested many to one in embeddable
        // class does not seem to be handled properly
        _testComponentType = false;

        // Call base setup
        //
        super.setUp();
    }

    /**
     * Change the author for clone message
     */
    @Override
    protected void changeAuthorForClone(IMessage message, IUser user) {
        ((net.sf.gilead.test.domain.dto.MessageDTO) message).setAuthor((net.sf.gilead.test.domain.dto.UserDTO) user);
    }

    /**
     * Change the author of the message
     */
    @Override
    protected void changeAuthorForDomain(IMessage message, IUser user) {
        ((net.sf.gilead.test.domain.java5.Message) message).setAuthor((net.sf.gilead.test.domain.java5.User) user);
    }
}
