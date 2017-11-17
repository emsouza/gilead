package net.sf.gilead.test;

import net.sf.gilead.test.dao.IEmployeeDAO;
import net.sf.gilead.test.dao.IMessageDAO;
import net.sf.gilead.test.dao.IUserDAO;
import net.sf.gilead.test.dao.hibernate.EmployeeDAO;
import net.sf.gilead.test.dao.hibernate.MessageDAO;
import net.sf.gilead.test.dao.hibernate.UserDAO;

/**
 * Factory for test DAO
 * 
 * @author bruno.marchesson
 */
public class DAOFactory {

    /**
     * @return the user DAO
     */
    public static IUserDAO getUserDAO() {
        return new UserDAO();
    }

    /**
     * @return the employee DAO
     */
    public static IEmployeeDAO getEmployeeDAO() {
        return new EmployeeDAO();
    }

    /**
     * @return the user DAO
     */
    public static IMessageDAO getMessageDAO() {
        return new MessageDAO();
    }
}
