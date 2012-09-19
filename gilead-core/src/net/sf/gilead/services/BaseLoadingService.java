/**
 * 
 */
package net.sf.gilead.services;

import java.io.Serializable;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Set;

import net.sf.gilead.core.IPersistenceUtil;
import net.sf.gilead.core.PersistentBeanManager;
import net.sf.gilead.core.annotations.AnnotationsManager;
import net.sf.gilead.pojo.base.ILightEntity;
import net.sf.gilead.util.IntrospectionHelper;

import org.apache.log4j.Logger;

/**
 * Remote loading service
 * 
 * @author bruno.marchesson
 */
public class BaseLoadingService<T extends ILightEntity> {
	// ----
	// Attributes
	// ----
	/**
	 * Serialization ID
	 */
	private static final long serialVersionUID = 5714428833885668669L;

	/**
	 * Logger channel
	 */
	private static Logger _log = Logger.getLogger(BaseLoadingService.class);

	/**
	 * The associated bean manager. Default value is defined by the unique instance of the singleton.
	 */
	private PersistentBeanManager beanManager = PersistentBeanManager.getInstance();

	// ----
	// Properties
	// ---
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

	// -----------------------------------------------------------------------
	//
	// Constructors
	//
	// -----------------------------------------------------------------------
	/**
	 * Constructor
	 */
	public BaseLoadingService() {}

	// -------------------------------------------------------------------------
	//
	// Association loading implementation
	//
	// -------------------------------------------------------------------------
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

	/*
	 * (non-Javadoc)
	 * @see net.sf.gilead.gwt.client.LoadingService#loadAssociationList(net.sf.gilead .pojo.base.ILightEntity,
	 * java.lang.String)
	 */
	@SuppressWarnings("unchecked")
	public <K extends ILightEntity> List<K> loadListAssociation(T parent, String propertyName) {
		return (List<K>) loadAssociation(parent, propertyName);
	}

	/*
	 * (non-Javadoc)
	 * @see net.sf.gilead.gwt.client.LoadingService#loadSetAssociation(net.sf.gilead .pojo.base.ILightEntity,
	 * java.lang.String)
	 */
	@SuppressWarnings("unchecked")
	public <K extends ILightEntity> Set<K> loadSetAssociation(T parent, String propertyName) {
		return (Set<K>) loadAssociation(parent, propertyName);
	}

	// -------------------------------------------------------------------------
	//
	// Entity loading methods
	//
	// -------------------------------------------------------------------------
	/**
	 * Loads an entity
	 */
	@SuppressWarnings("unchecked")
	public T loadEntity(String className, Serializable id) {
		// Precondition checking
		//
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
		//
		IPersistenceUtil persistenceUtil = beanManager.getPersistenceUtil();
		if (persistenceUtil == null) {
			throw new NullPointerException("Persistence util not set on beanManager field !");
		}

		if (_log.isDebugEnabled()) {
			_log.debug("Loading entity " + className + " with ID" + id);
		}

		// Load entity and clone it
		//
		try {
			Object entity = persistenceUtil.load(id, Class.forName(className));
			return (T) beanManager.clone(entity);
		} catch (ClassNotFoundException ex) {
			throw new RuntimeException("Class not found :" + className, ex);
		}

	}

	// -------------------------------------------------------------------------
	//
	// Internal methods
	//
	// -------------------------------------------------------------------------
	/**
	 * Loads an association
	 */
	protected Object loadAssociation(T parent, String propertyName) {
		// Precondition checking
		//
		if (parent == null) {
			throw new NullPointerException("Null entity !");
		}
		if ((propertyName == null) || (propertyName.length() == 0)) {
			throw new NullPointerException("Null or empty property name!");
		}

		if (beanManager == null) {
			throw new NullPointerException("Bean manager not set !");
		}

		if (AnnotationsManager.isServerOnly(parent, propertyName)) {
			_log.warn("Cannot load @ServerOnly property " + propertyName);
			return null;
		}

		// Get Persistence util
		//
		IPersistenceUtil persistenceUtil = beanManager.getPersistenceUtil();
		if (persistenceUtil == null) {
			throw new NullPointerException("Persistence util not set on beanManager field !");
		}

		if (_log.isDebugEnabled()) {
			_log.debug("Loading property " + propertyName + " for entity " + parent);
		}

		// Get Id
		//
		Serializable id = persistenceUtil.getId(parent);

		// Load entity and assocation
		//
		Object entity = persistenceUtil.loadAssociation(parent.getClass(), id, propertyName);
		if (entity == null) {
			_log.warn("Cannot load entity " + parent.getClass() + "[" + id + "] with property " + propertyName);
			return null;
		}

		// Get getter for the property
		//
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
