package net.sf.gilead.core;

import java.io.FileNotFoundException;
import java.io.Serializable;
import java.util.Date;
import java.util.List;

import net.sf.gilead.configuration.ConfigurationHelper;
import net.sf.gilead.core.beanlib.mapper.DirectoryClassMapper;
import net.sf.gilead.core.beanlib.mapper.ProxyClassMapper;
import net.sf.gilead.core.hibernate.HibernateUtil;
import net.sf.gilead.core.serialization.JBossProxySerialization;
import net.sf.gilead.core.store.stateful.InMemoryProxyStore;
import net.sf.gilead.core.store.stateless.StatelessProxyStore;
import net.sf.gilead.gwt.GwtConfigurationHelper;
import net.sf.gilead.test.DAOFactory;
import net.sf.gilead.test.HibernateContext;
import net.sf.gilead.test.HibernateContext.Context;
import net.sf.gilead.test.domain.interfaces.IAddress;
import net.sf.gilead.test.domain.interfaces.IEmployee;
import net.sf.gilead.test.domain.interfaces.IGroup;
import net.sf.gilead.test.domain.interfaces.IMessage;
import net.sf.gilead.test.domain.interfaces.IUser;

/**
 * Static helper class for test
 *
 * @author bruno.marchesson
 */
public class TestHelper {
    // ----
    // Constant
    // ----
    /**
     * The guest login
     */
    public final static String GUEST_LOGIN = "guest";

    /**
     * The JUnit login
     */
    public final static String JUNIT_LOGIN = "junit";

    /**
     * The employee login
     */
    public final static String EMPLOYEE_LOGIN = "employee";

    /**
     * The 'test' group
     */
    public final static String TEST_GROUP = "test";

    /**
     * The 'guest' group
     */
    public final static String GUEST_GROUP = "guests";

    // -------------------------------------------------------------------------
    //
    // Static helper
    //
    // -------------------------------------------------------------------------
    /**
     * Check that the database is initialized
     *
     * @return
     */
    public static boolean isInitialized() {
        return (DAOFactory.getUserDAO().countAll() > 0);
    }

    /**
     * Initialise DB
     */
    public static void initializeDB() {
        // Create guest & test group
        //
        IGroup guestGroup = createGroup();
        guestGroup.setName(GUEST_GROUP);

        IGroup testGroup = createGroup();
        testGroup.setName(TEST_GROUP);

        // Create guest user (no password)
        //
        IUser guestUser = createUser();
        guestUser.setLogin(GUEST_LOGIN);
        guestUser.setFirstName("No");
        guestUser.setLastName("name");

        // address
        IAddress address = createAddress();
        address.setStreet("Baker street");
        address.setCity("London");
        guestUser.setAddress(address);

        // create welcome message
        IMessage guestMessage = createMessage();
        guestMessage.setMessage("Welcome in Gilead sample application");
        guestMessage.setDate(new Date());
        guestUser.addMessage(guestMessage);

        // group
        guestUser.addToGroup(guestGroup);
        guestUser.addToGroup(testGroup);

        // save user (message and group are cascaded)
        DAOFactory.getUserDAO().saveUser(guestUser);

        // Create JUnit user
        //
        IUser junitUser = createUser();
        junitUser.setLogin(JUNIT_LOGIN);
        junitUser.setPassword("junit");
        junitUser.setFirstName("Unit");
        junitUser.setLastName("Test");

        // address
        address = createAddress();
        address.setStreet("Main street");
        address.setCity("Castle Rock");
        junitUser.setAddress(address);

        // create message
        IMessage junitMessage = createMessage();
        junitMessage.setMessage("JUnit first message");
        junitMessage.setDate(new Date());
        junitUser.addMessage(junitMessage);

        // group
        junitUser.addToGroup(testGroup);

        // save user (message and group are cascaded)
        DAOFactory.getUserDAO().saveUser(junitUser);

        // Create Employee user
        //
        IEmployee employee = createEmployee();
        employee.setLogin(EMPLOYEE_LOGIN);
        employee.setPassword("employee");
        employee.setFirstName("John");
        employee.setLastName("Doe");
        employee.setEmail("john.doe@gilead.com");

        // address
        address = createAddress();
        address.setStreet("Champs Elysee");
        address.setCity("Paris");
        employee.setAddress(address);

        // create message
        IMessage employeeMessage = createMessage();
        employeeMessage.setMessage("John Doe's message");
        employeeMessage.setDate(new Date());
        employee.addMessage(employeeMessage);

        // group
        employee.addToGroup(guestGroup);

        // save user (message and group are cascaded)
        DAOFactory.getUserDAO().saveUser(employee);
    }

    /**
     * Check that the database has a lot of data
     *
     * @return
     */
    public static boolean isLotOfDataCreated() {
        return (DAOFactory.getUserDAO().loadUserByLogin(JUNIT_LOGIN) != null);
    }

    /**
     * Initialise DB with lot of data
     */
    public static void initializeLotOfData(int messageCount) {
        // Create volumetry
        //
        IUser bigUser = createUser();
        bigUser.setLogin(JUNIT_LOGIN);
        bigUser.setFirstName("Performance");
        bigUser.setLastName("Test");

        // address
        IAddress address = createAddress();
        address.setStreet("Baker street");
        address.setCity("Big City");
        bigUser.setAddress(address);

        for (int index = 0; index < messageCount; index++) {
            // create message
            IMessage message = createMessage();
            message.setMessage("Message " + index);
            message.setDate(new Date());
            bigUser.addMessage(message);

            // batch save
            if (index % 10 == 0) {
                DAOFactory.getUserDAO().saveUser(bigUser);
            }
        }

        // save user
        DAOFactory.getUserDAO().saveUser(bigUser);
    }

    /**
     * @return an existing user ID
     */
    public static Serializable getExistingUserId() {
        // Load user list
        //
        List<IUser> userList = DAOFactory.getUserDAO().loadAll();

        if ((userList == null) || (userList.isEmpty() == true)) {
            return null;
        } else {
            // Return first element
            //
            return userList.get(0).getId();
        }
    }

    /**
     * @return an existing message ID
     */
    public static Integer getExistingMessageId() {
        // Load user list
        //
        List<IMessage> messageList = DAOFactory.getMessageDAO().loadAllMessage();

        if ((messageList == null) || (messageList.isEmpty() == true)) {
            return null;
        } else {
            // Return first element
            //
            return messageList.get(0).getId();
        }
    }

    // -------------------------------------------------------------------------
    //
    // Bean manager initialisation
    //
    // -------------------------------------------------------------------------
    /**
     * Init bean manager for stateless mode
     */
    public static PersistentBeanManager initStatelessBeanManager() {
        HibernateContext.setContext(HibernateContext.Context.stateless);

        // force init
        PersistentBeanManager.getInstance().setPersistenceUtil(null);

        return ConfigurationHelper.initStatelessBeanManager(new HibernateUtil(HibernateContext.getSessionFactory(), null));
    }

    /**
     * Init bean manager for stateless mode for GWT
     */
    public static PersistentBeanManager initGwtStatelessBeanManager() {
        HibernateContext.setContext(HibernateContext.Context.gwt);

        // force init
        PersistentBeanManager.getInstance().setPersistenceUtil(null);

        return GwtConfigurationHelper.initGwtStatelessBeanManager(new HibernateUtil(HibernateContext.getSessionFactory(), null));
    }

    /**
     * Init bean manager for stateless mode for legacy Gilead 1.2 (encoded proxy info)
     */
    public static PersistentBeanManager initLegacyStatelessBeanManager() {
        HibernateContext.setContext(HibernateContext.Context.legacy);

        // force init
        PersistentBeanManager.getInstance().setPersistenceUtil(null);

        return ConfigurationHelper.initLegacyStatelessBeanManager(new HibernateUtil(HibernateContext.getSessionFactory(), null));
    }

    /**
     * Init bean manager for stateless mode
     */
    public static PersistentBeanManager initStatefulBeanManager() {
        HibernateContext.setContext(HibernateContext.Context.stateful);
        HibernateUtil persistenceUtil = new HibernateUtil();
        persistenceUtil.setSessionFactory(HibernateContext.getSessionFactory());

        InMemoryProxyStore proxyStore = new InMemoryProxyStore();
        proxyStore.setPersistenceUtil(persistenceUtil);

        PersistentBeanManager beanManager = PersistentBeanManager.getInstance(); // new PersistentBeanManager();
        beanManager.setPersistenceUtil(persistenceUtil);
        beanManager.setProxyStore(proxyStore);
        beanManager.setClassMapper(null);

        return beanManager;
    }

    /**
     * Init bean manager for dynamic proxy mode
     *
     * @throws FileNotFoundException
     */
    public static PersistentBeanManager initProxyBeanManager() throws FileNotFoundException {
        HibernateContext.setContext(HibernateContext.Context.proxy);

        // force init
        PersistentBeanManager.getInstance().setPersistenceUtil(null);

        return GwtConfigurationHelper.initGwtProxyBeanManager(new HibernateUtil(HibernateContext.getSessionFactory(), null));
    }

    /**
     * Init bean manager for dynamic proxy mode
     *
     * @throws FileNotFoundException
     */
    public static PersistentBeanManager initProxy14BeanManager() throws FileNotFoundException {
        HibernateContext.setContext(HibernateContext.Context.proxy);

        HibernateUtil persistenceUtil = new HibernateUtil();
        persistenceUtil.setSessionFactory(HibernateContext.getSessionFactory());

        PersistentBeanManager beanManager = PersistentBeanManager.getInstance(); // new PersistentBeanManager();
        beanManager.setPersistenceUtil(persistenceUtil);

        StatelessProxyStore proxyStore = new StatelessProxyStore();
        proxyStore.setProxySerializer(new JBossProxySerialization());
        beanManager.setProxyStore(proxyStore);

        ProxyClassMapper classMapper = new ProxyClassMapper();
        classMapper.setPersistenceUtil(persistenceUtil);
        classMapper.setJava5(false);
        beanManager.setClassMapper(classMapper);

        return beanManager;
    }

    /**
     * Init bean manager for Java5 support mode
     */
    public static PersistentBeanManager initJava5SupportBeanManager() {
        HibernateContext.setContext(HibernateContext.Context.java5);

        HibernateUtil persistenceUtil = new HibernateUtil();
        persistenceUtil.setSessionFactory(HibernateContext.getSessionFactory());

        PersistentBeanManager beanManager = PersistentBeanManager.getInstance(); // new PersistentBeanManager();
        beanManager.setPersistenceUtil(persistenceUtil);
        beanManager.setProxyStore(new StatelessProxyStore());

        DirectoryClassMapper classMapper = new DirectoryClassMapper();
        classMapper.setRootDomainPackage("net.sf.gilead.test.domain.java5");
        classMapper.setRootClonePackage("net.sf.gilead.test.domain.dto");
        classMapper.setCloneSuffix("DTO");
        beanManager.setClassMapper(classMapper);

        return beanManager;
    }

    /**
     * Init bean manager for annotated Java5 mode
     */
    public static PersistentBeanManager initJava5AnnotatedBeanManager() {
        HibernateContext.setContext(HibernateContext.Context.annotated);

        PersistentBeanManager.getInstance().setPersistenceUtil(null);

        return ConfigurationHelper.initStatelessBeanManager(new HibernateUtil(HibernateContext.getSessionFactory(), null));
    }

    // --------------------------------------------------------------------------
    //
    // Internal methods
    //
    // --------------------------------------------------------------------------
    /**
     * Create a new user (depends on the server configuration)
     */
    private static IUser createUser() {
        Context context = HibernateContext.getContext();

        if (context == Context.stateless) {
            // stateless
            return new net.sf.gilead.test.domain.stateless.User();
        } else if (context == Context.gwt) {
            // gwt stateless
            return new net.sf.gilead.test.domain.gwt.User();
        } else if (context == Context.legacy) {
            // legacy stateless
            return new net.sf.gilead.test.domain.legacy.User();
        } else if (context == Context.stateful) {
            // stateful
            return new net.sf.gilead.test.domain.stateful.User();
        } else if (context == Context.proxy) {
            // dynamic proxy
            return new net.sf.gilead.test.domain.proxy.User();
        } else if (context == Context.java5) {
            // Java5
            return new net.sf.gilead.test.domain.java5.User();
        } else {
            // Annotated Java5
            return new net.sf.gilead.test.domain.annotated.User();
        }
    }

    /**
     * Create a new employee (depends on the server configuration)
     */
    private static IEmployee createEmployee() {
        Context context = HibernateContext.getContext();

        if (context == Context.stateless) {
            // stateless
            return new net.sf.gilead.test.domain.stateless.Employee();
        } else if (context == Context.gwt) {
            // gwt stateless
            return new net.sf.gilead.test.domain.gwt.Employee();
        } else if (context == Context.legacy) {
            // legacy stateless
            return new net.sf.gilead.test.domain.legacy.Employee();
        } else if (context == Context.stateful) {
            // stateful
            return new net.sf.gilead.test.domain.stateful.Employee();
        } else if (context == Context.proxy) {
            // dynamic proxy
            return new net.sf.gilead.test.domain.proxy.Employee();
        } else if (context == Context.java5) {
            // Java5
            return new net.sf.gilead.test.domain.java5.Employee();
        } else {
            // Annotated Java5
            return new net.sf.gilead.test.domain.annotated.Employee();
        }
    }

    /**
     * Create a new message (depends on the server configuration)
     */
    private static IMessage createMessage() {
        Context context = HibernateContext.getContext();

        if (context == Context.stateless) {
            // stateless
            return new net.sf.gilead.test.domain.stateless.Message();
        } else if (context == Context.gwt) {
            // gwt stateless
            return new net.sf.gilead.test.domain.gwt.Message();
        } else if (context == Context.legacy) {
            // legacy stateless
            return new net.sf.gilead.test.domain.legacy.Message();
        } else if (context == Context.stateful) {
            // stateful
            return new net.sf.gilead.test.domain.stateful.Message();
        } else if (context == Context.proxy) {
            // dynamic proxy
            return new net.sf.gilead.test.domain.proxy.Message();
        } else if (context == Context.java5) {
            // Java5
            return new net.sf.gilead.test.domain.java5.Message();
        } else {
            // Annotated Java5
            return new net.sf.gilead.test.domain.annotated.Message();
        }
    }

    /**
     * Create a new message (depends on the server configuration)
     */
    private static IGroup createGroup() {
        Context context = HibernateContext.getContext();

        if (context == Context.stateless) {
            // stateless
            return new net.sf.gilead.test.domain.stateless.Group();
        } else if (context == Context.gwt) {
            // gwt stateless
            return new net.sf.gilead.test.domain.gwt.Group();
        } else if (context == Context.legacy) {
            // legacy stateless
            return new net.sf.gilead.test.domain.legacy.Group();
        } else if (context == Context.stateful) {
            // stateful
            return new net.sf.gilead.test.domain.stateful.Group();
        } else if (context == Context.proxy) {
            // dynamic proxy
            return new net.sf.gilead.test.domain.proxy.Group();
        } else if (context == Context.java5) {
            // Java5
            return new net.sf.gilead.test.domain.java5.Group();
        } else {
            // Annotated Java5
            return new net.sf.gilead.test.domain.annotated.Group();
        }
    }

    /**
     * Create a new address (depends on the server configuration)
     */
    private static IAddress createAddress() {
        Context context = HibernateContext.getContext();

        if (context == Context.stateless) {
            // stateless
            return new net.sf.gilead.test.domain.stateless.Address();
        } else if (context == Context.gwt) {
            // gwt stateless
            return new net.sf.gilead.test.domain.gwt.Address();
        } else if (context == Context.legacy) {
            // legacy stateless
            return new net.sf.gilead.test.domain.legacy.Address();
        } else if (context == Context.stateful) {
            // stateful
            return new net.sf.gilead.test.domain.stateful.Address();
        } else if (context == Context.proxy) {
            // dynamic proxy
            return new net.sf.gilead.test.domain.proxy.Address();
        } else if (context == Context.java5) {
            // Java5
            return new net.sf.gilead.test.domain.java5.Address();
        } else {
            // Annotated Java5
            return new net.sf.gilead.test.domain.annotated.Address();
        }
    }
}
