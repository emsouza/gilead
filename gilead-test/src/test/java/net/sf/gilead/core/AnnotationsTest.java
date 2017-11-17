package net.sf.gilead.core;

import java.util.Date;

import junit.framework.TestCase;
import net.sf.gilead.annotations.TestAccessManager;
import net.sf.gilead.annotations.TestAccessManager.Role;
import net.sf.gilead.core.annotations.AnnotationsManager;
import net.sf.gilead.test.DAOFactory;
import net.sf.gilead.test.dao.IMessageDAO;
import net.sf.gilead.test.dao.IUserDAO;
import net.sf.gilead.test.domain.annotated.Message;
import net.sf.gilead.test.domain.annotated.User;

/**
 * Test case for ReadOnly and ServerOnly annotations
 *
 * @author bruno.marchesson
 */
public class AnnotationsTest extends TestCase {

    /**
     * Hibernate lazy manager
     */
    protected PersistentBeanManager beanManager;

    /**
     * Test init
     */
    @Override
    protected void setUp() throws Exception {
        beanManager = TestHelper.initJava5AnnotatedBeanManager();

        // Init db if needed
        if (TestHelper.isInitialized() == false) {
            TestHelper.initializeDB();
        }
    }

    /**
     * Test clone and merge of ReadOnly attributes
     */
    public void testReadOnlyCloneAndMergeUser() {
        // Get UserDAO
        IUserDAO userDAO = DAOFactory.getUserDAO();
        assertNotNull(userDAO);

        // Load user
        User user = (User) userDAO.loadUser(userDAO.searchUserAndMessagesByLogin("junit").getId());
        assertNotNull(user);
        assertNotNull(user.getPassword());

        // Clone user
        User cloneUser = (User) beanManager.clone(user);

        // Test cloned user
        assertNotNull(cloneUser);
        assertNull(cloneUser.getPassword());

        // Merge user
        User mergeUser = (User) beanManager.merge(cloneUser);

        // Test merged user
        assertNotNull(mergeUser);
        assertNotNull(mergeUser.getPassword());
        assertEquals(user.getPassword(), mergeUser.getPassword());
    }

    /**
     * Test clone and merge of ReadOnly attributes
     */
    public void testReadOnlyAndServerOnlyCloneAndMergeMessage() {
        // Init access manager
        TestAccessManager accessManager = new TestAccessManager();
        accessManager.setRole(Role.user);
        AnnotationsManager.setAccessManager(accessManager);

        // Get MessageDAO
        IMessageDAO messageDAO = DAOFactory.getMessageDAO();
        assertNotNull(messageDAO);

        // Load message
        Message message = (Message) messageDAO.loadDetailedMessage(TestHelper.getExistingMessageId());
        message.setComment("myComment");
        messageDAO.saveMessage(message);

        assertNotNull(message);
        assertNotNull(message.getVersion()); // serverOnly
        assertNotNull(message.getComment()); // readOnly

        // Clone message
        Message cloneMessage = (Message) beanManager.clone(message);

        // Test cloned Message
        assertNotNull(cloneMessage);
        assertNull(cloneMessage.getVersion()); // serverOnly
        assertNotNull(cloneMessage.getComment()); // readOnly

        cloneMessage.setComment("modified");

        // Merge Message
        Message mergeMessage = (Message) beanManager.merge(cloneMessage);

        // Test merged Message
        assertNotNull(mergeMessage);
        assertNotNull(mergeMessage.getVersion()); // serverOnly
        assertNotNull(mergeMessage.getComment());
        assertEquals(message.getComment(), mergeMessage.getComment());
    }

    /**
     * Test clone and merge of ReadOnly attributes
     */
    public void testAnnotationsOnTransientMessage() {
        // Init access manager
        TestAccessManager accessManager = new TestAccessManager();
        accessManager.setRole(Role.user);
        AnnotationsManager.setAccessManager(accessManager);

        // Create message
        Message message = new Message();
        message.setMessage("test");
        message.setDate(new Date());
        message.setVersion(1);
        message.setComment("myComment");

        // Clone message
        Message cloneMessage = (Message) beanManager.clone(message);

        // Test cloned message
        assertNotNull(cloneMessage);
        assertNull(cloneMessage.getVersion()); // serverOnly
        assertNotNull(cloneMessage.getComment()); // readOnly

        cloneMessage.setComment("modified");

        // Merge Message
        Message mergeMessage = (Message) beanManager.merge(cloneMessage);

        // Test merged Message
        assertNotNull(mergeMessage);
        assertNull(mergeMessage.getVersion()); // serverOnly
        assertNotNull(mergeMessage.getComment()); // readOnly
        assertEquals(cloneMessage.getComment(), mergeMessage.getComment());
    }
}
