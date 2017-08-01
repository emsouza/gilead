/**
 *
 */
package net.sf.gilead.core;

import java.util.Date;

import org.hibernate.FlushMode;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import junit.framework.TestCase;
import net.sf.gilead.test.DAOFactory;
import net.sf.gilead.test.HibernateContext;
import net.sf.gilead.test.dao.IUserDAO;
import net.sf.gilead.test.domain.annotated.Message;
import net.sf.gilead.test.domain.annotated.User;
import net.sf.gilead.test.domain.interfaces.IMessage;
import net.sf.gilead.test.domain.interfaces.IUser;

/**
 * @author eduardo.souza
 */
@RunWith(JUnit4.class)
public class MultipleMergesTest extends TestCase {

    protected PersistentBeanManager beanManager;

    /**
     * Test init
     */
    @Before
    public void before() throws Exception {

        beanManager = TestHelper.initJava5AnnotatedBeanManager();

        // Init db if needed
        if (!TestHelper.isInitialized()) {
            TestHelper.initializeDB();
        }
        if (!TestHelper.isLotOfDataCreated()) {
            TestHelper.initializeLotOfData(20);
        }
    }

    @Test
    public void multipleMerges() {
        // Get UserDAO
        IUserDAO userDAO = DAOFactory.getUserDAO();
        assertNotNull(userDAO);
        IUser user = userDAO.loadUserByLogin(TestHelper.JUNIT_LOGIN);

        IUser cloneUser = (IUser) beanManager.clone(user);

        // Test cloned user
        assertNotNull(cloneUser);

        cloneUser.addMessage(createNewMessage(cloneUser));

        // Create a new user and save it
        Session session = HibernateContext.getSessionFactory().getCurrentSession();
        session.setFlushMode(FlushMode.MANUAL);
        Transaction tx = session.beginTransaction();

        IUser mergedUser = (IUser) beanManager.merge(cloneUser);

        // Test merged message
        assertNotNull(mergedUser);

        mergedUser.addMessage(createNewMessage(mergedUser));
        mergedUser.addMessage(createNewMessage(mergedUser));
        mergedUser.addMessage(createNewMessage(mergedUser));
        mergedUser.addMessage(createNewMessage(mergedUser));

        mergedUser = (IUser) session.merge(mergedUser);

        tx.commit();
    }

    @Test(expected = RuntimeException.class)
    public void errorOnMultipleMerges() {
        // Get UserDAO
        IUserDAO userDAO = DAOFactory.getUserDAO();
        assertNotNull(userDAO);
        IUser user = userDAO.loadUserByLogin(TestHelper.JUNIT_LOGIN);

        IUser cloneUser = (IUser) beanManager.clone(user);

        // Test cloned user
        assertNotNull(cloneUser);

        cloneUser.addMessage(createNewMessage(cloneUser));

        // Create a new user and save it
        Session session = HibernateContext.getSessionFactory().getCurrentSession();
        session.setFlushMode(FlushMode.MANUAL);
        Transaction tx = session.beginTransaction();

        IUser mergedUser = (IUser) beanManager.merge(cloneUser);

        // Test merged message
        assertNotNull(mergedUser);

        mergedUser = (IUser) session.merge(mergedUser);

        mergedUser = (IUser) beanManager.merge(cloneUser);

        mergedUser = (IUser) session.merge(mergedUser);

        tx.commit();
    }

    /**
     * Create a new message
     *
     * @throws IllegalAccessException
     * @throws InstantiationException
     */
    protected IMessage createNewMessage(IUser user) {
        // Create message
        IMessage result = new Message();
        result.setDate(new Date());
        result.setMessage("test message");

        // Change author
        changeAuthorForDomain(result, user);

        return result;
    }

    protected void changeAuthorForDomain(IMessage message, IUser user) {
        ((net.sf.gilead.test.domain.annotated.Message) message).setAuthor((User) user);
    }
}
