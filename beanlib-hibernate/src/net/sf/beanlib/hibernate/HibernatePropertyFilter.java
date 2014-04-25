/*
 * Copyright 2005 The Apache Software Foundation.
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
package net.sf.beanlib.hibernate;

import static net.sf.beanlib.utils.ClassUtils.fqcn;
import static net.sf.beanlib.utils.ClassUtils.immutable;
import static net.sf.beanlib.utils.ClassUtils.isJavaPackage;

import java.lang.reflect.Method;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Set;

import net.sf.beanlib.CollectionPropertyName;
import net.sf.beanlib.spi.PropertyFilter;

/**
 * A default implementation used to determine if a Hibernate property 
 * that follows the JavaBean getter/setter convention should be propagated.
 * Each propagation decision can be controlled by specifying
 * <ul>
 * <li>An application package prefix used to determine if a property 
 * with a type of an entity bean class will be included for replication;</li>
 * <li>The set of entity bean classes for matching properties that will be replicated;</li>
 * <li>The set of collection and map properties that will be replicated;</li>
 * <li>A {@link net.sf.beanlib.spi.PropertyFilter vetoer} used to veto the propagation of a property</li>
 * </ul>
 *
 * @author Joe D. Velopar
 */
public class HibernatePropertyFilter implements PropertyFilter 
{
    /**
     * The set of entity bean classes for matching properties that will be replicated, 
     * eagerly fetching if necessary.
     * Null means all whereas empty means none.
     */
    private Set<Class<?>> entityBeanClassSet;

    /**
     * The set of collection and map properties that will be replicated, 
     * eagerly fetching if necessary.
     * Null means all whereas empty means none.
     */
    private Set<? extends CollectionPropertyName<?>> collectionPropertyNameSet;
    
    /** Used to veto the propagation of a property. */
    private PropertyFilter vetoer;

    /** 
     * An entity bean under a package with a name 
     * that matches this prefix will be included for replication,
     * eagerly fetched if necessary.  
     * Otherwise, the semantics of {@link #entityBeanClassSet} applies. 
     */ 
    private final String applicationPackagePrefix; 
    
    /**
     * Constructs with the specified options of controlling what to be replicated and what not.
     * 
     * @param entityBeanClassSet
     * The set of entity bean classes for matching properties that will be replicated, 
     * eagerly fetching if necessary.
     * Null means all whereas empty means none.
     * 
     * @param collectionPropertyNameSet
     * The set of collection and map properties that will be replicated, 
     * eagerly fetching if necessary.
     * Null means all whereas empty means none.
     * 
     * @param vetoer used to veto the propagation of a JavaBean property.
     */
    public HibernatePropertyFilter(Set<Class<?>> entityBeanClassSet, 
        Set<? extends CollectionPropertyName<?>> collectionPropertyNameSet, PropertyFilter vetoer)
    {
        this.entityBeanClassSet = entityBeanClassSet;
        this.collectionPropertyNameSet = collectionPropertyNameSet;
        this.vetoer = vetoer;
        this.applicationPackagePrefix = "#";    // disable by matching no packages 
    }

    /**
     * Constructs with the specified options of controlling what to be replicated and what not.
     * 
     * @param applicationPackagePrefix
     * An entity bean under a package with a name 
     * that matches this prefix will be included for replication,
     * eagerly fetched if necessary.  
     * Otherwise, the semantics of {@link #entityBeanClassSet} applies.
     *  
     * @param entityBeanClassSet
     * The set of entity bean classes for matching properties that will be replicated, 
     * eagerly fetching if necessary.
     * Null means all whereas empty means none.
     * 
     * @param collectionPropertyNameSet
     * The set of collection and map properties that will be replicated, 
     * eagerly fetching if necessary.
     * Null means all whereas empty means none.
     * 
     * @param vetoer used to veto the propagation of a JavaBean property.
     */
    public HibernatePropertyFilter(String applicationPackagePrefix, Set<Class<?>> entityBeanClassSet, 
        Set<? extends CollectionPropertyName<?>> collectionPropertyNameSet, PropertyFilter vetoer)
    {
        this.entityBeanClassSet = entityBeanClassSet;
        this.collectionPropertyNameSet = collectionPropertyNameSet;
        this.vetoer = vetoer;
        this.applicationPackagePrefix = applicationPackagePrefix;
    }
    
    /**
     * Constructs with the default behavior of replicating all properties recursively.
     */
    public HibernatePropertyFilter() {
        this.applicationPackagePrefix = "#";    // disable by matching no packages 
    }
    
    /**
     * Constructs with an application package prefix.
     * 
     * @param applicationPackagePrefix
     * An entity bean under a package with a name 
     * that matches this prefix will be included for replication,
     * eagerly fetched if necessary.  
     * Otherwise, the semantics of {@link #entityBeanClassSet} applies.
     */
    public HibernatePropertyFilter(String applicationPackagePrefix) {
        this.applicationPackagePrefix = applicationPackagePrefix;
        this.entityBeanClassSet = Collections.emptySet();
    }
    
    /**
     * Returns the configured set of entity bean classes for matching properties that will be replicated, 
     * eagerly fetching if necessary;
     * null if all entity bean classes are to be replicated; 
     * or empty if no entity bean class is to be replicated.
     */
    public Set<Class<?>> getEntityBeanClassSet() {
        return entityBeanClassSet;
    }

    /**
     * Used to configure the set of entity bean classes for matching properties that will be replicated, 
     * eagerly fetching if necessary.
     * 
     * @param entityBeanClassSet the set of entity bean classes for matching properties that will be replicated, 
     * eagerly fetching if necessary.
     * null if all entity bean classes are to be replicated; 
     * or empty if no entity bean class is to be replicated.
     * 
     * @return the current instance for method chaining purposes.
     */
    public HibernatePropertyFilter withEntityBeanClassSet(Set<Class<?>> entityBeanClassSet) {
        this.entityBeanClassSet = entityBeanClassSet;
        return this;
    }

    /**
     * Returns the configured set of collection and map properties that are to be replicated, 
     * eagerly fetching if necessary;
     * null if all collection and map properties are to be replicated; 
     * or empty if no collection nor map properties are to be replicated.
     */
    @SuppressWarnings("unchecked")
    public Set<CollectionPropertyName<?>> getCollectionPropertyNameSet() {
        return (Set<CollectionPropertyName<?>>)collectionPropertyNameSet;
    }

    /**
     * Used to configure the set of collection and map properties that will be replicated, eagerly fetching if necessary.
     * 
     * @param collectionPropertyNameSet set of collection and map properties that will be replicated, 
     * eagerly fetching if necessary;
     * null if all collection and map properties are to be replicated;
     * or empty if no collection nor map properties are to be replicated.
     * 
     * @return the current instance for method chaining purposes.
     */
    public HibernatePropertyFilter withCollectionPropertyNameSet(
            Set<? extends CollectionPropertyName<?>> collectionPropertyNameSet) 
    {
        this.collectionPropertyNameSet = collectionPropertyNameSet;
        return this;
    }

    /**
     * Returns the vetoer configured for vetoing the propagation of a property.
     */
    public PropertyFilter getVetoer() {
        return vetoer;
    }

    /**
     * Used to configure a vetoer for vetoing the propagation of a property.
     * 
     * @return the current instance for method chaining purposes.
     */
    public HibernatePropertyFilter withVetoer(PropertyFilter vetoer) {
        this.vetoer = vetoer;
        return this;
    }
    
    public boolean propagate(String propertyName, Method readerMethod) 
    {
        if (propagateImpl(propertyName, readerMethod))
            return vetoer == null ? true : vetoer.propagate(propertyName, readerMethod);
        return false;
    }
    
    public boolean propagateImpl(String propertyName, Method readerMethod) 
    {
        Class<?> returnType = UnEnhancer.unenhanceClass(readerMethod.getReturnType());
        
        if (immutable(returnType))
            return true;
        
        if (returnType.isArray()) {
            if (immutable(returnType.getComponentType()))
                return true;
        }
        
        if (entityBeanClassSet == null) {
            // All entity bean to be populated.
            if (collectionPropertyNameSet == null) {
                // all fields to be populated
                return true;
            }
            return checkCollectionProperty(propertyName, readerMethod);
        }
        // Only a selected set of entity bean to be populated.
        if (isJavaPackage(returnType)) {
            // Not an entity bean.
            if (collectionPropertyNameSet == null) {
                // All Collection/Map properties to be populated.
                return true;
            }
            return checkCollectionProperty(propertyName, readerMethod);
        }
        // An entity bean.
        Class<?> superClass = returnType;
        
        for (;;) {
            if (entityBeanClassSet.contains(superClass) 
            || isApplicationClass(superClass))
                return true;
            // check if it's ancestor is specified in entityBeanClassSet
            superClass = superClass.getSuperclass();
            
            if (superClass == null || superClass == Object.class)
                return false;        // not specified in entityBeanClassSet
        }
    }
    
    private boolean checkCollectionProperty(String propertyName, Method readerMethod)
    {
        // Only a specified set of Collection/Map properties needs to be populated
        Class<?> returnType = UnEnhancer.unenhanceClass(readerMethod.getReturnType());
        
        if (Collection.class.isAssignableFrom(returnType) 
        ||  Map.class.isAssignableFrom(returnType)) 
        {
            @SuppressWarnings("unchecked")
            CollectionPropertyName<?> colPropName = 
                new CollectionPropertyName(
                        UnEnhancer.unenhanceClass(readerMethod.getDeclaringClass()), propertyName); 

            // A Collection/Map property
            return collectionPropertyNameSet.contains(colPropName);
        }
        // Not a Collection/Map property.
        return true;
    }
    
    /** Returns true iff c is an application class. */
    public boolean isApplicationClass(Class<?> c) {
        return c != null && fqcn(c).startsWith(applicationPackagePrefix);
    }
}
