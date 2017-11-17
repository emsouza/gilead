package net.sf.gilead.performance.gwt.server;

import javax.servlet.ServletException;

import net.sf.gilead.core.TestHelper;
import net.sf.gilead.gwt.PersistentRemoteService;
import net.sf.gilead.performance.gwt.client.PerformanceService;
import net.sf.gilead.test.DAOFactory;
import net.sf.gilead.test.dao.IUserDAO;
import net.sf.gilead.test.domain.interfaces.IUser;

/**
 * GWT performance service implementation
 *
 * @author bruno.marchesson
 */
public class Java5PerformanceServiceImpl extends PersistentRemoteService implements PerformanceService {

    private static final long serialVersionUID = -707833353923499944L;

    @Override
    public void init() throws ServletException {
        super.init();

        // Init Hibernate context
        setBeanManager(TestHelper.initJava5AnnotatedBeanManager());

        // Init DB if needed
        if (TestHelper.isInitialized() == false) {
            TestHelper.initializeDB();
        }
        if (TestHelper.isLotOfDataCreated() == false) {
            TestHelper.initializeLotOfData(100);
        }
    }

    @Override
    public IUser loadUserAndMessages() {
        // Get UserDAO
        IUserDAO userDAO = DAOFactory.getUserDAO();

        // Load user
        IUser user = userDAO.searchUserAndMessagesByLogin(TestHelper.JUNIT_LOGIN);
        return user;
    }
}
