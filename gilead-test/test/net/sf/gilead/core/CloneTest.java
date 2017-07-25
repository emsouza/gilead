/**
 *
 */
package net.sf.gilead.core;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.hibernate.FlushMode;
import org.hibernate.Hibernate;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;

import junit.framework.TestCase;
import net.sf.gilead.core.wrapper.WrappingArrayClass;
import net.sf.gilead.core.wrapper.WrappingClass;
import net.sf.gilead.core.wrapper.WrappingClass.ErrorCode;
import net.sf.gilead.test.DAOFactory;
import net.sf.gilead.test.HibernateContext;
import net.sf.gilead.test.dao.IMessageDAO;
import net.sf.gilead.test.dao.IUserDAO;
import net.sf.gilead.test.domain.interfaces.IMessage;
import net.sf.gilead.test.domain.interfaces.IUser;
import net.sf.gilead.test.domain.misc.BaseListLoadResult;
import net.sf.gilead.test.domain.misc.Configuration;
import net.sf.gilead.test.domain.misc.PagingList;
import net.sf.gilead.test.domain.misc.Style;

/**
 * Test case clone and merge server operations
 *
 * @author bruno.marchesson
 */
public abstract class CloneTest extends TestCase {
    // ----
    // Attributes
    // ----
    /**
     * Persistent lazy manager
     */
    protected PersistentBeanManager _beanManager;

    /**
     * Clone user class
     */
    protected Class<?> _cloneUserClass = null;

    /**
     * Clone employee class
     */
    protected Class<?> _cloneEmployeeClass = null;

    /**
     * Domain message class
     */
    protected Class<?> _cloneMessageClass = null;

    /**
     * Domain user class
     */
    protected Class<?> _domainUserClass = null;

    /**
     * Domain employee class
     */
    protected Class<?> _domainEmployeeClass = null;

    /**
     * Domain message class
     */
    protected Class<?> _domainMessageClass = null;

    /**
     * Test component type clone and merge
     */
    protected boolean _testComponentType = true;

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
            TestHelper.initializeLotOfData(20);
        }
    }

    // --------------------------------------------------------------------------
    //
    // Test methods
    //
    // ---------------------------------------------------------------------------

    /**
     * Test clone of a loaded (proxy) user
     */
    public void testCloneAndMergeLoadedProxy() {
        // Get UserDAO
        //
        SessionFactory sessionFactory = HibernateContext.getSessionFactory();
        assertNotNull(sessionFactory);

        // Load user
        //
        IUser user = (IUser) sessionFactory.openSession().load(_domainUserClass, TestHelper.getExistingUserId());
        user.getFirstName();
        assertNotNull(user);

        // Clone user
        //
        IUser cloneUser = (IUser) _beanManager.clone(user);

        // Test cloned user
        //
        assertNotNull(cloneUser);
        assertEquals(_cloneUserClass, cloneUser.getClass());

        // Merge user
        //
        IUser mergeUser = (IUser) _beanManager.merge(cloneUser);

        // Test merged user
        //
        assertNotNull(mergeUser);
        assertEquals(_domainUserClass, mergeUser.getClass());
    }

    /**
     * Test clone of a proxy
     */
    public void testCloneProxy() {
        // Get UserDAO
        //
        SessionFactory sessionFactory = HibernateContext.getSessionFactory();
        assertNotNull(sessionFactory);

        // Load user
        //
        IUser user = (IUser) sessionFactory.openSession().load(_domainUserClass, 1);
        assertNotNull(user);

        // Clone user
        //
        IUser cloneUser = (IUser) _beanManager.clone(user);

        // Test cloned user
        //
        assertNull(cloneUser);
    }

    /**
     * Test clone and merge of a loaded message and associated user
     */
    public void testCloneAndMergeMessageAndUser() {
        // Get UserDAO
        //
        IMessageDAO messageDAO = DAOFactory.getMessageDAO();
        assertNotNull(messageDAO);

        // Load message and user
        //
        IMessage message = messageDAO.loadDetailedMessage(TestHelper.getExistingMessageId());
        assertNotNull(message);
        assertNotNull(message.getAuthor());
        assertFalse(Hibernate.isInitialized(message.getAuthor().getMessageList()));

        // Clone message
        //
        IMessage cloneMessage = (IMessage) _beanManager.clone(message);

        // Test cloned message
        //
        assertNotNull(cloneMessage);
        assertEquals(_cloneMessageClass, cloneMessage.getClass());

        // Test cloned user class
        assertNotNull(cloneMessage.getAuthor());
        assertNull(cloneMessage.getAuthor().getMessageList());

        // Merge message
        //
        IMessage mergeMessage = (IMessage) _beanManager.merge(cloneMessage);

        // Test merged message
        //
        assertNotNull(mergeMessage);
        assertEquals(_domainMessageClass, mergeMessage.getClass());

        // Test cloned user class
        assertNotNull(mergeMessage.getAuthor());
        assertNotNull(mergeMessage.getAuthor().getMessageList());
        assertFalse(Hibernate.isInitialized(mergeMessage.getAuthor().getMessageList()));
    }

    /**
     * Test clone and merge of an unsaved user
     *
     * @throws IllegalAccessException
     * @throws InstantiationException
     */
    public void testCloneAndMergeUnsavedUser() throws InstantiationException, IllegalAccessException {
        // Get UserDAO
        //
        IUserDAO userDAO = DAOFactory.getUserDAO();
        assertNotNull(userDAO);
        IUser junitUser = userDAO.loadUserByLogin(TestHelper.JUNIT_LOGIN);

        // Create a new user and save it
        //
        Session session = HibernateContext.getSessionFactory().getCurrentSession();
        session.setFlushMode(FlushMode.MANUAL);
        Transaction tx = session.beginTransaction();

        IUser user = createNewUser();
        user.setLogin("newTest");
        user.setFirstName("Unsaved");
        user.setLastName("value");
        user.setAddress(junitUser.getAddress());

        IMessage message = createNewMessage(user);
        user.addMessage(message);

        // Clone user
        //
        session.saveOrUpdate(user);
        IUser cloneUser = (IUser) _beanManager.clone(user);

        tx.commit();
        // Test cloned user
        //
        assertNotNull(cloneUser);
        // assertNotNull(cloneUser.getMessageList());

        // Merge user
        //
        IUser mergeUser = (IUser) _beanManager.merge(cloneUser);

        // Test merged message
        //
        assertNotNull(mergeUser);
        // assertNotNull(mergeUser.getMessageList());
    }

    /**
     * Test clone of a loaded user list
     */
    public void testCloneUserList() {
        // Get UserDAO
        //
        IUserDAO userDAO = DAOFactory.getUserDAO();
        assertNotNull(userDAO);

        // Load user
        //
        List<IUser> userList = userDAO.loadAll();
        assertNotNull(userList);
        assertFalse(userList.isEmpty());

        // Clone user
        //
        List cloneUserList = (List) _beanManager.clone(userList);

        // Test cloned user
        //
        assertNotNull(cloneUserList);
        assertEquals(cloneUserList.size(), userList.size());

        for (Object user : cloneUserList) {
            assertTrue(_cloneUserClass.equals(user.getClass()) || _cloneEmployeeClass.equals(user.getClass()));
        }
    }

    /**
     * Test clone of a loaded user sub list (a collection class with no empty constructor)
     */
    public void testCloneUserSubList() {
        // Get UserDAO
        //
        IUserDAO userDAO = DAOFactory.getUserDAO();
        assertNotNull(userDAO);

        // Load user
        //
        List<IUser> userList = userDAO.loadAll();
        assertNotNull(userList);
        assertFalse(userList.isEmpty());

        // Clone user
        //
        List<IUser> cloneUserList = (List<IUser>) _beanManager.clone(userList.subList(1, 2));

        // Test cloned user
        //
        assertNotNull(cloneUserList);
    }

    /**
     * Test map cloning
     */
    public void testCloneAndMergeMap() {
        // Get User DAO
        //
        IUserDAO userDAO = DAOFactory.getUserDAO();
        assertNotNull(userDAO);

        // Create a map with all users and message
        //
        Map<IUser, Collection<IMessage>> userMap = new HashMap<IUser, Collection<IMessage>>();
        List<IUser> userList = userDAO.loadAll();
        for (IUser user : userList) {
            IUser completeUser = userDAO.searchUserAndMessagesByLogin(user.getLogin());
            userMap.put(user, completeUser.getMessageList());
        }

        // Clone map
        //
        Map<IUser, Collection<IMessage>> cloneMap = (Map<IUser, Collection<IMessage>>) _beanManager.clone(userMap);
        assertNotNull(cloneMap);
        assertEquals(cloneMap.size(), userMap.size());

        // Clone verification
        //
        for (Entry<IUser, Collection<IMessage>> entry : cloneMap.entrySet()) {
            // User checking
            //
            IUser cloneUser = entry.getKey();
            assertNotNull(cloneUser);
            assertTrue(_cloneUserClass.equals(cloneUser.getClass()) || _cloneEmployeeClass.equals(cloneUser.getClass()));

            // Message checking
            //
            for (IMessage message : entry.getValue()) {
                assertNotNull(message);
                assertEquals(_cloneMessageClass, message.getClass());
            }
        }

        // Merge map
        //
        Map<IUser, Collection<IMessage>> mergeMap = (Map<IUser, Collection<IMessage>>) _beanManager.merge(cloneMap);
        assertNotNull(mergeMap);
        assertEquals(mergeMap.size(), cloneMap.size());

        // Merge verification
        //
        for (Entry<IUser, Collection<IMessage>> entry : mergeMap.entrySet()) {
            // User checking
            //
            IUser mergeUser = entry.getKey();
            assertNotNull(mergeUser);
            assertTrue(_domainUserClass.equals(mergeUser.getClass()) || _domainEmployeeClass.equals(mergeUser.getClass()));

            // Message checking
            //
            for (IMessage message : entry.getValue()) {
                assertNotNull(message);
                assertEquals(_domainMessageClass, message.getClass());
            }
        }
    }

    /**
     * Test clone and merge operations on wrapper object (ie a non persistent class containing persistent classes)
     */
    public void testCloneAndMergeWrapperObject() {
        // Create wrapping object
        //
        WrappingClass wrapper = new WrappingClass();

        // Get Message and User DAO
        //
        IMessageDAO messageDAO = DAOFactory.getMessageDAO();
        assertNotNull(messageDAO);

        IUserDAO userDAO = DAOFactory.getUserDAO();
        assertNotNull(userDAO);

        // Fill wrapper
        //
        wrapper.setErrorCode(ErrorCode.error);
        wrapper.setUser(userDAO.loadUserByLogin(TestHelper.JUNIT_LOGIN));
        wrapper.setMessageList(messageDAO.loadAllMessage());

        // Loading verification
        //
        assertNotNull(wrapper.getUser().getMessageList());
        assertFalse(Hibernate.isInitialized(wrapper.getUser().getMessageList()));

        // Clone wrapper
        //
        WrappingClass cloneWrapper = (WrappingClass) _beanManager.clone(wrapper);

        // Clone verification
        //
        assertEquals(wrapper.getErrorCode(), cloneWrapper.getErrorCode());
        assertNotNull(cloneWrapper.getUser());
        assertEquals(wrapper.getUser().getId(), cloneWrapper.getUser().getId());
        assertNull(cloneWrapper.getUser().getMessageList());

        // Merge wrapper
        //
        WrappingClass mergedWrapper = (WrappingClass) _beanManager.merge(cloneWrapper);

        // Merge verification
        //
        assertEquals(wrapper.getErrorCode(), mergedWrapper.getErrorCode());
        assertEquals(wrapper.getUser().getId(), mergedWrapper.getUser().getId());
        assertNotNull(mergedWrapper.getUser().getMessageList());
        assertFalse(Hibernate.isInitialized(mergedWrapper.getUser().getMessageList()));
    }

    /**
     * Test clone and merge operations on array wrapper (ie a non persistent class containing persistent classes)
     */
    public void testCloneAndMergeArrayWrapper() {
        // Create wrapping object
        //
        WrappingArrayClass wrapper = new WrappingArrayClass();

        // Get Message and User DAO
        //
        IMessageDAO messageDAO = DAOFactory.getMessageDAO();
        assertNotNull(messageDAO);

        // Fill wrapper
        //
        List<IMessage> messageList = messageDAO.loadAllMessage();
        wrapper.setMessages(messageList.toArray(new IMessage[messageList.size()]));

        // Clone wrapper
        //
        WrappingArrayClass cloneWrapper = (WrappingArrayClass) _beanManager.clone(wrapper);

        // Clone verification
        //
        assertNotNull(cloneWrapper.getMessages());
        for (int index = 0; index < cloneWrapper.getMessages().length; index++) {
            IUser author = cloneWrapper.getMessages()[index].getAuthor();
            assertNull(author);
        }

        // Merge wrapper
        //
        WrappingArrayClass mergedWrapper = (WrappingArrayClass) _beanManager.merge(cloneWrapper);

        // Merge verification
        //
        assertNotNull(mergedWrapper.getMessages());
        for (int index = 0; index < cloneWrapper.getMessages().length; index++) {
            IUser author = mergedWrapper.getMessages()[index].getAuthor();
            assertNotNull(author);
            assertFalse(_beanManager.getPersistenceUtil().isInitialized(author));
        }
    }

    /**
     * Test clone and merge operations on wrapper transient object (ie a non persistent class containing transient
     * instance)
     */
    public void testCloneAndMergeTransientWrapperObject() throws Exception {
        // Create wrapping object
        //
        WrappingClass wrapper = new WrappingClass();

        // Fill wrapper
        //
        wrapper.setErrorCode(ErrorCode.warning);
        IUser user = (IUser) _domainUserClass.newInstance();
        wrapper.setUser(user);

        List<IMessage> messageList = new ArrayList<IMessage>();
        IMessage message = (IMessage) _domainMessageClass.newInstance();
        messageList.add(message);
        wrapper.setMessageList(messageList);

        // Loading verification
        //
        assertNull(wrapper.getUser().getMessageList());
        assertEquals(wrapper.getMessageList().size(), 1);

        // Clone wrapper
        //
        WrappingClass cloneWrapper = (WrappingClass) _beanManager.clone(wrapper);

        // Clone verification
        //
        assertNotNull(cloneWrapper.getUser());
        assertEquals(wrapper.getErrorCode(), cloneWrapper.getErrorCode());
        assertEquals(wrapper.getUser().getId(), cloneWrapper.getUser().getId());
        assertNull(cloneWrapper.getUser().getMessageList());
        assertEquals(cloneWrapper.getMessageList().size(), 1);

        // Merge wrapper
        //
        WrappingClass mergedWrapper = (WrappingClass) _beanManager.merge(cloneWrapper);

        // Merge verification
        //
        assertEquals(wrapper.getErrorCode(), mergedWrapper.getErrorCode());
        assertEquals(wrapper.getUser().getId(), mergedWrapper.getUser().getId());
        assertNull(mergedWrapper.getUser().getMessageList());
        assertEquals(mergedWrapper.getMessageList().size(), 1);
    }

    /**
     * Test clone and merge operations on enum transient object
     */
    public void testCloneAndMergeEnumObject() throws Exception {
        // Create style object
        //
        Style style = new Style();
        Style.SortDir sortDir = Style.SortDir.ASC;

        // Clone style
        //
        Style cloneStyle = (Style) _beanManager.clone(style);
        Style.SortDir cloneSortDir = (Style.SortDir) _beanManager.clone(sortDir);

        // Clone verification
        //
        assertNotNull(cloneStyle);
        assertNotNull(cloneSortDir);

        // Merge style
        //
        Style mergedStyle = (Style) _beanManager.merge(cloneStyle);
        Style.SortDir mergedSortDir = (Style.SortDir) _beanManager.merge(cloneSortDir);

        // Merge verification
        //
        assertNotNull(mergedStyle);
        assertNotNull(mergedSortDir);
    }

    /**
     * Test clone and merge operations on transient object
     */
    public void testCloneAndMergeTransientObject() throws Exception {
        // Clone and merge integer
        //
        Integer clone = (Integer) _beanManager.clone(new Integer(2));
        assertEquals(2, clone.intValue());

        Integer merge = (Integer) _beanManager.merge(clone);
        assertEquals(2, merge.intValue());

        // Clone and merge third party instance
        //
        Configuration configuration = new Configuration();
        configuration.setName("test");
        configuration.setSpringContextFile("dummy.xml");

        Configuration cloneConfiguration = (Configuration) _beanManager.clone(configuration);
        assertNotNull(cloneConfiguration);
        assertEquals(configuration.getName(), cloneConfiguration.getName());

        Configuration mergeConfiguration = (Configuration) _beanManager.merge(cloneConfiguration);
        assertNotNull(mergeConfiguration);
        assertEquals(configuration.getName(), mergeConfiguration.getName());
    }

    /**
     * Test change property on client side
     */
    public void testCloneAndMergeArrays() {
        // Clone String array
        //
        String[] stringList = { "test1", "test2", "test3" };
        String[] cloneStringList = (String[]) _beanManager.clone(stringList);
        assertNotNull(cloneStringList);
        assertEquals(stringList.length, cloneStringList.length);

        // Clone users array
        //
        IUserDAO userDAO = DAOFactory.getUserDAO();
        assertNotNull(userDAO);

        List<IUser> userList = userDAO.loadAll();
        assertNotNull(userList);
        assertFalse(userList.isEmpty());
        IUser[] users = userList.toArray(new IUser[userList.size()]);

        IUser[] cloneUsers = (IUser[]) _beanManager.clone(users);
        assertNotNull(cloneUsers);
        assertEquals(users.length, cloneUsers.length);
    }

    /**
     * Test changing association between clone and merge
     */
    public void testChangeAssociationAfterClone() {
        // Get Message and User DAO
        //
        IMessageDAO messageDAO = DAOFactory.getMessageDAO();
        assertNotNull(messageDAO);

        IUserDAO userDAO = DAOFactory.getUserDAO();
        assertNotNull(userDAO);

        // Load last message
        //
        IMessage message = messageDAO.loadDetailedMessage(TestHelper.getExistingMessageId());
        assertNotNull(message);
        assertEquals(_domainMessageClass, message.getClass());

        assertNotNull(message.getAuthor());
        assertEquals(_domainUserClass, message.getAuthor().getClass());

        // Load user
        //
        IUser user = null;
        if (message.getAuthor().getLogin().equals(TestHelper.JUNIT_LOGIN)) {
            user = userDAO.searchUserAndMessagesByLogin(TestHelper.GUEST_LOGIN);
        } else {
            user = userDAO.searchUserAndMessagesByLogin(TestHelper.JUNIT_LOGIN);
        }
        assertNotNull(user);
        assertEquals(_domainUserClass, user.getClass());
        assertFalse(user.equals(message.getAuthor()));

        // Clone message
        //
        IMessage cloneMessage = (IMessage) _beanManager.clone(message);

        // Test cloned message
        //
        assertNotNull(cloneMessage);
        assertEquals(_cloneMessageClass, cloneMessage.getClass());
        assertEquals(message.getMessage(), cloneMessage.getMessage());

        assertNotNull(cloneMessage.getAuthor());

        // Change clone user
        //
        IUser cloneUser = (IUser) _beanManager.clone(user);
        assertNotNull(cloneUser);
        assertEquals(_cloneUserClass, cloneUser.getClass());

        changeAuthorForClone(cloneMessage, cloneUser);

        // Merge message
        //
        IMessage mergeMessage = (IMessage) _beanManager.merge(cloneMessage);

        // Clone verification
        //
        assertNotNull(mergeMessage);
        assertEquals(_domainMessageClass, mergeMessage.getClass());
        assertEquals(message.getMessage(), mergeMessage.getMessage());

        assertNotNull(mergeMessage.getAuthor());
        assertEquals(_domainUserClass, mergeMessage.getAuthor().getClass());
        assertEquals(user.getId(), mergeMessage.getAuthor().getId());

        // Save message
        //
        messageDAO.saveMessage(mergeMessage);
    }

    /**
     * Test change property on client side
     *
     * @throws IllegalAccessException
     * @throws InstantiationException
     */
    public void testAddNewItemAfterClone() throws InstantiationException, IllegalAccessException {
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
        int messageCount = user.getMessageList().size();

        // Clone user
        //
        IUser cloneUser = (IUser) _beanManager.clone(user);
        assertNotNull(cloneUser);
        assertNotNull(cloneUser.getMessageList());
        assertFalse(cloneUser.getMessageList().isEmpty());
        assertEquals(messageCount, cloneUser.getMessageList().size());

        // Add a new message
        cloneUser.addMessage(createNewCloneMessage(cloneUser));
        assertEquals(messageCount + 1, cloneUser.getMessageList().size());

        // Merge user
        //
        IUser mergeUser = (IUser) _beanManager.merge(cloneUser);

        assertNotNull(mergeUser);
        assertNotNull(mergeUser.getMessageList());
        assertFalse(mergeUser.getMessageList().isEmpty());
        assertEquals(messageCount + 1, mergeUser.getMessageList().size());

        // Save for test
        //
        userDAO.saveUser(mergeUser);

        // Check message saving
        //
        user = userDAO.searchUserAndMessagesByLogin(TestHelper.JUNIT_LOGIN);
        assertNotNull(user);
        assertNotNull(user.getMessageList());
        assertFalse(user.getMessageList().isEmpty());
        assertEquals(messageCount + 1, user.getMessageList().size());
    }

    /**
     * Test delete collection on client side
     */
    public void testDeleteCollectionAfterClone() {
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
        IUser cloneUser = (IUser) _beanManager.clone(user);

        // delete all messages
        cloneUser.getMessageList().clear();

        // Merge user
        //
        IUser mergeUser = (IUser) _beanManager.merge(cloneUser);
        assertNotNull(mergeUser);
        assertNotNull(mergeUser.getMessageList());
        assertTrue(mergeUser.getMessageList().isEmpty());

        // Save merged user
        //
        userDAO.saveUser(mergeUser);

        // Reload user to count messages
        //
        user = userDAO.searchUserAndMessagesByLogin(TestHelper.JUNIT_LOGIN);
        assertNotNull(user);
        assertTrue(user.getMessageList() == null || user.getMessageList().isEmpty());
    }

    /**
     * Test nullify collection on client side
     */
    public void testNullifyCollectionAfterClone() {
        // Get UserDAO
        //
        IUserDAO userDAO = DAOFactory.getUserDAO();
        assertNotNull(userDAO);

        // Load user
        //
        IUser user = userDAO.searchUserAndMessagesByLogin(TestHelper.EMPLOYEE_LOGIN);
        assertNotNull(user);
        assertNotNull(user.getMessageList());
        assertFalse(user.getMessageList().isEmpty());

        // Clone user
        //
        IUser cloneUser = (IUser) _beanManager.clone(user);

        // delete all messages
        cloneUser.setMessageList(null);

        // Merge user
        //
        IUser mergeUser = (IUser) _beanManager.merge(cloneUser);
        assertNotNull(mergeUser);
        assertTrue(mergeUser.getMessageList() == null || mergeUser.getMessageList().isEmpty());

        // Save merged user
        //
        userDAO.saveUser(mergeUser);

        // Reload user to count messages
        //
        user = userDAO.searchUserAndMessagesByLogin(TestHelper.EMPLOYEE_LOGIN);
        assertNotNull(user);
        assertTrue(user.getMessageList() == null || user.getMessageList().isEmpty());
    }

    /**
     * Test delete collection on client side
     */
    public void testNewEmptyCollectionAfterClone() {
        // Get UserDAO
        //
        IUserDAO userDAO = DAOFactory.getUserDAO();
        assertNotNull(userDAO);

        // Load user
        //
        IUser user = userDAO.searchUserAndMessagesByLogin(TestHelper.GUEST_LOGIN);
        assertNotNull(user);
        assertNotNull(user.getMessageList());
        assertFalse(user.getMessageList().isEmpty());

        // Clone user
        //
        IUser cloneUser = (IUser) _beanManager.clone(user);

        // delete all messages
        cloneUser.setMessageList(new HashSet<IMessage>());

        // Merge user
        //
        IUser mergeUser = (IUser) _beanManager.merge(cloneUser);
        assertNotNull(mergeUser);
        assertNotNull(mergeUser.getMessageList());
        assertTrue(mergeUser.getMessageList().isEmpty());

        // Save merged user
        //
        userDAO.saveUser(mergeUser);

        // Reload user to count messages
        //
        user = userDAO.searchUserAndMessagesByLogin(TestHelper.JUNIT_LOGIN);
        assertNotNull(user);
        assertTrue(user.getMessageList() == null || user.getMessageList().isEmpty());
    }

    /**
     * Test create collection on client side
     *
     * @throws IllegalAccessException
     * @throws InstantiationException
     */
    public void testNewCollectionAfterClone() throws InstantiationException, IllegalAccessException {
        // Get UserDAO
        //
        IUserDAO userDAO = DAOFactory.getUserDAO();
        assertNotNull(userDAO);

        // Empty user message
        //
        IUser user = userDAO.searchUserAndMessagesByLogin(TestHelper.JUNIT_LOGIN);
        assertNotNull(user);
        user.getMessageList().clear();
        userDAO.saveUser(user);

        assertTrue(user.getMessageList().isEmpty());

        // Clone user
        //
        IUser cloneUser = (IUser) _beanManager.clone(user);

        // add new message
        cloneUser.addMessage(createNewCloneMessage(cloneUser));

        // Merge user
        //
        IUser mergeUser = (IUser) _beanManager.merge(cloneUser);
        assertNotNull(mergeUser);
        assertNotNull(mergeUser.getMessageList());
        assertFalse(mergeUser.getMessageList().isEmpty());

        // Save merged user
        //
        userDAO.saveUser(mergeUser);

        // Reload user to count messages
        //
        user = userDAO.searchUserAndMessagesByLogin(TestHelper.JUNIT_LOGIN);
        assertNotNull(user);
        assertNotNull(user.getMessageList());
        assertEquals(1, user.getMessageList().size());
    }

    /**
     * Test clone and merge ArrayList subclass, such as GWT-Plus PagingList
     */
    public void testCloneAndMergePagingList() {
        // Get UserDAO
        //
        IUserDAO userDAO = DAOFactory.getUserDAO();
        assertNotNull(userDAO);

        // Fill paging list
        //
        PagingList list = new PagingList();
        list.addAll(userDAO.loadAll());
        assertFalse(list.isEmpty());
        list.setTotalRecords(list.size());

        // Clone paging list
        //
        PagingList cloneList = (PagingList) _beanManager.clone(list);
        assertNotNull(cloneList);
        assertEquals(list.getTotalRecords(), cloneList.getTotalRecords());
        assertEquals(list.size(), cloneList.size());

        // Merge paging list
        //
        PagingList mergeList = (PagingList) _beanManager.merge(cloneList);
        assertNotNull(mergeList);
        assertEquals(list.getTotalRecords(), mergeList.getTotalRecords());
        assertEquals(list.size(), mergeList.size());
    }

    /**
     * Test clone of a loaded user and associated messages
     */
    public void testCloneAndMergeBaseListLoadResult() {
        // Get UserDAO
        //
        IUserDAO userDAO = DAOFactory.getUserDAO();
        assertNotNull(userDAO);

        // Create BaseListLoadResult
        //
        BaseListLoadResult<IUser> userList = new BaseListLoadResult<IUser>(userDAO.loadAll());
        assertNotNull(userList.getData());
        assertFalse(userList.getData().isEmpty());

        // Clone user list
        //
        BaseListLoadResult<IUser> cloneUserList = (BaseListLoadResult<IUser>) _beanManager.clone(userList);

        // Test cloned user list
        //
        assertNotNull(cloneUserList);
        assertNotNull(cloneUserList.getData());
        assertFalse(cloneUserList.getData().isEmpty());

        // Merge user list
        //
        BaseListLoadResult<IUser> mergeUserList = (BaseListLoadResult<IUser>) _beanManager.merge(cloneUserList);

        // Test merged user list
        //
        assertNotNull(mergeUserList);
        assertNotNull(mergeUserList.getData());
        assertFalse(mergeUserList.getData().isEmpty());
    }

    /**
     * Test clone of Project item
     */
    public void testCloneAndMergeBigDecimal() {
        // Create Big Decimal
        //
        BigDecimal decimal = new BigDecimal(1);

        // Clone decimal
        //
        BigDecimal cloneDecimal = (BigDecimal) _beanManager.clone(decimal);

        // Test cloned decimal
        //
        assertNotNull(cloneDecimal);
        assertSame(decimal, cloneDecimal);

        // Merge decimal
        //
        BigDecimal mergeDecimal = (BigDecimal) _beanManager.merge(cloneDecimal);

        // Test merged decimal
        //
        assertNotNull(mergeDecimal);
        assertEquals(decimal, mergeDecimal);
    }

    /**
     * Test delete property on client side
     */
    /*
     * public void testDeleteManyToManyAfterClone() { // Get UserDAO // IUserDAO userDAO = DAOFactory.getUserDAO();
     * assertNotNull(userDAO); // Load user // IUser user = userDAO.searchUserAndGroupsByLogin(TestHelper.GUEST_LOGIN);
     * assertNotNull(user); assertNotNull(user.getGroupList()); assertFalse(user.getGroupList().isEmpty()); int
     * groupCount = user.getGroupList().size(); // Clone user // IUser cloneUser = (IUser) _beanManager.clone(user); //
     * remove user from group IGroup group = null; for (IGroup cloneGroup : cloneUser.getGroupList()) { if
     * (cloneGroup.getName().equals(TestHelper.GUEST_GROUP)) { group = cloneGroup; break; } } assertNotNull(group);
     * cloneUser.removeUserFromGroup(group); // Merge user // IUser mergeUser = (IUser) _beanManager.merge(cloneUser);
     * assertNotNull(mergeUser); assertNotNull(mergeUser.getGroupList());
     * assertFalse(mergeUser.getGroupList().isEmpty()); assertEquals(groupCount -1, mergeUser.getGroupList().size()); //
     * Save merged user // userDAO.saveUser(mergeUser); // Reload user to count group // user =
     * userDAO.searchUserAndGroupsByLogin(TestHelper.GUEST_LOGIN); assertNotNull(user);
     * assertNotNull(user.getGroupList()); assertFalse(user.getGroupList().isEmpty()); assertEquals(groupCount -1,
     * user.getGroupList().size()); }
     */

    /**
     * Test clone of a loaded user and associated address (component type)
     */
    public void testCloneAndMergeComponentType() {
        // This test does not work with JPA because the country
        // is serialized instead of load
        // Look like relevant to Hibernate bug
        //
        if (_testComponentType == false) {
            return;
        }

        // Get UserDAO
        //
        IUserDAO userDAO = DAOFactory.getUserDAO();
        assertNotNull(userDAO);

        // Load user
        //
        IUser user = userDAO.searchUserAndMessagesByLogin(TestHelper.JUNIT_LOGIN);
        assertNotNull(user);

        // Address loading verification
        //
        assertNotNull(user.getAddress());

        // Clone user
        //
        IUser cloneUser = (IUser) _beanManager.clone(user);

        // Test cloned user
        //
        assertNotNull(cloneUser);
        assertEquals(_cloneUserClass, cloneUser.getClass());

        // Address cloning verification
        assertNotNull(cloneUser.getAddress());

        // Merge user
        //
        IUser mergeUser = (IUser) _beanManager.merge(cloneUser);

        // Test merged user
        //
        assertNotNull(mergeUser);
        assertEquals(_domainUserClass, _beanManager.getPersistenceUtil().getUnenhancedClass(mergeUser.getClass()));

        // Address merging verification
        assertNotNull(mergeUser.getAddress());

    }

    /**
     * Test merge a new entity object graph (created on client side)
     */
    public void testMergeOnNewEntities() throws Exception {
        // Create clone user
        //
        IUser cloneUser = createNewCloneUser();

        // Create associated messages
        //
        IMessage message1 = createNewCloneMessage(cloneUser);
        cloneUser.addMessage(message1);

        IMessage message2 = createNewCloneMessage(cloneUser);
        cloneUser.addMessage(message2);

        // Merge user
        //
        IUser mergeUser = (IUser) _beanManager.merge(cloneUser);

        // Test merged user
        //
        assertNotNull(mergeUser);
        assertNotNull(mergeUser.getMessageList());
        assertEquals(cloneUser.getMessageList().size(), mergeUser.getMessageList().size());

    }

    /**
     * Test merge a new entity object graph (created on client side)
     */
    @SuppressWarnings("unchecked")
    public void testMergeTwiceOnNewEntities() throws Exception {
        // Create clone user
        //
        IUser cloneUser = createNewCloneUser();

        // Create associated messages
        //
        IMessage message = createNewCloneMessage(cloneUser);
        cloneUser.addMessage(message);
        assertEquals(message.getAuthor(), cloneUser);

        // Create a list of all these items
        //
        List<Object> cloneList = new ArrayList<Object>(2);
        cloneList.add(cloneUser);
        cloneList.add(message);

        // Merge list
        //
        List<Object> mergeList = (List<Object>) _beanManager.merge(cloneList);

        // Test merged object
        //
        assertNotNull(mergeList);
        IUser mergeUser = (IUser) mergeList.get(0);
        IMessage mergeMessage = (IMessage) mergeList.get(1);
        assertNotNull(mergeUser);
        assertNotNull(mergeMessage);
        assertEquals(mergeUser, mergeMessage.getAuthor());
    }

    // -------------------------------------------------------------------------
    //
    // Internal methods
    //
    // -------------------------------------------------------------------------
    /**
     * Create a new message
     *
     * @throws IllegalAccessException
     * @throws InstantiationException
     */
    protected IMessage createNewCloneMessage(IUser user) throws InstantiationException, IllegalAccessException {
        // Create message
        //
        IMessage result = (IMessage) _cloneMessageClass.newInstance();
        result.setDate(new Date());
        result.setMessage("test message");

        // Change author
        //
        changeAuthorForClone(result, user);

        return result;
    }

    /**
     * Create a new message
     *
     * @throws IllegalAccessException
     * @throws InstantiationException
     */
    protected IMessage createNewMessage(IUser user) throws InstantiationException, IllegalAccessException {
        // Create message
        //
        IMessage result = (IMessage) _domainMessageClass.newInstance();
        result.setDate(new Date());
        result.setMessage("test message");

        // Change author
        //
        changeAuthorForDomain(result, user);

        return result;
    }

    /**
     * Create a new user
     *
     * @throws IllegalAccessException
     * @throws InstantiationException
     */
    protected IUser createNewCloneUser() throws InstantiationException, IllegalAccessException {
        // Create user
        //
        IUser result = (IUser) _cloneUserClass.newInstance();

        return result;
    }

    /**
     * Create a new user
     *
     * @throws IllegalAccessException
     * @throws InstantiationException
     */
    protected IUser createNewUser() throws InstantiationException, IllegalAccessException {
        // Create user
        //
        IUser result = (IUser) _domainUserClass.newInstance();

        return result;
    }

    /**
     * Change the author of the message
     *
     * @param message
     * @param user
     */
    protected abstract void changeAuthorForClone(IMessage message, IUser user);

    /**
     * Change the author of the message
     *
     * @param message
     * @param user
     */
    protected abstract void changeAuthorForDomain(IMessage message, IUser user);
}
