package net.sf.gilead.core.hibernate;

import junit.framework.TestCase;
import net.sf.gilead.core.TestHelper;
import net.sf.gilead.exception.NotPersistentObjectException;
import net.sf.gilead.exception.TransientObjectException;
import net.sf.gilead.test.DAOFactory;
import net.sf.gilead.test.HibernateContext;
import net.sf.gilead.test.HibernateContext.Context;
import net.sf.gilead.test.dao.IMessageDAO;
import net.sf.gilead.test.dao.IUserDAO;
import net.sf.gilead.test.domain.interfaces.IMessage;
import net.sf.gilead.test.domain.interfaces.IUser;
import net.sf.gilead.test.domain.misc.Configuration;
/* import net.sf.gilead.test.domain.misc.PageElement;
 import net.sf.gilead.test.domain.misc.TextElement; */
import net.sf.gilead.test.domain.stateless.Message;
import net.sf.gilead.test.domain.stateless.User;

import org.hibernate.SessionFactory;

/**
 * Hibernate Helper test case
 * 
 * @author bruno.marchesson
 */
public class HibernateUtilTest extends TestCase {
    // -------------------------------------------------------------------------
    //
    // Test init
    //
    // -------------------------------------------------------------------------
    /**
     * Test initialisation
     */
    @Override
    protected void setUp() throws Exception {
        super.setUp();

        // Retrieve Hibernate session factory
        HibernateContext.setContext(Context.stateless);
        SessionFactory factory = HibernateContext.getSessionFactory();

        HibernateUtil.getInstance().setSessionFactory(factory);

        // Init db if needed
        //
        if (TestHelper.isInitialized() == false) {
            TestHelper.initializeDB();
        }
    }

    // -------------------------------------------------------------------------
    //
    // Test methods
    //
    // -------------------------------------------------------------------------
    /**
     * Test ID retrieving
     */
    public final void testGetIdObject() {
        // Get test user
        //
        IUserDAO userDAO = DAOFactory.getUserDAO();
        IUser user = userDAO.loadUser(Integer.valueOf(1));
        assertNotNull(user);

        // Test ID retrieving
        //
        assertEquals(user.getId(), HibernateUtil.getInstance().getId(user));

        // Error test on transient object
        //
        user = new User();
        try {
            HibernateUtil.getInstance().getId(user);
            fail("Expected an exception on transient object");
        } catch (TransientObjectException ex) { /* expected behavior */}

        // Error test on non Hibernate object
        //
        Configuration configuration = new Configuration();
        try {
            HibernateUtil.getInstance().getId(configuration);
            fail("Expected an exception on not Hibernate object");
        } catch (NotPersistentObjectException ex) { /* expected behavior */}

    }

    /**
     * Test Hibernate POJO checking
     */
    public final void testIsPersistentPojo() {
        // Get test user
        //
        IUserDAO userDAO = DAOFactory.getUserDAO();
        IUser user = userDAO.loadUser(Integer.valueOf(1));
        assertNotNull(user);

        // Test ID retrieving
        //
        assertTrue(HibernateUtil.getInstance().isPersistentPojo(user));

        // Test on transient object
        //
        IMessage message = new Message();
        assertFalse(HibernateUtil.getInstance().isPersistentPojo(message));

        // Error test on non Hibernate object
        //
        Configuration configuration = new Configuration();
        assertFalse(HibernateUtil.getInstance().isPersistentPojo(configuration));
    }

    /**
     * Test Hibernate class checking
     */
    public final void testIsHibernateClass() {
        assertTrue(HibernateUtil.getInstance().isPersistentClass(User.class));
        assertTrue(HibernateUtil.getInstance().isPersistentClass(Message.class));
        assertFalse(HibernateUtil.getInstance().isPersistentClass(Configuration.class));
    }

    /**
     * Test association loading
     */
    public void testSimpleAssociationLoad() {
        // Load test message
        //
        IMessageDAO messageDAO = DAOFactory.getMessageDAO();
        IMessage message = messageDAO.loadLastMessage();
        assertNotNull(message);
        assertFalse(HibernateUtil.getInstance().isInitialized(message.getAuthor()));

        // Test 'author' loading
        //
        IMessage loadedMessage = (IMessage) HibernateUtil.getInstance().loadAssociation(message.getClass(), message.getId(), "author");
        assertNotNull(loadedMessage);
        assertTrue(HibernateUtil.getInstance().isInitialized(loadedMessage.getAuthor()));

    }
}
