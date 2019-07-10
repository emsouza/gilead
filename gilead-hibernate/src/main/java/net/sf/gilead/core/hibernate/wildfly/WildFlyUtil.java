package net.sf.gilead.core.hibernate.wildfly;

import javax.persistence.EntityManagerFactory;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.jboss.as.jpa.container.TransactionScopedEntityManager;

import net.sf.gilead.core.hibernate.HibernateUtil;

public class WildFlyUtil extends HibernateUtil {

    public WildFlyUtil(SessionFactory sessionFactory) {
        super(sessionFactory, null);
    }

    public WildFlyUtil(EntityManagerFactory entityManagerFactory) {
        super();
        setEntityManagerFactory(entityManagerFactory);
    }

    public WildFlyUtil(SessionFactory sessionFactory, Session session) {
        super(sessionFactory, session);
    }

    public WildFlyUtil(EntityManagerFactory entityManagerFactory, Session session) {
        super(session);
        setEntityManagerFactory(entityManagerFactory);
    }

    /**
     * Set entity manager factory from JBoss
     *
     * @param entityManagerFactory
     */
    public void setEntityManagerFactory(Object entityManagerFactory) {
        // Manage InjectedEntityManagerFactory
        if (entityManagerFactory instanceof TransactionScopedEntityManager) {
            // Need to call 'getDelegate' method
            entityManagerFactory = ((TransactionScopedEntityManager) entityManagerFactory).getDelegate();
        }
        if (entityManagerFactory instanceof SessionFactory) {
            // Base class
            setSessionFactory(((SessionFactory) entityManagerFactory));
        } else {
            throw new IllegalArgumentException("Cannot find Hibernate entity manager factory implementation for " + entityManagerFactory);
        }
    }
}
