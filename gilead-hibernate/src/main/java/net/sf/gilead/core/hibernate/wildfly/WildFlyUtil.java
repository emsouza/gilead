package net.sf.gilead.core.hibernate.wildfly;

import javax.persistence.EntityManagerFactory;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.engine.spi.SessionFactoryImplementor;

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
    protected void setEntityManagerFactory(EntityManagerFactory entityManagerFactory) {
        if (entityManagerFactory instanceof SessionFactoryImplementor) {
            // Base class
            setSessionFactory((SessionFactoryImplementor) entityManagerFactory);
        } else {
            throw new IllegalArgumentException("Cannot find Hibernate entity manager factory implementation for " + entityManagerFactory);
        }
    }
}
