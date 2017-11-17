package net.sf.gilead.core.loading.gwt.server;

import net.sf.gilead.core.TestHelper;
import net.sf.gilead.core.loading.gwt.client.StatelessInitService;
import net.sf.gilead.gwt.PersistentRemoteService;
import net.sf.gilead.test.DAOFactory;
import net.sf.gilead.test.dao.IMessageDAO;
import net.sf.gilead.test.dao.IUserDAO;
import net.sf.gilead.test.domain.gwt.Message;
import net.sf.gilead.test.domain.gwt.User;

public class StatelessInitServiceImpl extends PersistentRemoteService implements StatelessInitService {

    private static final long serialVersionUID = 9068940871396469148L;

    /**
     * Initialization
     */
    public void initDB() {
        // Init Hibernate context
        setBeanManager(TestHelper.initGwtStatelessBeanManager());

        // Init DB if needed
        if (TestHelper.isInitialized() == false) {
            TestHelper.initializeDB();
        }
    }

    @Override
    public Message loadTestMessage() {
        initDB();

        // Load last message
        IMessageDAO messageDAO = DAOFactory.getMessageDAO();
        return (Message) messageDAO.loadLastMessage();
    }

    @Override
    public User loadTestUser() {
        // Load test user
        IUserDAO userDAO = DAOFactory.getUserDAO();
        return (User) userDAO.loadUserByLogin(TestHelper.JUNIT_LOGIN);
    }
}
