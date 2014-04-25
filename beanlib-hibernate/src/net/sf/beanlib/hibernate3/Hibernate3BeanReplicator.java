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
package net.sf.beanlib.hibernate3;

import java.util.Set;

import net.jcip.annotations.NotThreadSafe;
import net.sf.beanlib.CollectionPropertyName;
import net.sf.beanlib.hibernate.HibernateBeanReplicator;
import net.sf.beanlib.hibernate.HibernatePropertyFilter;
import net.sf.beanlib.spi.BeanPopulatorBaseSpi;
import net.sf.beanlib.spi.CustomBeanTransformerSpi;
import net.sf.beanlib.spi.PropertyFilter;

/**
 * Hibernate 3 Bean Replicator.
 * <p> 
 * This class can be used to conveniently replicate Hibernate (v3.x) objects 
 * that follow the JavaBean getter/setter convention on a best attempt basis.
 *   
 * The replication is typically recursive in that 
 * the whole object graph of the input object is replicated into an equivalent output object graph, 
 * resolving circular references, and eagerly fetching proxied instances as necessary.
 * 
 * However, the exact behavior of the replication process including<ul>
 * <li>to what extent the input object graph should be traversed and/or replicated; and </li>
 * <li>whether proxied instances should be eagerly fetched or not</li>
 * </ul> 
 * can be controlled and/or supplemented by the client code via various options:
 * <p>
 * <ol>
 * <li>All the configurable options of {@link BeanPopulatorBaseSpi} are available, as
 * the replication of JavaBean properties inevitably involves bean population.</li>
 * <p>
 * <li>Specifying an application package prefix: 
 * property with a type of an entity bean class with package name that matchs the prefix will be replicated;</li>
 * <p>
 * <li>Specifying a set of entity bean classes: property with a type of an entity bean class 
 * that is part of the set will be replicated;</li>
 * <p>
 * <li>Specifying a set of collection and map properties that will be replicated;</li>
 * <p>
 * <li>Specifying a {@link net.sf.beanlib.spi.PropertyFilter vetoer} used to veto the propagation of a property</li>
 * <p>
 * <li>For anything else that the existing implementation fails to transform, client can provide
 * one or multiple custom transformer factories via  
 * {@link #initCustomTransformerFactory(net.sf.beanlib.spi.CustomBeanTransformerSpi.Factory...)}.
 * </li>
 * </ol>
 * 
 * @see CustomBeanTransformerSpi
 * @see HibernateBeanReplicator
 * 
 * @author Joe D. Velopar
 */
@NotThreadSafe
public class Hibernate3BeanReplicator extends HibernateBeanReplicator
{
    /**
     * By default, all properties will be included for replication.
     */
    public Hibernate3BeanReplicator() {
    	super(new Hibernate3BeanTransformer()
    	        .initPropertyFilter(new HibernatePropertyFilter()));
    }

    /**
     * Constructs with an application package prefix.
     * 
     * @param applicationPackagePrefix
     * An application package prefix used to determine if a property 
     * with a type of an entity bean class will be included for replication.
     */
    public Hibernate3BeanReplicator(String applicationPackagePrefix) {
        super(new Hibernate3BeanTransformer()
                .initPropertyFilter(new HibernatePropertyFilter(applicationPackagePrefix)));
    }
	
    /**
     * Convenient constructor to specify:
     * <ol>
     * <li>The set of entity bean classes for matching properties that will be replicated;</li>
     * <li>The set of collection and map properties that will be replicated;</li>
     * <li>A {@link PropertyFilter vetoer} used to veto the propagation of specific properties</li>
     * </ol>
     * <p>
     * Note this constructor is relevant only if the default property filter {@link HibernatePropertyFilter} is used.
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
     * @param vetoer used to veto the propagation of specific properties.
	 */
    public Hibernate3BeanReplicator(
            Set<Class<?>> entityBeanClassSet, 
            Set<? extends CollectionPropertyName<?>> collectionPropertyNameSet, PropertyFilter vetoer) 
    {
        super(new Hibernate3BeanTransformer()
              .initPropertyFilter(
                    new HibernatePropertyFilter()
                    .withEntityBeanClassSet(entityBeanClassSet)
                    .withCollectionPropertyNameSet(collectionPropertyNameSet)
                    .withVetoer(vetoer)));
    }
    
    /**
     * Convenient constructor to specify:
     * <ol>
     * <li>An application package prefix used to determine if a property 
     * with a type of an entity bean class will be included for replication;</li>
     * <li>The set of entity bean classes for matching properties that will be replicated;</li>
     * <li>The set of collection and map properties that will be replicated;</li>
     * <li>A {@link PropertyFilter vetoer} used to veto the propagation of specific properties</li>
     * </ol>
     * <p>
     * Note this constructor is relevant only if the default property filter {@link HibernatePropertyFilter} is used.
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
     * @param vetoer used to veto the propagation of specific properties.
     */
    public Hibernate3BeanReplicator(
            String applicationPackagePrefix,
            Set<Class<?>> entityBeanClassSet, 
            Set<? extends CollectionPropertyName<?>> collectionPropertyNameSet, PropertyFilter vetoer) 
    {
        super(new Hibernate3BeanTransformer()
              .initPropertyFilter(
                    new HibernatePropertyFilter(applicationPackagePrefix)
                    .withEntityBeanClassSet(entityBeanClassSet)
                    .withCollectionPropertyNameSet(collectionPropertyNameSet)
                    .withVetoer(vetoer)));
    }
}
