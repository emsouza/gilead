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
package net.sf.beanlib.provider.replicator;

import net.sf.beanlib.BeanlibException;
import net.sf.beanlib.provider.BeanTransformer;
import net.sf.beanlib.spi.BeanTransformerSpi;
import net.sf.beanlib.spi.ChainedCustomBeanTransformer;
import net.sf.beanlib.spi.CustomBeanTransformerSpi;
import net.sf.beanlib.spi.replicator.BeanReplicatorSpi;

/**
 * Default implementation of {@link BeanReplicatorSpi}.
 * <p>
 * A BeanReplicator can be used to replicate JavaBean's.
 * <h2>Quick Start</h2>
 * To replicate a simple JavaBean with a class definition like:
 * <blockquote><pre>
 * public class SimpleBean {
 *     private String name;
 *
 *  public SimpleBean() {}
 *    public SimpleBean(String name) { this.name = name; }
 *    
 *    public String getName() { return name; }
 *    public void setName(String name) { this.name = name; }
 * }
 * ...
 * SimpleBean from = new SimpleBean("foo");
 * SimpleBean to = new BeanReplicator().replicateBean(from);
 * </pre></blockquote> 
 * Notes a no-arg constructor is required for the JavaBean at the top level.
 * <p>
 * How about a more complex example ? Let's try:
 * <pre><blockquote>public class ComplexBean {
 *     private String name;
 *     private ComplexBean[] array;
 *     private Collection&lt;ComplexBean> collection;
 *     private Map&lt;String,ComplexBean> map;
 * 
 *     public ComplexBean() {}
 *     public ComplexBean(String name) { this.name = name; }
 * 
 *     public String getName() { return name; }
 *     public void setName(String name) { this.name = name; }
 * 
 *     public Collection&lt;ComplexBean> getCollection() { return collection; }
 *     public void setCollection(Collection&lt;ComplexBean> collection) { this.collection = collection; }
 * 
 *     public ComplexBean[] getArray() { return array; }
 *     public void setArray(ComplexBean[] array) { this.array = array; }
 * 
 *     public Map&lt;String, ComplexBean> getMap() { return map; }
 *     public void setMap(Map&lt;String, ComplexBean> map) { this.map = map; }
 * }
 * </blockquote></pre>First, set up the bean
 * <blockquote><pre>
 * ComplexBean from = new ComplexBean("foo");
 * ComplexBean[] a = { from };
 * Collection&lt;ComplexBean> col = Arrays.asList(a);
 * from.setArray(a);
 * from.setCollection(col);
 * Map&lt;String,ComplexBean> map = new HashMap&lt;String,ComplexBean>();
 * map.put(from.getName(), from);
 * from.setMap(map);
 * </pre></blockquote>
 * And then replicate it in the same way:
 * <pre><blockquote>ComplexBean to = new BeanReplicator().replicateBean(from);
 * <blockquote></pre>
 * Voila!  The "to" and "from" beans are different object instances, 
 * but the content (ie the entire object graph) has been replicated.
 * Note this works as long as these objects follow the JavaBean convention.
 * 
 * See <a 
 * href="http://beanlib.svn.sourceforge.net/viewvc/beanlib/trunk/beanlib-test/src/net/sf/beanlib/provider/replicator/BeanReplicatorTest.java?revision=283&view=markup"
 * >BeanReplicatorTest.java</a> for more details.
 * 
 * @author Joe D. Velopar
 */
public class BeanReplicator extends ReplicatorTemplate implements BeanReplicatorSpi
{
    /**
     * Constructs with a given bean transformer.
     */
    public BeanReplicator(BeanTransformerSpi beanTransformer) 
    {
        super(beanTransformer);
    }

    /** 
     * Convenient constructor that both defaults to use {@link BeanTransformer},
     * and allows plugging in one or more custom bean transformer factories 
     * that will be chained together.
     * 
     * @see ChainedCustomBeanTransformer
     */
    public BeanReplicator(CustomBeanTransformerSpi.Factory ... customTransformerFactory) 
    {
        super(new BeanTransformer(customTransformerFactory));
    }
    
    /**
     * Constructs with the default {@link BeanTransformer}.
     */
    public BeanReplicator() {
        super(new BeanTransformer());
    }
    
    /**
     * Replicates a given JavaBean object.
     * 
     * @param <V> from type
     * @param from from bean to be replicated.
     */
    @SuppressWarnings("unchecked")
    public <V> V replicateBean(V from) {
        return replicateBean(from, (Class<V>)from.getClass());
    }
    
    /**
     * Replicates the properties of a JavaBean object to an instance of a target class,
     * which is selected from the given "from" and "to" classes, giving
     * priority to the one which is more specific whenever possible.
     * 
     * @param <V> from type
     * @param <T> target type
     * @param from the original bean to be replicated
     * @param toClass target class to be instantiated
     */
    public <V,T> T replicateBean(V from, Class<T> toClass) {
        return this.replicateBean(from, toClass, from);
    }
    
    /**
     * Replicates the properties of a JavaBean object to an instance of a target class,
     * which is selected from the given "from" and "to" classes, giving
     * priority to the one which is more specific whenever possible.
     * 
     * @param <V> from type
     * @param <T> target type
     * @param from from bean (after unenhancement) to be replicated
     * @param toClass target class to be instantiated
     * @param originalFrom the original from bean before any "unenhancement"
     * @return an instance of the replicated bean
     */
    protected <V,T> T replicateBean(V from, Class<T> toClass, V originalFrom)
    {
        Class<?> fromClass = from.getClass();
        String fromClassName = fromClass.getName();
        
        if (fromClassName.startsWith("net.sf.cglib.")) {
            // Want to skip the cglib stuff.
            return null;
        }
        if (fromClassName.startsWith("java.")) {
            if (!toClass.isAssignableFrom(fromClass))
                return null;
            // https://sourceforge.net/tracker/?func=detail&aid=3453166&group_id=140152&atid=745598
            if (fromClass == Class.class) { // "from" is a class per se
                @SuppressWarnings("unchecked") T t =(T)from;
                return t;
            }
            // Sorry, don't really know what it is ... soldier on...
        }
        T to;
        try {
            to = createToInstance(from, toClass);
        } catch (SecurityException e) {
            throw new BeanlibException("BeanReplicator.replicateBean failed:" 
                + " toClass=" + toClass + ", from=" + from
                +", originalFrom=" + originalFrom, e);
        } catch (InstantiationException e) {
            throw new BeanlibException("BeanReplicator.replicateBean failed:" 
                + " toClass=" + toClass + ", from=" + from
                +", originalFrom=" + originalFrom, e);
        } catch (IllegalAccessException e) {
            throw new BeanlibException("BeanReplicator.replicateBean failed:" 
                + " toClass=" + toClass + ", from=" + from
                +", originalFrom=" + originalFrom, e);
        } catch (NoSuchMethodException e) {
            throw new BeanlibException("BeanReplicator.replicateBean failed:" 
                + " toClass=" + toClass + ", from=" + from
                +", originalFrom=" + originalFrom, e);
        }
        putTargetCloned(originalFrom, to);
        // recursively populate member objects.
        populateBean(from, to);
        return to;
    }
    
    /**
     * Populates the properties of a "from" JavaBean object 
     * to a target "to" JavaBean object.
     * 
     * @param from the bean from which the properties are to be retrieved
     * @param to the target bean to be populated
     */
    public <V,T> T populate(V from, T to)
    {
        putTargetCloned(from, to);
        // recursively populate member objects.
        populateBean(from, to);
        return to;
    }
}
