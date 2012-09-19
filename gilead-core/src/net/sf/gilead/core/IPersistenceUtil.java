package net.sf.gilead.core;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * Interface for all Facade over (JPA) persistence engines. It allows the library to work with Hibernate and (soon)
 * OpenJPA and EclipseLink
 * 
 * @author bruno.marchesson
 */
public interface IPersistenceUtil {
	// -------------------------------------------------------------------------
	//
	// Session Management
	//
	// -------------------------------------------------------------------------
	/**
	 * Open a new session
	 * 
	 * @return the opened session
	 */
	public abstract void openSession();

	/**
	 * Close the session opened with 'openSession' call
	 * 
	 * @return the opened session
	 */
	public abstract void closeCurrentSession();

	/**
	 * Load a fresh instance of the persistent Pojo
	 * 
	 * @param clonePojo the clone pojo (needed for ID)
	 * @param persistentClass the persistent class
	 * @return the loaded instance
	 */
	public abstract Object load(Serializable id, Class<?> persistentClass);

	/**
	 * Flush pending modifications if needed
	 */
	public void flushIfNeeded();

	// -------------------------------------------------------------------------
	//
	// ID management
	//
	// -------------------------------------------------------------------------
	/**
	 * @return the ID of the argument Hibernate Pojo
	 */
	public abstract Serializable getId(Object pojo);

	/**
	 * @return the ID of the argument DTO with the same name than the persistent class
	 */
	public abstract Serializable getId(Object pojo, Class<?> persistentClass);

	// -------------------------------------------------------------------------
	//
	// Persistence metadata management
	//
	// -------------------------------------------------------------------------
	/**
	 * Indicates if the pojo is persistent or not. Its class must be declared as persistent and the pojo must have an ID
	 */
	public abstract boolean isPersistentPojo(Object pojo);

	/**
	 * Indicates if the class is managed by the persistance container of not
	 */
	public abstract boolean isPersistentClass(Class<?> clazz);

	/**
	 * Get the persistent class without proxy
	 * 
	 * @return the underlying persistent class
	 */
	public abstract Class<?> getUnenhancedClass(Class<?> clazz);

	/**
	 * Indicated if the argument class is enhanced or not
	 * 
	 * @param clazz the persistent class
	 * @return true is the class is enhanced, false otherwise
	 */
	public abstract boolean isEnhanced(Class<?> clazz);

	// -------------------------------------------------------------------------
	//
	// Proxy management
	//
	// -------------------------------------------------------------------------
	/**
	 * Indicates if the argument collection is persistent or not
	 * 
	 * @param collectionClass the collection class
	 * @return
	 */
	public abstract boolean isPersistentCollection(Class<?> collectionClass);

	/**
	 * Returns the underlying (real) collection for the persistent collection
	 * 
	 * @param persistentCollection the persistent collection
	 * @return
	 */
	public Collection<?> getUnderlyingCollection(Collection<?> persistentCollection);

	/**
	 * Indicates if the argument map is persistent or not
	 * 
	 * @param collectionClass the collection class
	 * @return
	 */
	public abstract boolean isPersistentMap(Class<?> collectionClass);

	/**
	 * Indicates it the argument is initialized not
	 * 
	 * @param proxy the instance to check
	 * @return
	 */
	public abstract boolean isInitialized(Object proxy);

	/**
	 * Initialize the argument if not
	 * 
	 * @param proxy the instance to initialize
	 * @return
	 */
	public abstract void initialize(Object proxy);

	/**
	 * Serialize proxy informations of the argument entity into a map of properties
	 * 
	 * @param proxy the proxy to serialize
	 * @return a map of properties
	 */
	public abstract Map<String, Serializable> serializeEntityProxy(Object proxy);

	/**
	 * Create an uninitialized proxy from the proxy informations
	 * 
	 * @param proxyInformations the serialized proxy informations
	 * @return the generated proxy
	 */
	public abstract Object createEntityProxy(Map<String, Serializable> proxyInformations);

	/**
	 * Serialize a persistent collection
	 * 
	 * @param persistentCollection the persistent collection
	 * @return a Map with mininmum informations needed to re-create the persistent info
	 */
	public abstract Map<String, Serializable> serializePersistentCollection(Collection<?> persistentCollection);

	/**
	 * Create a persistent collection from serialized informations
	 * 
	 * @param parent the parent bean of the collection
	 * @param serialized form of the persistent collection informations
	 * @param underlyingCollection the filled underlying collection
	 * @return the created persistent collection
	 */
	public abstract Collection<?> createPersistentCollection(Object parent, Map<String, Serializable> proxyInformations,
			Collection<?> underlyingCollection);

	/**
	 * Serialize a persistent map
	 * 
	 * @param persistentMap the persistent map
	 * @return a Map with mininmum informations needed to re-create the persistent info
	 */
	public abstract Map<String, Serializable> serializePersistentMap(Map<?, ?> persistentMap);

	/**
	 * Create a persistent map from serialized informations
	 * 
	 * @param parent the parent bean of the collection
	 * @param serialized form of the persistent collection informations
	 * @param underlyingMap the filled underlying map
	 * @return the created persistent collection
	 */
	public abstract Map<?, ?> createPersistentMap(Object parent, Map<String, Serializable> proxyInformations, Map<?, ?> underlyingMap);

	// -------------------------------------------------------------------------
	//
	// Loading management
	//
	// -------------------------------------------------------------------------
	/**
	 * Load an association from the parent object
	 * 
	 * @param parentClass class of the parent entity
	 * @param parentId id of the parent entity
	 * @param propertyName the name of the property to load
	 */
	public abstract Object loadAssociation(Class<?> parentClass, Serializable parentId, String propertyName);

	/**
	 * Executes an EJBQL query.
	 * 
	 * @param query the EJBQL query
	 * @param parameters parameters list (can be null)
	 * @return the query result list
	 */
	public abstract List<Object> executeQuery(String query, List<Object> parameters);

	/**
	 * Executes an EJBQL query.
	 * 
	 * @param query the EJBQL query
	 * @param parameters parameters map (can be null)
	 * @return the query result list
	 */
	public abstract List<Object> executeQuery(String query, Map<String, Object> parameters);
}