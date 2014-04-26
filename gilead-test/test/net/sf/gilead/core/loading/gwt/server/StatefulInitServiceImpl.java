package net.sf.gilead.core.loading.gwt.server;

import javax.servlet.ServletException;

import net.sf.gilead.core.TestHelper;
import net.sf.gilead.core.loading.gwt.client.StatefulInitService;
import net.sf.gilead.gwt.PersistentRemoteService;
import net.sf.gilead.test.DAOFactory;
import net.sf.gilead.test.dao.IMessageDAO;
import net.sf.gilead.test.dao.IUserDAO;
import net.sf.gilead.test.domain.stateful.Message;
import net.sf.gilead.test.domain.stateful.User;

public class StatefulInitServiceImpl extends PersistentRemoteService implements StatefulInitService {
    /**
     * Serialization ID
     */
    private static final long serialVersionUID = 9068940871396469148L;

    // -------------------------------------------------------------------------
    //
    // Service init
    //
    // -------------------------------------------------------------------------
    @Override
    public void init() throws ServletException {
        super.init();

        // Init Hibernate context
        //
        setBeanManager(TestHelper.initStatefulBeanManager());

        // Init DB if needed
        //
        if (TestHelper.isInitialized() == false) {
            TestHelper.initializeDB();
        }
    }

    // -------------------------------------------------------------------------
    //
    // Service methods
    //
    // -------------------------------------------------------------------------
    /*
     * (non-Javadoc)
     * @see net.sf.gilead.core.loading.gwt.client.InitService#loadTestMessage()
     */
    @Override
    public Message loadTestMessage() {
        // Load last message
        //
        IMessageDAO messageDAO = DAOFactory.getMessageDAO();
        return (Message) messageDAO.loadLastMessage();
    }

    /*
     * (non-Javadoc)
     * @see net.sf.gilead.core.loading.gwt.client.InitService#loadTestUser()
     */
    @Override
    public User loadTestUser() {
        // Load test user
        //
        IUserDAO userDAO = DAOFactory.getUserDAO();
        return (User) userDAO.loadUserByLogin(TestHelper.JUNIT_LOGIN);
    }

}
