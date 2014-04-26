package net.sf.gilead.test.dao;

import java.util.List;

import net.sf.gilead.test.domain.interfaces.IUser;

public interface IUserDAO {

    // -------------------------------------------------------------------------
    //
    // Public interface
    //
    // -------------------------------------------------------------------------
    /**
     * Load the user with the argument ID
     */
    public IUser loadUser(Integer id);

    /**
     * Load the user with the argument login
     */
    public IUser loadUserByLogin(String login);

    /**
     * Load the user with the argument login
     */
    public IUser searchUserAndMessagesByLogin(String login);

    /**
     * Load the user with the argument login
     */
    public IUser searchUserAndGroupsByLogin(String login);

    /**
     * Load all the users
     */
    public List<IUser> loadAll();

    /**
     * Load all the users and associated messages
     */
    public List<IUser> loadAllUserAndMessages();

    /**
     * Count all the users
     */
    public int countAll();

    /**
     * Save the argument user
     * 
     * @param user the user to save or create
     */
    public void saveUser(IUser user);

}