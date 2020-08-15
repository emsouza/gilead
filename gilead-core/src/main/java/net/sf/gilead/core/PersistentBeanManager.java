package net.sf.gilead.core;

import java.beans.BeanInfo;
import java.beans.IndexedPropertyDescriptor;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.io.Serializable;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.sf.beanlib.utils.ClassUtils;
import net.sf.gilead.core.beanlib.ClassMapper;
import net.sf.gilead.core.store.ProxyStore;
import net.sf.gilead.core.store.stateless.StatelessProxyStore;
import net.sf.gilead.exception.CloneException;
import net.sf.gilead.exception.InvocationException;
import net.sf.gilead.exception.NotAssignableException;
import net.sf.gilead.exception.NotPersistentObjectException;
import net.sf.gilead.exception.TransientObjectException;
import net.sf.gilead.util.CollectionHelper;

/**
 * Manager for Persistent POJO handling
 *
 * @author bruno.marchesson
 */
public class PersistentBeanManager {

    private static final Logger LOGGER = LoggerFactory.getLogger(PersistentBeanManager.class);

    /**
     * The unique instance of the Persistence Bean Manager
     */
    private static PersistentBeanManager instance = null;

    /**
     * The associated Proxy informations store
     */
    protected ProxyStore proxyStore;

    /**
     * The Class mapper
     */
    protected ClassMapper classMapper;

    /**
     * The POJO lazy killer
     */
    protected LazyKiller lazyKiller;

    /**
     * The associated persistence util implementation
     */
    protected PersistenceUtil persistenceUtil;

    /**
     * @return the unique instance of the singleton
     */
    public synchronized static PersistentBeanManager getInstance() {
        if (instance == null) {
            instance = new PersistentBeanManager();
        }
        return instance;
    }

    /**
     * Empty Constructor
     */
    private PersistentBeanManager() {
        proxyStore = new StatelessProxyStore();
        lazyKiller = new LazyKiller();
        lazyKiller.setProxyStore(proxyStore);
    }

    /**
     * @return the proxy store
     */
    public ProxyStore getProxyStore() {
        return proxyStore;
    }

    /**
     * set the used pojo store
     */
    public void setProxyStore(ProxyStore proxyStore) {
        LOGGER.debug("Using Proxy Store : " + proxyStore);
        this.proxyStore = proxyStore;
        lazyKiller.setProxyStore(proxyStore);
    }

    /**
     * @return the class mapper
     */
    public ClassMapper getClassMapper() {
        return classMapper;
    }

    /**
     * @param mapper the class Mapper to set
     */
    public void setClassMapper(ClassMapper classMapper) {
        LOGGER.debug("Using class mapper : " + classMapper);
        this.classMapper = classMapper;
        lazyKiller.setClassMapper(classMapper);
    }

    /**
     * @return the _persistenceUtil
     */
    public PersistenceUtil getPersistenceUtil() {
        return persistenceUtil;
    }

    /**
     * @param util the _persistenceUtil to set
     */
    public void setPersistenceUtil(PersistenceUtil persistenceUtil) {
        LOGGER.debug("Using persistence util : " + persistenceUtil);
        this.persistenceUtil = persistenceUtil;
        lazyKiller.setPersistenceUtil(persistenceUtil);
    }

    /**
     * Clone and store the Hibernate POJO(s)
     */
    public Object clone(Object object) {
        // Explicit clone : no assignable compatibility checking
        return clone(object, false);
    }

    public Object clone(Object object, boolean assignable) {
        try {
            return cloneInternal(object, assignable);
        } finally {
            proxyStore.cleanUp();
            lazyKiller.reset();
        }
    }

    /**
     * Clone and store the Hibernate POJO
     *
     * @param object the object to store
     * @param assignable if the assignation from source to target class (via ClassMapper) must be checked
     * @return the clone
     */
    protected Object cloneInternal(Object object, boolean assignable) {
        // Precondition checking
        if (object == null) {
            return null;
        }
        if (persistenceUtil == null) {
            throw new RuntimeException("No Persistence Util set !");
        }

        // Flush any pending modifications before clone
        persistenceUtil.flushIfNeeded();

        // Collection handling
        if (object instanceof Collection) {
            return cloneCollection((Collection<?>) object, assignable);
        } else if (object instanceof Map) {
            return cloneMap((Map<?, ?>) object, assignable);
        } else if (object.getClass().isArray()) {
            // Clone as a collection
            Object[] array = (Object[]) object;
            Collection<?> result = cloneCollection(Arrays.asList(array), assignable);

            // Get the result as an array (much more tricky !!!)
            Class<?> componentType = object.getClass().getComponentType();
            Object[] copy = (Object[]) java.lang.reflect.Array.newInstance(componentType, array.length);
            return result.toArray(copy);
        } else {
            return clonePojo(object, assignable);
        }
    }

    /**
     * Merge the clone POJO to its Hibernate counterpart
     */
    public Object merge(Object object) {
        // Explicit merge
        return merge(object, false);
    }

    /**
     * Merge the clone POJO to its Hibernate counterpart
     */
    @SuppressWarnings({ "unchecked", "rawtypes" })
    public Object merge(Object object, boolean assignable) {
        // Precondition checking
        if (object == null) {
            return null;
        }

        if (persistenceUtil == null) {
            throw new RuntimeException("No Persistence Util set !");
        }

        // Collection handling
        if (object instanceof Collection) {
            return mergeCollection((Collection) object, assignable);
        } else if (object instanceof Map) {
            return mergeMap((Map) object, assignable);
        } else if (object.getClass().isArray()) {
            // Check primitive type
            if (object.getClass().getComponentType().isPrimitive()) {
                return object;
            }

            // Merge as a collection
            Object[] array = (Object[]) object;
            Collection result = mergeCollection(Arrays.asList(array), assignable);

            // Get the result as an array (much more tricky !!!)
            Class<?> componentType = object.getClass().getComponentType();
            Object[] copy = (Object[]) java.lang.reflect.Array.newInstance(componentType, array.length);
            return result.toArray(copy);
        } else {
            return mergePojo(object, assignable);
        }
    }

    /**
     * Clone and store the Hibernate POJO
     *
     * @param pojo the pojo to store
     * @param assignable does the source and target class must be assignable?
     * @exception NotAssignableException if source and target class are not assignable
     */
    protected Object clonePojo(Object pojo, boolean assignable) {

        // Null checking
        if (pojo == null) {
            return null;
        }

        // Precondition checking : is the pojo managed by Hibernate
        try {
            Class<?> targetClass = pojo.getClass();
            if (persistenceUtil.isPersistentPojo(pojo) == true) {

                // Assignation test
                Class<?> hibernateClass = persistenceUtil.getUnenhancedClass(pojo.getClass());
                targetClass = null;
                if (classMapper != null) {
                    targetClass = classMapper.getTargetClass(hibernateClass);
                }

                if (targetClass == null) {
                    targetClass = hibernateClass;
                }

                if (assignable && !hibernateClass.isAssignableFrom(targetClass)) {
                    throw new NotAssignableException(hibernateClass, targetClass);
                }

                // Proxy checking
                if (!persistenceUtil.isInitialized(pojo)) {
                    // If the root pojo is not initialized, replace it by null
                    return null;
                }
            } else if (!holdPersistentObject(pojo)) {

                // Do not clone not persistent classes, since they do not necessary implement Java Bean
                // specification.
                LOGGER.debug("Not persistent instance, clone is not needed for [{}].", pojo.toString());
                return pojo;
            }

            // Clone the pojo
            return lazyKiller.detach(pojo, targetClass);
        } finally {
            persistenceUtil.closeCurrentSession();
            proxyStore.cleanUp();
        }
    }

    /**
     * Clone and store a map of Hibernate POJO
     */
    protected Map<?, ?> cloneMap(Map<?, ?> hibernatePojoMap, boolean assignable) {
        // Clone each element of the map
        Map<Object, Object> cloneMap = createNewMap(hibernatePojoMap);

        for (Map.Entry<?, ?> entry : hibernatePojoMap.entrySet()) {
            cloneMap.put(cloneInternal(entry.getKey(), assignable), cloneInternal(entry.getValue(), assignable));
        }

        return cloneMap;
    }

    /**
     * Clone and store a collection of Hibernate POJO
     */
    protected Collection<?> cloneCollection(Collection<?> hibernatePojoList, boolean assignable) {
        // Clone each element of the collection
        Collection<Object> clonePojoList = createNewCollection(hibernatePojoList);
        for (Object hibernatePojo : hibernatePojoList) {
            clonePojoList.add(cloneInternal(hibernatePojo, assignable));
        }

        return clonePojoList;
    }

    /**
     * Retrieve the Hibernate Pojo and merge the modification from GWT
     *
     * @param clonePojo the clone pojo
     * @param assignable does the source and target class must be assignable
     * @return the merged Hibernate POJO
     * @exception UnsupportedOperationException if the clone POJO does not implements ILightEntity and the POJO store is
     *                stateless
     * @exception NotAssignableException if source and target class are not assignable
     */
    protected Object mergePojo(Object clonePojo, boolean assignable) {
        // Get Hibernate associated class
        Class<?> cloneClass = clonePojo.getClass();
        Class<?> hibernateClass = null;
        if (classMapper != null) {
            hibernateClass = classMapper.getSourceClass(cloneClass);
        }
        if (hibernateClass == null) {
            // Not a clone : take the inner class
            hibernateClass = clonePojo.getClass();
        }

        // Precondition checking : is the pojo managed by Hibernate
        if (persistenceUtil.isPersistentClass(hibernateClass)) {
            // Assignation checking
            if (assignable && !hibernateClass.isAssignableFrom(cloneClass)) {
                throw new NotAssignableException(hibernateClass, cloneClass);
            }
        }

        // Retrieve the pojo
        try {
            Serializable id = null;
            try {
                id = persistenceUtil.getId(clonePojo, hibernateClass);
                if (id == null) {
                    LOGGER.debug("Hibernate POJO not found... Can be transient or deleted data [{}].", clonePojo.getClass().getName());
                }
            } catch (TransientObjectException ex) {
                LOGGER.error("Transient object [{}].", clonePojo);
            } catch (NotPersistentObjectException ex) {
                if (holdPersistentObject(clonePojo) == false) {
                    // Not a persistent instance, since they do not necessary implement the Java bean
                    // specification
                    LOGGER.debug("Not a persistent object, merge is not needed [{}].", clonePojo.getClass().getName());
                    return clonePojo;
                } else {
                    LOGGER.debug("Merging wrapper object [{}].", clonePojo);
                }
            }

            if (ClassUtils.immutable(hibernateClass)) {
                // Do not clone immutable types
                return clonePojo;
            }

            // Create a new POJO instance
            Object hibernatePojo = null;
            try {

                LOGGER.debug("Create new object to merge cloned instance [{}].", hibernateClass.getName());
                Constructor<?> constructor = hibernateClass.getDeclaredConstructor();
                constructor.setAccessible(true);
                hibernatePojo = constructor.newInstance();

            } catch (Exception e) {
                throw new RuntimeException("Cannot create a fresh new instance of the class " + hibernateClass, e);
            }

            // Merge the modification in the Hibernate Pojo
            lazyKiller.attach(hibernatePojo, clonePojo);

            return hibernatePojo;

        } finally {
            persistenceUtil.closeCurrentSession();
            proxyStore.cleanUp();
        }
    }

    /**
     * Retrieve the Hibernate Pojo List and merge the modification from GWT
     *
     * @param clonePojoList the clone pojo list
     * @return a list of merged Hibernate POJO
     * @exception UnsupportedOperationException if a POJO from the list does not implements ILightEntity and the POJO
     *                store is stateless
     */
    protected Collection<?> mergeCollection(Collection<?> clonePojoList, boolean assignable) {
        Collection<Object> hibernatePojoList = createNewCollection(clonePojoList);

        // Retrieve every hibernate from pojo list
        for (Object clonePojo : clonePojoList) {
            try {
                hibernatePojoList.add(merge(clonePojo, assignable));
            } catch (TransientObjectException e) {
                // Keep new pojo (probably created from GWT)
                hibernatePojoList.add(clonePojo);
            }
        }
        return hibernatePojoList;
    }

    /**
     * Fill copy map with Hibernate merged POJO
     *
     * @param cloneMap
     * @return a map with merge Hibernate POJO
     */
    protected Map<?, ?> mergeMap(Map<?, ?> cloneMap, boolean assignable) {
        Map<Object, Object> hibernateMap = new HashMap<>();

        // Iterate over map
        for (Map.Entry<?, ?> entry : cloneMap.entrySet()) {
            // Merge key
            Object key = entry.getKey();
            try {
                key = merge(key, assignable);
            } catch (TransientObjectException ex) {
                /* keep key untouched */
            }

            // Merge value
            Object value = entry.getValue();
            try {
                value = merge(value, assignable);
            } catch (TransientObjectException ex) {
                /* keep value untouched */
            }

            hibernateMap.put(key, value);
        }

        return hibernateMap;
    }

    /**
     * Create a new collection with the same behavior than the argument one
     *
     * @param pojoCollection the source collection
     * @return a newly created, empty collection
     */
    @SuppressWarnings({ "unchecked", "rawtypes" })
    protected Collection<Object> createNewCollection(Collection<?> pojoCollection) {
        Class<? extends Collection> collectionClass = pojoCollection.getClass();

        // Exclusion case : persistent collection, base collection.
        if (persistenceUtil.isPersistentCollection(collectionClass) || collectionClass.isAnonymousClass() || collectionClass.isMemberClass()
                || collectionClass.isLocalClass()) {
            // Create a basic collection
            return (Collection<Object>) createBasicCollection(pojoCollection);
        } else {
            // Create the same collection
            Collection<Object> result = null;
            try {
                // First, search constructor with initial capacity argument
                Constructor<?> constructor = collectionClass.getConstructor(Integer.TYPE);
                result = (Collection<Object>) constructor.newInstance(pojoCollection.size());
            } catch (NoSuchMethodException e) {
                // No such constructor, so search the empty one
                try {
                    Constructor<?> constructor = collectionClass.getConstructor((Class[]) null);
                    result = (Collection<Object>) constructor.newInstance();
                } catch (NoSuchMethodException ex) {
                    // No empty or simple constructor : fallback on basic collection
                    LOGGER.warn("Unable to find basic constructor for [{}]. Falling back to basic collection.", collectionClass.getName());
                    return (Collection<Object>) createBasicCollection(pojoCollection);
                } catch (Exception ex) {
                    throw new RuntimeException("Cannot instantiate collection !", ex);
                }
            } catch (Exception ex) {
                throw new RuntimeException("Cannot instantiate collection !", ex);
            }

            if (!collectionClass.getPackage().getName().startsWith("java")) {
                // Extend collections (such as PagingList)
                lazyKiller.populate(result, pojoCollection);
            }
            return result;
        }
    }

    /**
     * Creation of basic collection
     *
     * @param collection
     * @return
     */
    protected Collection<?> createBasicCollection(Collection<?> collection) {
        if (collection instanceof List) {
            return new ArrayList<>(collection.size());
        } else if (collection instanceof SortedSet) {
            return new TreeSet<>();
        } else if (collection instanceof Set) {
            return new HashSet<>(collection.size());
        } else {
            throw new CloneException(String.format("Unhandled collection type [%s].", collection.getClass().getName()));
        }
    }

    /**
     * Create a new map with the same behavior than the argument one
     *
     * @param pojoMap the source map
     * @return a newly created, empty map
     */
    @SuppressWarnings({ "unchecked", "rawtypes" })
    protected Map<Object, Object> createNewMap(Map<?, ?> pojoMap) {
        Class<? extends Map> mapClass = pojoMap.getClass();

        if (persistenceUtil.isPersistentCollection(mapClass) || mapClass.isAnonymousClass() || mapClass.isMemberClass()
                || mapClass.isLocalClass()) {
            return new HashMap<>();
        } else {
            // Create the same map
            try {
                Constructor<?> constructor = mapClass.getConstructor();
                return (Map<Object, Object>) constructor.newInstance();
            } catch (Exception ex) {
                throw new RuntimeException("Cannot instantiate collection!", ex);
            }
        }
    }

    /**
     * In deep persistent association checking. This method is used to detect wrapping object (ie not persistent class
     * holding persistent associations)
     *
     * @param pojo the wrapping pojo
     * @return true if the pojo contains persistent member, false otherwise
     */
    protected boolean holdPersistentObject(Object pojo) {
        return holdPersistentObject(pojo, new ArrayList<>());
    }

    /**
     * In deep persistent association checking. This method is used to detect wrapping object (ie not persistent class
     * holding persistent associations)
     *
     * @param pojo the wrapping pojo
     * @param alreadyChecked list of already checked pojos
     * @return true if the pojo contains persistent member, false otherwise
     */
    protected boolean holdPersistentObject(Object pojo, List<Object> alreadyChecked) {
        try {
            // Precondition checking
            if (pojo == null || alreadyChecked.contains(pojo)) {
                return false;
            }

            alreadyChecked.add(pojo);
            Class<?> pojoClass = pojo.getClass();
            if (classMapper != null) {
                Class<?> sourceClass = classMapper.getSourceClass(pojoClass);
                if (sourceClass != null) {
                    pojoClass = sourceClass;
                }
            }

            if (persistenceUtil.isEnhanced(pojoClass) || persistenceUtil.isPersistentClass(pojoClass)
                    || persistenceUtil.isPersistentCollection(pojoClass)) {
                return true;
            }

            if (pojo instanceof Collection) {
                @SuppressWarnings({ "unchecked", "rawtypes" })
                Collection<Object> pojoCollection = (Collection) pojo;
                for (Object item : pojoCollection) {
                    if (holdPersistentObject(item, alreadyChecked)) {
                        return true;
                    }
                }

                return false;
            }

            // Iterate over properties
            BeanInfo info = Introspector.getBeanInfo(pojo.getClass());
            PropertyDescriptor[] descriptors = info.getPropertyDescriptors();
            for (PropertyDescriptor descriptor : descriptors) {
                Class<?> propertyClass = descriptor.getPropertyType();
                if (propertyClass == null) {
                    // Indexed property
                    propertyClass = ((IndexedPropertyDescriptor) descriptor).getPropertyType();
                } else if (propertyClass.isArray()) {
                    propertyClass = propertyClass.getComponentType();
                }
                if (propertyClass == null) {
                    // Can do nothing with this...
                    continue;
                }

                // Check needed for collection or property declared as bare Object
                boolean isCollection = Collection.class.isAssignableFrom(propertyClass) || Map.class.isAssignableFrom(propertyClass);
                boolean isObject = propertyClass.equals(Object.class);

                if (ClassUtils.immutable(propertyClass) || (ClassUtils.isJavaPackage(propertyClass) && !isCollection && !isObject)) {
                    // Basic type : no check needed
                    continue;
                }

                // Not a basic type, so a check is needed collection and recursive search handling
                Method readMethod = descriptor.getReadMethod();
                if (readMethod == null) {
                    continue;
                }
                readMethod.setAccessible(true);
                Object propertyValue = readMethod.invoke(pojo, (Object[]) null);

                if (propertyValue == null) {
                    continue;
                }

                // Unmodifiable collection handling
                if (CollectionHelper.isUnmodifiableCollection(propertyValue)) {
                    propertyValue = CollectionHelper.getUnmodifiableCollection(propertyValue);
                }

                // Get real property class
                propertyClass = propertyValue.getClass();

                if (classMapper != null && classMapper.getSourceClass(propertyClass) != null) {
                    propertyClass = classMapper.getSourceClass(propertyClass);
                }

                if (persistenceUtil.isPersistentClass(propertyClass) || persistenceUtil.isPersistentCollection(propertyClass)) {
                    return true;
                }

                // Check property value
                if (propertyValue instanceof Collection<?>) {
                    // Check collection values
                    Collection<?> propertyCollection = (Collection<?>) propertyValue;
                    for (Object value : propertyCollection) {
                        if (holdPersistentObject(value, alreadyChecked)) {
                            return true;
                        }
                    }
                } else if (propertyValue instanceof Map<?, ?>) {
                    // Check map entry and values
                    Map<?, ?> propertyMap = (Map<?, ?>) propertyValue;
                    for (Map.Entry<?, ?> value : propertyMap.entrySet()) {
                        if (holdPersistentObject(value.getKey(), alreadyChecked) || holdPersistentObject(value.getValue(), alreadyChecked)) {
                            return true;
                        }
                    }
                } else if (propertyClass.isArray()) {
                    // Check array elements
                    Object[] propertyValues = (Object[]) propertyValue;
                    for (Object propertyValue2 : propertyValues) {
                        if (holdPersistentObject(propertyValue2)) {
                            return true;
                        }
                    }
                } else {
                    // Recursive search
                    if (holdPersistentObject(propertyValue, alreadyChecked)) {
                        return true;
                    }
                }
            }

            // No persistent property
            return false;
        } catch (Exception e) {
            throw new InvocationException(e);
        }
    }
}
