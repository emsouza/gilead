/*
 * Copyright 2007 The Apache Software Foundation.
 *
 * Licensed under the Apache License, Version 2.0 (the "License")
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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
import java.util.logging.Level;
import java.util.logging.Logger;

import net.sf.beanlib.utils.ClassUtils;
import net.sf.gilead.core.annotations.AnnotationsManager;
import net.sf.gilead.core.beanlib.IClassMapper;
import net.sf.gilead.core.store.IProxyStore;
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
    // ----
    // Attributes
    // ----
    /**
     * The unique instance of the Persistence Bean Manager
     */
    private static PersistentBeanManager _instance = null;

    /**
     * Logger channel
     */
    private Logger _log = Logger.getLogger(PersistentBeanManager.class.getSimpleName());

    /**
     * The associated Proxy informations store
     */
    protected IProxyStore _proxyStore;

    /**
     * The Class mapper
     */
    protected IClassMapper _classMapper;

    /**
     * The POJO lazy killer
     */
    protected LazyKiller _lazyKiller;

    /**
     * The associated persistence util implementation
     */
    protected IPersistenceUtil _persistenceUtil;

    // ----
    // Property
    // ----
    /**
     * @return the unique instance of the singleton
     */
    public synchronized static PersistentBeanManager getInstance() {
        if (_instance == null) {
            _instance = new PersistentBeanManager();
        }
        return _instance;
    }

    /**
     * @return the proxy store
     */
    public IProxyStore getProxyStore() {
        return _proxyStore;
    }

    /**
     * set the used pojo store
     */
    public void setProxyStore(IProxyStore proxyStore) {
        _log.info("Using Proxy Store : " + proxyStore);
        _proxyStore = proxyStore;
        _lazyKiller.setProxyStore(_proxyStore);
    }

    /**
     * @return the class mapper
     */
    public IClassMapper getClassMapper() {
        return _classMapper;
    }

    /**
     * @param mapper the class Mapper to set
     */
    public void setClassMapper(IClassMapper mapper) {
        _log.info("Using class mapper : " + mapper);
        _classMapper = mapper;

        _lazyKiller.setClassMapper(mapper);
    }

    /**
     * @return the _persistenceUtil
     */
    public IPersistenceUtil getPersistenceUtil() {
        return _persistenceUtil;
    }

    /**
     * @param util the _persistenceUtil to set
     */
    public void setPersistenceUtil(IPersistenceUtil util) {
        _log.info("Using persistence util : " + util);

        _persistenceUtil = util;
        _lazyKiller.setPersistenceUtil(util);
    }

    // -------------------------------------------------------------------------
    //
    // Constructor
    //
    // -------------------------------------------------------------------------
    /**
     * Empty Constructor
     */
    public PersistentBeanManager() {
        // Default parameters
        _proxyStore = new StatelessProxyStore();
        _lazyKiller = new LazyKiller();
        _lazyKiller.setProxyStore(_proxyStore);
    }

    // -------------------------------------------------------------------------
    //
    // Hibernate Java 1.4 POJO public interface
    //
    // -------------------------------------------------------------------------
    /**
     * Clone and store the Hibernate POJO(s)
     */
    public Object clone(Object object) {
        // Explicit clone : no assignable compatibility checking
        try {
            return clone(object, false);
        } finally {
            _proxyStore.cleanUp();
            _lazyKiller.reset();
        }
    }

    /**
     * Clone and store the Hibernate POJO
     * 
     * @param object the object to store
     * @param assignable if the assignation from source to target class (via ClassMapper) must be checked
     * @return the clone
     */
    public Object clone(Object object, boolean assignable) {

        // Precondition checking
        if (object == null) {
            return null;
        }
        if (_persistenceUtil == null) {
            throw new RuntimeException("No Persistence Util set !");
        }

        // Flush any pending modifications before clone
        _persistenceUtil.flushIfNeeded();

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
    @SuppressWarnings("unchecked")
    public Object merge(Object object, boolean assignable) {
        // Precondition checking
        //
        if (object == null) {
            return null;
        }

        if (_persistenceUtil == null) {
            throw new RuntimeException("No Persistence Util set !");
        }

        // Collection handling
        //
        if (object instanceof Collection) {
            return mergeCollection((Collection) object, assignable);
        } else if (object instanceof Map) {
            return mergeMap((Map) object, assignable);
        } else if (object.getClass().isArray()) {
            // Check primitive type
            //
            if (object.getClass().getComponentType().isPrimitive()) {
                return object;
            }

            // Merge as a collection
            //
            Object[] array = (Object[]) object;
            Collection result = mergeCollection(Arrays.asList(array), assignable);

            // Get the result as an array (much more tricky !!!)
            //
            Class<?> componentType = object.getClass().getComponentType();
            Object[] copy = (Object[]) java.lang.reflect.Array.newInstance(componentType, array.length);
            return result.toArray(copy);
        } else {
            return mergePojo(object, assignable);
        }
    }

    // -------------------------------------------------------------------------
    //
    // Hibernate Java 1.4 POJO internal methods
    //
    // -------------------------------------------------------------------------
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
            if (_persistenceUtil.isPersistentPojo(pojo) == true) {

                // Assignation test
                Class<?> hibernateClass = _persistenceUtil.getUnenhancedClass(pojo.getClass());
                targetClass = null;
                if (_classMapper != null) {
                    targetClass = _classMapper.getTargetClass(hibernateClass);
                }

                if (targetClass == null) {
                    targetClass = hibernateClass;
                }

                if ((assignable == true) && (hibernateClass.isAssignableFrom(targetClass) == false)) {
                    throw new NotAssignableException(hibernateClass, targetClass);
                }

                // Proxy checking
                if (_persistenceUtil.isInitialized(pojo) == false) {
                    // If the root pojo is not initialized, replace it by null
                    return null;
                }
            } else if (holdPersistentObject(pojo) == false) {

                // Do not clone not persistent classes, since they do not necessary implement Java Bean specification.
                _log.log(Level.FINE, "Not persistent instance, clone is not needed : " + pojo.toString());
                return pojo;
            }

            // Clone the pojo
            return _lazyKiller.detach(pojo, targetClass);
        } finally {
            _persistenceUtil.closeCurrentSession();
            _proxyStore.cleanUp();
        }
    }

    /**
     * Clone and store a map of Hibernate POJO
     */
    protected Map<?, ?> cloneMap(Map<?, ?> hibernatePojoMap, boolean assignable) {
        // Clone each element of the map
        Map<Object, Object> cloneMap = createNewMap(hibernatePojoMap);

        for (Map.Entry<?, ?> entry : hibernatePojoMap.entrySet()) {
            cloneMap.put(clone(entry.getKey(), assignable), clone(entry.getValue(), assignable));
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
            clonePojoList.add(clone(hibernatePojo, assignable));
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
        if (_classMapper != null) {
            hibernateClass = _classMapper.getSourceClass(cloneClass);
        }
        if (hibernateClass == null) {
            // Not a clone : take the inner class
            hibernateClass = clonePojo.getClass();
        }

        // Precondition checking : is the pojo managed by Hibernate
        if (_persistenceUtil.isPersistentClass(hibernateClass) == true) {
            // Assignation checking
            if ((assignable == true) && (hibernateClass.isAssignableFrom(cloneClass) == false)) {
                throw new NotAssignableException(hibernateClass, cloneClass);
            }
        }

        // Retrieve the pojo
        try {
            Serializable id = null;
            try {
                id = _persistenceUtil.getId(clonePojo, hibernateClass);
                if (id == null) {
                    _log.info("HibernatePOJO not found : can be transient or deleted data : " + clonePojo);
                }
            } catch (TransientObjectException ex) {
                _log.info("Transient object : " + clonePojo);
            } catch (NotPersistentObjectException ex) {
                if (holdPersistentObject(clonePojo) == false) {
                    // Do not merge not persistent instance, since they do not
                    // necessary
                    // implement the Java bean specification
                    //
                    _log.log(Level.FINE, "Not persistent object, merge is not needed : " + clonePojo);
                    return clonePojo;
                } else {
                    _log.log(Level.FINE, "Merging wrapper object : " + clonePojo);
                }
            }

            if (ClassUtils.immutable(hibernateClass)) {
                // Do not clone immutable types
                //
                return clonePojo;
            }

            // Create a new POJO instance
            //
            Object hibernatePojo = null;
            try {
                if (AnnotationsManager.hasGileadAnnotations(hibernateClass)) {
                    if (id != null) {
                        // ServerOnly or ReadOnly annotation : load from DB
                        // needed
                        //
                        hibernatePojo = _persistenceUtil.load(id, hibernateClass);
                    } else {
                        // Transient instance
                        //
                        hibernatePojo = clonePojo;
                    }
                } else {
                    Constructor<?> constructor = hibernateClass.getDeclaredConstructor(new Class<?>[] {});
                    constructor.setAccessible(true);
                    hibernatePojo = constructor.newInstance();
                }
            } catch (Exception e) {
                throw new RuntimeException("Cannot create a fresh new instance of the class " + hibernateClass, e);
            }

            // Merge the modification in the Hibernate Pojo
            //
            _lazyKiller.attach(hibernatePojo, clonePojo);
            return hibernatePojo;
        } finally {
            _persistenceUtil.closeCurrentSession();
            _proxyStore.cleanUp();
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
        //
        for (Object clonePojo : clonePojoList) {
            try {
                hibernatePojoList.add(merge(clonePojo, assignable));
            } catch (TransientObjectException e) {
                // Keep new pojo (probably created from GWT)
                //
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
        Map<Object, Object> hibernateMap = new HashMap<Object, Object>();

        // Iterate over map
        //
        for (Map.Entry<?, ?> entry : cloneMap.entrySet()) {
            // Merge key
            Object key = entry.getKey();
            try {
                key = merge(key, assignable);
            } catch (TransientObjectException ex) { /* keep key untouched */
            }

            // Merge value
            Object value = entry.getValue();
            try {
                value = merge(value, assignable);
            } catch (TransientObjectException ex) { /* keep value untouched */
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
    @SuppressWarnings("unchecked")
    protected Collection<Object> createNewCollection(Collection<?> pojoCollection) {
        Class<? extends Collection> collectionClass = pojoCollection.getClass();

        // Exclusion case : persistent collection, base collection or BlazeDS ones
        //
        if (_persistenceUtil.isPersistentCollection(collectionClass) || collectionClass.isAnonymousClass() || collectionClass.isMemberClass()
                || collectionClass.isLocalClass() || collectionClass.getName().equals("flex.messaging.io.ArrayCollection")) {
            // Create a basic collection
            //
            return createBasicCollection(pojoCollection);
        } else {
            // Create the same collection
            //
            Collection<Object> result = null;
            try {
                // First, search constructor with initial capacity argument
                //
                Constructor<?> constructor = collectionClass.getConstructor(Integer.TYPE);
                result = (Collection<Object>) constructor.newInstance(pojoCollection.size());
            } catch (NoSuchMethodException e) {
                // No such constructor, so search the empty one
                //
                try {
                    Constructor<?> constructor = collectionClass.getConstructor((Class[]) null);
                    result = (Collection<Object>) constructor.newInstance();
                } catch (NoSuchMethodException ex) {
                    // No empty or simple constructor : fallback on basic
                    // collection
                    //
                    _log.log(Level.WARNING, "Unable to find basic constructor for " + collectionClass.getName()
                            + " : falling back to basic collection");
                    return createBasicCollection(pojoCollection);
                } catch (Exception ex) {
                    throw new RuntimeException("Cannot instantiate collection !", ex);
                }
            } catch (Exception ex) {
                throw new RuntimeException("Cannot instantiate collection !", ex);
            }

            if (collectionClass.getPackage().getName().startsWith("java") == false) {
                // Extend collections (such as PagingList)
                //
                _lazyKiller.populate(result, pojoCollection);
            }

            return result;
        }
    }

    /**
     * Creation of basic collection
     * 
     * @param pojoCollection
     * @return
     */
    protected Collection<Object> createBasicCollection(Collection<?> pojoCollection) {
        if (pojoCollection instanceof List) {
            try {
                return new ArrayList<Object>(pojoCollection.size());
            } catch (Exception ex) {
                // No access to size ? lazy initialization exception ?
                //
                _log.log(Level.FINE, "Error creating array list with size " + ex);
                return new ArrayList<Object>();
            }
        } else if (pojoCollection instanceof Set) {
            if (pojoCollection instanceof SortedSet) {
                return new TreeSet<Object>();
            } else {
                try {
                    return new HashSet<Object>(pojoCollection.size());
                } catch (Exception ex) {
                    // No access to size ? lazy initialization exception ?
                    //
                    _log.log(Level.FINE, "Error creating hash set with size " + ex);
                    return new HashSet<Object>();
                }
            }
        } else {
            throw new CloneException("Unhandled collection type : " + pojoCollection.getClass().toString());
        }
    }

    /**
     * Create a new map with the same behavior than the argument one
     * 
     * @param pojoMap the source map
     * @return a newly created, empty map
     */
    @SuppressWarnings("unchecked")
    protected Map<Object, Object> createNewMap(Map<?, ?> pojoMap) {
        Class<? extends Map> mapClass = pojoMap.getClass();

        if (_persistenceUtil.isPersistentCollection(mapClass) || mapClass.isAnonymousClass() || mapClass.isMemberClass() || mapClass.isLocalClass()) {
            return new HashMap<Object, Object>();
        } else {
            // Create the same map
            //
            try {
                Constructor<?> constructor = mapClass.getConstructor((Class[]) null);
                return (Map<Object, Object>) constructor.newInstance();
            } catch (Exception ex) {
                throw new RuntimeException("Cannot instantiate collection !", ex);
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
        return holdPersistentObject(pojo, new ArrayList<Object>());
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
            //
            if ((pojo == null) || (alreadyChecked.contains(pojo))) {
                return false;
            }

            alreadyChecked.add(pojo);
            Class<?> pojoClass = pojo.getClass();
            if (_classMapper != null) {
                Class<?> sourceClass = _classMapper.getSourceClass(pojoClass);
                if (sourceClass != null) {
                    pojoClass = sourceClass;
                }
            }

            if ((_persistenceUtil.isEnhanced(pojoClass) == true) || (_persistenceUtil.isPersistentClass(pojoClass) == true)
                    || (_persistenceUtil.isPersistentCollection(pojoClass) == true)) {
                return true;
            }

            if (pojo instanceof Collection) {
                @SuppressWarnings("unchecked")
                Collection<Object> pojoCollection = (Collection) pojo;
                for (Object item : pojoCollection) {
                    if (holdPersistentObject(item, alreadyChecked)) {
                        return true;
                    }
                }

                return false;
            }

            // Iterate over properties
            //
            BeanInfo info = Introspector.getBeanInfo(pojo.getClass());
            PropertyDescriptor[] descriptors = info.getPropertyDescriptors();
            for (PropertyDescriptor descriptor : descriptors) {
                Class<?> propertyClass = descriptor.getPropertyType();
                if (propertyClass == null) {
                    // Indexed property
                    //
                    propertyClass = ((IndexedPropertyDescriptor) descriptor).getPropertyType();
                } else if (propertyClass.isArray()) {
                    propertyClass = propertyClass.getComponentType();
                }
                if (propertyClass == null) {
                    // Can do nothing with this...
                    //
                    continue;
                }

                // Check needed for collection or property declared as bare
                // Object
                boolean isCollection = Collection.class.isAssignableFrom(propertyClass) || Map.class.isAssignableFrom(propertyClass);
                boolean isObject = propertyClass.equals(Object.class);

                if ((ClassUtils.immutable(propertyClass) == true)
                        || ((ClassUtils.isJavaPackage(propertyClass) == true) && (isCollection == false) && (isObject == false))) {
                    // Basic type : no check needed
                    //
                    continue;
                }

                // Not a basic type, so a check is needed
                //
                // collection and recursive search handling
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

                if ((_classMapper != null) && (_classMapper.getSourceClass(propertyClass) != null)) {
                    propertyClass = _classMapper.getSourceClass(propertyClass);
                }

                if ((_persistenceUtil.isPersistentClass(propertyClass) == true) || (_persistenceUtil.isPersistentCollection(propertyClass) == true)) {
                    return true;
                }

                // Check property value
                //
                if (propertyValue instanceof Collection<?>) {
                    // Check collection values
                    //
                    Collection<?> propertyCollection = (Collection<?>) propertyValue;
                    for (Object value : propertyCollection) {
                        if (holdPersistentObject(value, alreadyChecked) == true) {
                            return true;
                        }
                    }
                } else if (propertyValue instanceof Map<?, ?>) {
                    // Check map entry and values
                    //
                    Map<?, ?> propertyMap = (Map<?, ?>) propertyValue;
                    for (Map.Entry<?, ?> value : propertyMap.entrySet()) {
                        if ((holdPersistentObject(value.getKey(), alreadyChecked) == true)
                                || (holdPersistentObject(value.getValue(), alreadyChecked) == true)) {
                            return true;
                        }
                    }
                } else if (propertyClass.isArray()) {
                    // Check array elements
                    //
                    Object[] propertyValues = (Object[]) propertyValue;
                    for (Object propertyValue2 : propertyValues) {
                        if (holdPersistentObject(propertyValue2) == true) {
                            return true;
                        }
                    }
                } else {
                    // Recursive search
                    //
                    if (holdPersistentObject(propertyValue, alreadyChecked) == true) {
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
