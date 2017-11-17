package net.sf.gilead.test.dao.hibernate;

import java.util.List;

import org.hibernate.LockMode;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;

import net.sf.gilead.test.HibernateContext;
import net.sf.gilead.test.dao.IMessageDAO;
import net.sf.gilead.test.domain.interfaces.IMessage;

/**
 * DAO for message beans. This implementation use HQL to work seamlessly with all implementation of the Message domain
 * class (Java 1.4 _ stateful or stateless _ and Java5)
 *
 * @author bruno.marchesson
 */
public class MessageDAO implements IMessageDAO {

    @Override
    public IMessage loadLastMessage() {
        Session session = null;
        Transaction transaction = null;
        try {
            // Get session
            session = HibernateContext.getSessionFactory().getCurrentSession();
            transaction = session.beginTransaction();

            // Create query
            Query query = session.createQuery("from Message order by date desc");
            query.setMaxResults(1);

            // Execute query
            IMessage message = (IMessage) query.uniqueResult();
            transaction.commit();
            return message;
        } catch (RuntimeException e) {
            // Rollback
            transaction.rollback();
            throw e;
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<IMessage> loadAllMessage() {
        Session session = null;
        Transaction transaction = null;
        try {
            // Get session
            session = HibernateContext.getSessionFactory().getCurrentSession();
            transaction = session.beginTransaction();

            // Create query
            Query query = session.createQuery("from Message order by date desc");

            // Execute query
            List<IMessage> result = query.list();
            transaction.commit();

            return result;
        } catch (RuntimeException e) {
            // Rollback
            transaction.rollback();
            throw e;
        }
    }

    @Override
    public IMessage loadDetailedMessage(Integer id) {
        Session session = null;
        Transaction transaction = null;
        try {
            // Get session
            session = HibernateContext.getSessionFactory().getCurrentSession();
            transaction = session.beginTransaction();

            // Create query
            StringBuffer hqlQuery = new StringBuffer();
            hqlQuery.append("from Message message");
            hqlQuery.append(" inner join fetch message.author");
            hqlQuery.append(" where message.id = :id");

            // Fill query
            Query query = session.createQuery(hqlQuery.toString());
            query.setInteger("id", id);

            // Execute query
            IMessage message = (IMessage) query.uniqueResult();

            transaction.commit();
            return message;
        } catch (RuntimeException e) {
            // Rollback
            transaction.rollback();
            throw e;
        }
    }

    @Override
    public void saveMessage(IMessage message) {
        Session session = null;
        Transaction transaction = null;
        try {
            // Get session
            session = HibernateContext.getSessionFactory().getCurrentSession();
            transaction = session.beginTransaction();

            // Save message
            session.saveOrUpdate(message);
            transaction.commit();
        } catch (RuntimeException e) {
            // Rollback
            transaction.rollback();
            throw e;
        }
    }

    @Override
    public void deleteMessage(IMessage message) {
        Session session = null;
        Transaction transaction = null;
        try {
            // Get session
            session = HibernateContext.getSessionFactory().getCurrentSession();
            transaction = session.beginTransaction();

            // Delete message
            session.delete(message);
            transaction.commit();
        } catch (RuntimeException e) {
            // Rollback
            transaction.rollback();
            throw e;
        }
    }

    @Override
    public void lockMessage(IMessage message) {
        Session session = null;
        Transaction transaction = null;
        try {
            // Get session
            session = HibernateContext.getSessionFactory().getCurrentSession();
            transaction = session.beginTransaction();

            // Lock message
            if (message.getId() > 0) {
                session.lock(message, LockMode.UPGRADE);
            }
            transaction.commit();
        } catch (RuntimeException e) {
            // Rollback
            transaction.rollback();
            throw e;
        }
    }

    @Override
    public int countAllMessages() {
        Session session = null;
        Transaction transaction = null;
        try {
            // Get session
            session = HibernateContext.getSessionFactory().getCurrentSession();
            transaction = session.beginTransaction();

            // Create query
            Query query = session.createQuery("select count(*) from Message");

            // Execute query
            int result = ((Long) query.uniqueResult()).intValue();
            transaction.commit();

            return result;
        } catch (RuntimeException e) {
            // Rollback
            transaction.rollback();
            throw e;
        }
    }
}
