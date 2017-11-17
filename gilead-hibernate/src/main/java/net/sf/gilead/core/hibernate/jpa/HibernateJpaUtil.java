package net.sf.gilead.core.hibernate.jpa;

import javax.persistence.EntityManagerFactory;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.jpa.HibernateEntityManagerFactory;

import net.sf.gilead.core.hibernate.HibernateUtil;
import net.sf.gilead.util.IntrospectionHelper;

/**
 * @author bruno.marchesson
 */
public class HibernateJpaUtil extends HibernateUtil {

    public HibernateJpaUtil(SessionFactory sessionFactory) {
        super(sessionFactory, null);
    }

    public HibernateJpaUtil(EntityManagerFactory entityManagerFactory) {
        super();
        setEntityManagerFactory(entityManagerFactory);
    }

    public HibernateJpaUtil(SessionFactory sessionFactory, Session session) {
        super(sessionFactory, session);
    }

    public HibernateJpaUtil(EntityManagerFactory entityManagerFactory, Session session) {
        super(session);
        setEntityManagerFactory(entityManagerFactory);
    }

    /**
     * Sets the JPA entity manager factory
     *
     * @param entityManagerFactory
     */
    public void setEntityManagerFactory(Object entityManagerFactory) {

        if (entityManagerFactory instanceof HibernateEntityManagerFactory == false) {
            // Probably an injected session factory
            entityManagerFactory = IntrospectionHelper.searchMember(HibernateEntityManagerFactory.class, entityManagerFactory);
            if (entityManagerFactory == null) {
                throw new IllegalArgumentException("Cannot find Hibernate entity manager factory implementation !");
            }
        }

        // Call parent setSessionFactory
        setSessionFactory(((HibernateEntityManagerFactory) entityManagerFactory).getSessionFactory());
    }
}
