/*
 * Copyright 2008 The Apache Software Foundation.
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

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.sameInstance;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertThat;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import junit.framework.JUnit4TestAdapter;

import org.junit.Test;

/**
 * @author Joe D. Velopar
 */
public class BeanReplicatorTest 
{
    public static class ComplexBean {
        private Class<?> clazz;

        private String name;
        private ComplexBean[] array;
        private Collection<ComplexBean> collection;
        private Map<String,ComplexBean> map;
    
        public ComplexBean() { }
        public ComplexBean(String name) { this.name = name; }
    
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
    
        public Collection<ComplexBean> getCollection() { return collection; }
        public void setCollection(Collection<ComplexBean> collection) { this.collection = collection; }
    
        public ComplexBean[] getArray() { return array; }
        public void setArray(ComplexBean[] array) { this.array = array; }
    
        public Map<String, ComplexBean> getMap() { return map; }
        public void setMap(Map<String, ComplexBean> map) { this.map = map; }

        public Class<?> getClazz() { return clazz; }
        public void setClazz(Class<?> clazz) { this.clazz = clazz; }
    }
    
    @Test
    public void test() {
        ComplexBean from = new ComplexBean("foo");
        ComplexBean[] a = { from };
        Collection<ComplexBean> col = Arrays.asList(a);
        from.setArray(a);
        from.setCollection(col);
        Map<String,ComplexBean> map = new HashMap<String,ComplexBean>();
        map.put(from.getName(), from);
        from.setMap(map);
        from.setClazz(List.class);
        
        ComplexBean to = new BeanReplicator().replicateBean(from);
        
        assertThat(from.getName(), is("foo"));
        assertThat(from.getArray()[0], sameInstance(from));
        assertThat(from.getCollection().iterator().next(), sameInstance(from));
        assertThat(from.getMap().get(from.getName()), sameInstance(from));

        assertThat(from, not(sameInstance(to)));

        assertThat(from.getName(), is(to.getName()));
        assertThat(to.getArray()[0], sameInstance(to));
        assertThat(to.getArray().length, is(1));
        assertThat(to.getCollection().iterator().next(), sameInstance(to));
        assertThat(to.getCollection().size(), is(1));
        assertThat(to.getMap().get(to.getName()), sameInstance(to));
        assertThat(to.getMap().size(), is(1));
        assertSame(to.getClazz(), List.class);
    }
    
    public static junit.framework.Test suite() {
        return new JUnit4TestAdapter(new Object(){}.getClass().getEnclosingClass());
    }
}
