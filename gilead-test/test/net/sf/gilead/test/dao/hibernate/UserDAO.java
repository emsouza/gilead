/**
 *
 */
package net.sf.gilead.test.dao.hibernate;

import java.util.List;

import net.sf.gilead.test.HibernateContext;
import net.sf.gilead.test.dao.IUserDAO;
import net.sf.gilead.test.domain.interfaces.IUser;

import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;

/**
 * DAO for user beans. This implementation use HQL to work seamlessly with all implementation of the Message domain
 * class (Java 1.4 _ stateful or stateless _ and Java5)
 * 
 * @author bruno.marchesson
 */
public class UserDAO implements IUserDAO {
    // -------------------------------------------------------------------------
    //
    // Public interface
    //
    // -------------------------------------------------------------------------
    /**
     * Load the user with the argument ID
     */
    @Override
    public IUser loadUser(Integer id) {
        Session session = null;
        Transaction transaction = null;
        try {
            // Get session
            //
            session = HibernateContext.getSessionFactory().getCurrentSession();
            transaction = session.beginTransaction();

            // Create query
            //
            Query query = session.createQuery("from User user where user.id=:id");
            query.setInteger("id", id);

            // Execute query
            //
            IUser user = (IUser) query.uniqueResult();
            transaction.commit();

            return user;
        } catch (RuntimeException e) {
            // Rollback
            //
            transaction.rollback();
            throw e;
        }
    }

    /**
     * Load the user with the argument login
     */
    @Override
    public IUser loadUserByLogin(String login) {
        Session session = null;
        Transaction transaction = null;
        try {
            // Get session
            //
            session = HibernateContext.getSessionFactory().getCurrentSession();
            transaction = session.beginTransaction();

            // Create query
            //
            StringBuffer hqlQuery = new StringBuffer();
            hqlQuery.append("from User user where user.login=:login");

            // Fill query
            //
            Query query = session.createQuery(hqlQuery.toString());
            query.setString("login", login);

            // Execute query
            //
            IUser user = (IUser) query.uniqueResult();
            transaction.commit();

            return user;
        } catch (RuntimeException e) {
            // Rollback
            //
            transaction.rollback();
            throw e;
        }
    }

    /**
     * Load the user with the argument login
     */
    @Override
    public IUser searchUserAndMessagesByLogin(String login) {
        Session session = null;
        Transaction transaction = null;
        try {
            // Get session
            //
            session = HibernateContext.getSessionFactory().getCurrentSession();
            transaction = session.beginTransaction();

            // Create query
            //
            StringBuffer hqlQuery = new StringBuffer();
            hqlQuery.append("from User user");
            hqlQuery.append(" left join fetch user.messageList");
            hqlQuery.append(" where user.login=:login");

            // Fill query
            //
            Query query = session.createQuery(hqlQuery.toString());
            query.setString("login", login);

            // Execute query
            //
            IUser user = (IUser) query.uniqueResult();
            transaction.commit();

            return user;
        } catch (RuntimeException e) {
            // Rollback
            //
            transaction.rollback();
            throw e;
        }
    }

    /**
     * Load the user with the argument login
     */
    @Override
    public IUser searchUserAndGroupsByLogin(String login) {
        Session session = null;
        Transaction transaction = null;
        try {
            // Get session
            //
            session = HibernateContext.getSessionFactory().getCurrentSession();
            transaction = session.beginTransaction();

            // Create query
            //
            StringBuffer hqlQuery = new StringBuffer();
            hqlQuery.append("from User user");
            hqlQuery.append(" left join fetch user.groupList");
            hqlQuery.append(" where user.login=:login");

            // Fill query
            //
            Query query = session.createQuery(hqlQuery.toString());
            query.setString("login", login);

            // Execute query
            //
            IUser user = (IUser) query.uniqueResult();
            transaction.commit();

            return user;
        } catch (RuntimeException e) {
            // Rollback
            //
            transaction.rollback();
            throw e;
        }
    }

    /**
     * Load all the users
     */
    @Override
    @SuppressWarnings("unchecked")
    public List<IUser> loadAll() {
        Session session = null;
        Transaction transaction = null;
        try {
            // Get session
            //
            session = HibernateContext.getSessionFactory().getCurrentSession();
            transaction = session.beginTransaction();

            // Create query
            //
            Query query = session.createQuery("from User user");

            // Execute query
            //
            List<IUser> list = query.list();
            transaction.commit();

            return list;
        } catch (RuntimeException e) {
            // Rollback
            //
            transaction.rollback();
            throw e;
        }
    }

    /**
     * Load all the users
     */
    @Override
    @SuppressWarnings("unchecked")
    public List<IUser> loadAllUserAndMessages() {
        Session session = null;
        Transaction transaction = null;
        try {
            // Get session
            //
            session = HibernateContext.getSessionFactory().getCurrentSession();
            transaction = session.beginTransaction();

            // Create query
            //
            Query query = session.createQuery("from User user left join fetch user.messageList");

            // Execute query
            //
            List<IUser> list = query.list();
            transaction.commit();

            return list;
        } catch (RuntimeException e) {
            // Rollback
            //
            transaction.rollback();
            throw e;
        }
    }

    /**
     * Count all the users
     */
    @Override
    public int countAll() {
        Session session = null;
        Transaction transaction = null;
        try {
            // Get session
            //
            session = HibernateContext.getSessionFactory().getCurrentSession();
            transaction = session.beginTransaction();

            // Create query
            //
            Query query = session.createQuery("select count(*) from User user");

            // Execute query
            //
            int result = ((Long) query.uniqueResult()).intValue();
            transaction.commit();

            return result;
        } catch (RuntimeException e) {
            // Rollback
            //
            transaction.rollback();
            throw e;
        }
    }

    /**
     * Save the argument user
     * 
     * @param user the user to save or create
     */
    @Override
    public void saveUser(IUser user) {
        Session session = null;
        Transaction transaction = null;
        try {
            // Get session
            //
            session = HibernateContext.getSessionFactory().getCurrentSession();
            transaction = session.beginTransaction();

            // Save user
            //
            session.saveOrUpdate(user);
            transaction.commit();
        } catch (RuntimeException e) {
            // Rollback
            //
            transaction.rollback();
            throw e;
        }
    }
}