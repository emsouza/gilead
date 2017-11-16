package net.sf.gilead.test.dao;

import net.sf.gilead.test.domain.interfaces.IEmployee;

/**
 * Interface for employee DAO
 * 
 * @author bruno.marchesson
 */
public interface IEmployeeDAO {

    // -------------------------------------------------------------------------
    //
    // Public interface
    //
    // -------------------------------------------------------------------------
    /**
     * Load the employee with the argument ID
     */
    public IEmployee loadEmployee(Integer id);

    /**
     * Load the employee with the argument login
     */
    public IEmployee searchEmployeeAndMessagesByLogin(String login);
}