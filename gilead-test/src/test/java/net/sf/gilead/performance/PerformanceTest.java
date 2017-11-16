/**
 *
 */
package net.sf.gilead.performance;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import junit.framework.TestCase;
import net.sf.gilead.core.PersistentBeanManager;
import net.sf.gilead.core.TestHelper;
import net.sf.gilead.test.DAOFactory;
import net.sf.gilead.test.dao.IUserDAO;
import net.sf.gilead.test.domain.interfaces.IUser;

/**
 * Abstract class for performance test
 *
 * @author bruno.marchesson
 */
public abstract class PerformanceTest extends TestCase {

    private static final Logger LOGGER = LoggerFactory.getLogger(PerformanceTest.class);

    // ----
    // Attributes
    // ----
    /**
     * Persistent lazy manager
     */
    protected PersistentBeanManager _beanManager;

    /**
     * Indicates if merge is needed
     */
    protected boolean _merge = true;

    // -------------------------------------------------------------------------
    //
    // Test initialisation
    //
    // -------------------------------------------------------------------------
    /**
     * Test init
     */
    @Override
    protected void setUp() throws Exception {
        super.setUp();

        // Init db if needed
        //
        if (TestHelper.isInitialized() == false) {
            TestHelper.initializeDB();
        }
        if (TestHelper.isLotOfDataCreated() == false) {
            TestHelper.initializeLotOfData(100);
        }
    }

    // -------------------------------------------------------------------------
    //
    // Test methods
    //
    // -------------------------------------------------------------------------
    /**
     * Test clone of a loaded user and associated messages
     */
    public void testPerformanceOnCloneAndMergeUserAndMessages() {
        // Get UserDAO
        //
        IUserDAO userDAO = DAOFactory.getUserDAO();
        assertNotNull(userDAO);

        // Load user
        //
        IUser user = userDAO.searchUserAndMessagesByLogin(TestHelper.JUNIT_LOGIN);
        assertNotNull(user);
        assertNotNull(user.getMessageList());
        assertFalse(user.getMessageList().isEmpty());

        // Clone user
        //
        long start = System.currentTimeMillis();
        IUser cloneUser = (IUser) _beanManager.clone(user);
        long end = System.currentTimeMillis();
        assertNotNull(cloneUser);

        LOGGER.info(getClass().getSimpleName() + " / Clone user took " + (end - start) + " ms.");

        if (_merge) {
            // Merge user
            //
            start = System.currentTimeMillis();
            IUser mergeUser = (IUser) _beanManager.merge(cloneUser);
            LOGGER.info(getClass().getSimpleName() + " / Merge user took " + (System.currentTimeMillis() - start) + " ms.");

            // Test merged user
            //
            assertNotNull(mergeUser);
        }
    }

    /**
     * Test clone of a list of user and associated messages
     */
    public void testPerformanceOnCloneAndMergeAllUserAndMessages() {
        // Get UserDAO
        //
        IUserDAO userDAO = DAOFactory.getUserDAO();
        assertNotNull(userDAO);

        // Load users
        //
        List<IUser> userList = userDAO.loadAllUserAndMessages();

        // Clone user
        //
        long start = System.currentTimeMillis();
        List<IUser> cloneUserList = (List<IUser>) _beanManager.clone(userList);
        long end = System.currentTimeMillis();
        assertNotNull(cloneUserList);

        LOGGER.info(getClass().getSimpleName() + " / Clone user list took " + (end - start) + " ms.");

        if (_merge) {
            // Merge user
            //
            start = System.currentTimeMillis();
            List<IUser> mergeUserList = (List<IUser>) _beanManager.merge(cloneUserList);
            LOGGER.info(getClass().getSimpleName() + " / Merge user list took " + (System.currentTimeMillis() - start) + " ms.");

            // Test merged user
            //
            assertNotNull(mergeUserList);
        }
    }

    /**
     * Test clone of a list of user and associated messages
     */
    public void testLongRunningPerformanceOnClone() {
        // Get UserDAO
        //
        IUserDAO userDAO = DAOFactory.getUserDAO();
        assertNotNull(userDAO);

        // Load users
        //
        List<IUser> userList = userDAO.loadAllUserAndMessages();

        // Clone user
        //
        for (int index = 0; index < 10; index++) {
            long start = System.currentTimeMillis();
            List<IUser> cloneUserList = (List<IUser>) _beanManager.clone(userList);
            long end = System.currentTimeMillis();
            assertNotNull(cloneUserList);

            LOGGER.info(getClass().getSimpleName() + " / Clone user list took " + (end - start) + " ms.");
        }
    }
}
