/**
 *
 */
package net.sf.gilead.core.hibernate.jpa;

import javax.persistence.EntityManagerFactory;

import net.sf.gilead.core.hibernate.HibernateUtil;
import net.sf.gilead.util.IntrospectionHelper;

import org.hibernate.jpa.HibernateEntityManagerFactory;

/**
 * @author bruno.marchesson
 */
public class HibernateJpaUtil extends HibernateUtil {
    /**
     * Sets the JPA entity manager factory
     * 
     * @param entityManagerFactory
     */
    public void setEntityManagerFactory(Object entityManagerFactory) {
        if (entityManagerFactory instanceof HibernateEntityManagerFactory == false) {
            // Probably an injected session factory
            //
            entityManagerFactory = IntrospectionHelper.searchMember(HibernateEntityManagerFactory.class, entityManagerFactory);
            if (entityManagerFactory == null) {
                throw new IllegalArgumentException("Cannot find Hibernate entity manager factory implementation !");
            }
        }

        // Call parent setSessionFactory
        //
        setSessionFactory(((HibernateEntityManagerFactory) entityManagerFactory).getSessionFactory());
    }

    // -------------------------------------------------------------------------
    //
    // Constructors
    //
    // -------------------------------------------------------------------------
    /**
     * Empty constructor.
     */
    public HibernateJpaUtil() {
        super();
    }

    /**
     * Complete constructor.
     */
    public HibernateJpaUtil(EntityManagerFactory entityManagerFactory) {
        super();
        setEntityManagerFactory(entityManagerFactory);
    }

}
