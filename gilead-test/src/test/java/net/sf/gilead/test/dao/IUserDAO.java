package net.sf.gilead.test.dao;

import java.util.List;

import net.sf.gilead.test.domain.interfaces.IUser;

public interface IUserDAO {

    /**
     * Load the user with the argument ID
     */
    IUser loadUser(Integer id);

    /**
     * Load the user with the argument login
     */
    IUser loadUserByLogin(String login);

    /**
     * Load the user with the argument login
     */
    IUser searchUserAndMessagesByLogin(String login);

    /**
     * Load the user with the argument login
     */
    IUser searchUserAndGroupsByLogin(String login);

    /**
     * Load all the users
     */
    List<IUser> loadAll();

    /**
     * Load all the users and associated messages
     */
    List<IUser> loadAllUserAndMessages();

    /**
     * Count all the users
     */
    int countAll();

    /**
     * Save the argument user
     * 
     * @param user the user to save or create
     */
    void saveUser(IUser user);
}
