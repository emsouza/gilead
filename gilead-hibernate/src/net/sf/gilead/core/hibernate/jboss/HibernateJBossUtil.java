package net.sf.gilead.core.hibernate.jboss;

import javax.persistence.EntityManagerFactory;

import net.sf.gilead.core.hibernate.HibernateUtil;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.jpa.HibernateEntityManagerFactory;
import org.jboss.as.jpa.container.TransactionScopedEntityManager;

public class HibernateJBossUtil extends HibernateUtil {
    /**
     * Set entity manager factory from JBoss
     *
     * @param entityManagerFactory
     */
    public void setEntityManagerFactory(Object entityManagerFactory) {
        // Manage InjectedEntityManagerFactory
        //
        if (entityManagerFactory instanceof TransactionScopedEntityManager) {
            // Need to call 'getDelegate' method
            //
            entityManagerFactory = ((TransactionScopedEntityManager) entityManagerFactory).getDelegate();
        }
        if (entityManagerFactory instanceof HibernateEntityManagerFactory) {
            // Base class
            //
            setSessionFactory(((HibernateEntityManagerFactory) entityManagerFactory).getSessionFactory());
        } else {
            throw new IllegalArgumentException("Cannot find Hibernate entity manager factory implementation for " + entityManagerFactory);
        }
    }

    // -------------------------------------------------------------------------
    //
    // Constructors
    //
    // -------------------------------------------------------------------------
    public HibernateJBossUtil(SessionFactory sessionFactory) {
        super(sessionFactory, null);
    }

    public HibernateJBossUtil(EntityManagerFactory entityManagerFactory) {
        super();
        setEntityManagerFactory(entityManagerFactory);
    }

    public HibernateJBossUtil(SessionFactory sessionFactory, Session session) {
        super(sessionFactory, session);
    }

    public HibernateJBossUtil(EntityManagerFactory entityManagerFactory, Session session) {
        super(session);
        setEntityManagerFactory(entityManagerFactory);
    }
}
