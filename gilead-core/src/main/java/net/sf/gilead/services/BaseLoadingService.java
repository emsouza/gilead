
package net.sf.gilead.services;

import java.io.Serializable;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.sf.gilead.core.PersistenceUtil;
import net.sf.gilead.core.PersistentBeanManager;
import net.sf.gilead.pojo.base.ILightEntity;
import net.sf.gilead.util.IntrospectionHelper;

/**
 * Remote loading service
 *
 * @author bruno.marchesson
 */
public class BaseLoadingService<T extends ILightEntity> {

    private static final Logger LOGGER = LoggerFactory.getLogger(BaseLoadingService.class);

    /**
     * The associated bean manager. Default value is defined by the unique instance of the singleton.
     */
    private PersistentBeanManager beanManager = PersistentBeanManager.getInstance();

    /**
     * @return the beanManager
     */
    public PersistentBeanManager getBeanManager() {
        return beanManager;
    }

    /**
     * @param beanManager the beanManager to set
     */
    public void setBeanManager(PersistentBeanManager beanManager) {
        this.beanManager = beanManager;
    }

    /**
     * Load an association from the parent entity
     *
     * @param parent the entity
     * @param property the name of the property to load
     * @return the loaded entity
     */
    @SuppressWarnings("unchecked")
    public <K extends ILightEntity> K loadEntityAssociation(T parent, String propertyName) {
        return (K) loadAssociation(parent, propertyName);
    }

    @SuppressWarnings("unchecked")
    public <K extends ILightEntity> List<K> loadListAssociation(T parent, String propertyName) {
        return (List<K>) loadAssociation(parent, propertyName);
    }

    @SuppressWarnings("unchecked")
    public <K extends ILightEntity> Set<K> loadSetAssociation(T parent, String propertyName) {
        return (Set<K>) loadAssociation(parent, propertyName);
    }

    /**
     * Loads an entity
     */
    @SuppressWarnings("unchecked")
    public T loadEntity(String className, Serializable id) {
        // Precondition checking
        if (id == null) {
            throw new NullPointerException("Missing id!");
        }

        if (className == null) {
            throw new NullPointerException("Missing entity class!");
        }

        if (beanManager == null) {
            throw new NullPointerException("Bean manager not set !");
        }

        // Get Persistence util
        PersistenceUtil persistenceUtil = beanManager.getPersistenceUtil();
        if (persistenceUtil == null) {
            throw new NullPointerException("Persistence util not set on beanManager field !");
        }

        LOGGER.debug("Loading entity [{}] with ID [{}].", className, id);

        // Load entity and clone it
        try {
            Object entity = persistenceUtil.load(id, Class.forName(className));
            return (T) beanManager.clone(entity);
        } catch (ClassNotFoundException ex) {
            throw new RuntimeException("Class not found :" + className, ex);
        }

    }

    /**
     * Loads an association
     */
    protected Object loadAssociation(T parent, String propertyName) {
        // Precondition checking
        if (parent == null) {
            throw new NullPointerException("Null entity!");
        }
        if ((propertyName == null) || (propertyName.length() == 0)) {
            throw new NullPointerException("Null or empty property name!");
        }

        if (beanManager == null) {
            throw new NullPointerException("Bean manager not set !");
        }

        // Get Persistence util
        PersistenceUtil persistenceUtil = beanManager.getPersistenceUtil();
        if (persistenceUtil == null) {
            throw new NullPointerException("Persistence util not set on beanManager field !");
        }

        LOGGER.debug("Loading property [{}] for entity [{}].", propertyName, parent);

        // Get Id
        Serializable id = persistenceUtil.getId(parent);

        // Load entity and assocation
        Object entity = persistenceUtil.loadAssociation(parent.getClass(), id, propertyName);
        if (entity == null) {
            LOGGER.warn("Cannot load entity [{}] with ID [{}] with property [{}].", parent.getClass(), id, propertyName);
            return null;
        }

        // Get getter for the property
        Object association = null;
        try {
            Method reader = IntrospectionHelper.getReaderMethodForProperty(entity.getClass(), propertyName);
            association = reader.invoke(entity, (Object[]) null);
        } catch (Exception ex) {
            throw new RuntimeException("Error during lazy loading invocation !", ex);
        }

        return beanManager.clone(association);
    }
}
