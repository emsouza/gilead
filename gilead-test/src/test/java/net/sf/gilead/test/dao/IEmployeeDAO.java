package net.sf.gilead.test.dao;

import net.sf.gilead.test.domain.interfaces.IEmployee;

/**
 * Interface for employee DAO
 * 
 * @author bruno.marchesson
 */
public interface IEmployeeDAO {

    /**
     * Load the employee with the argument ID
     */
    IEmployee loadEmployee(Integer id);

    /**
     * Load the employee with the argument login
     */
    IEmployee searchEmployeeAndMessagesByLogin(String login);
}