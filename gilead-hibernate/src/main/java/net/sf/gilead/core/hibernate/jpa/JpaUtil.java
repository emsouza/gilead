package net.sf.gilead.core.hibernate.jpa;

import javax.persistence.EntityManagerFactory;

import org.hibernate.Session;
import org.hibernate.SessionFactory;

import net.sf.gilead.core.hibernate.HibernateUtil;

/**
 * @author bruno.marchesson
 */
public class JpaUtil extends HibernateUtil {

    public JpaUtil(SessionFactory sessionFactory) {
        super(sessionFactory, null);
    }

    public JpaUtil(EntityManagerFactory entityManagerFactory) {
        super();
        setEntityManagerFactory(entityManagerFactory);
    }

    public JpaUtil(SessionFactory sessionFactory, Session session) {
        super(sessionFactory, session);
    }

    /**
     * Sets the JPA entity manager factory
     *
     * @param entityManagerFactory
     */
    public void setEntityManagerFactory(EntityManagerFactory entityManagerFactory) {
        // Call parent setSessionFactory
        setSessionFactory((SessionFactory) entityManagerFactory);
    }
}
