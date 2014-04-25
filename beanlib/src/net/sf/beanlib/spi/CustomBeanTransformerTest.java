/*
 * Copyright 2009 The Apache Software Foundation.
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
package net.sf.beanlib.spi;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.CoreMatchers.sameInstance;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import junit.framework.JUnit4TestAdapter;
import net.sf.beanlib.BeanlibException;
import net.sf.beanlib.PropertyInfo;
import net.sf.beanlib.provider.BeanTransformer;
import net.sf.beanlib.provider.replicator.BeanReplicator;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;
import org.junit.Test;

/**
 * @author Hanson Char
 */
public class CustomBeanTransformerTest {
    public static class Point {
        private final int x, y;
        
        public Point(int x, int y) {
            this.x = x;
            this.y = y;
        }
        public int getX() { return x; }
        public int getY() { return y; }
        @Override public String toString() { return ToStringBuilder.reflectionToString(this, ToStringStyle.MULTI_LINE_STYLE); }
        @Override public boolean equals(Object obj) { return EqualsBuilder.reflectionEquals(this, obj); }
    }
    
    public static class Bean {
        private Point point;
        private Collection<Point> collection;
        private Point[] array;
        private Map<String,Point> map;
        
        public Map<String, Point> getMap() { return map; }
        public void setMap(Map<String, Point> map) { this.map = map; }
        
        public Point[] getArray() { return array; }
        public void setArray(Point[] array) { this.array = array; }
        
        public Collection<Point> getCollection() { return collection; }
        public void setCollection(Collection<Point> collection) { this.collection = collection; }
        
        public Point getPoint() { return point; }
        public void setPoint(Point point) { this.point = point; }
        
        @Override public String toString() { return ToStringBuilder.reflectionToString(this, ToStringStyle.MULTI_LINE_STYLE); }
    }
    
    public static class MyBeanTransformer implements CustomBeanTransformerSpi {
        public  boolean isTransformable(Object from, Class<?> toClass, PropertyInfo propertyInfo) {
            return toClass == Point.class && from != null;
        }
    
        public <T> T transform(Object in, Class<T> toClass, PropertyInfo propertyInfo) {
            Point from = (Point)in;
            // Note the framework takes care of the issue of object identity,
            // so we don't need to here.
            @SuppressWarnings("unchecked")
            T ret = (T)new Point(from.getX(), from.getY());
            return ret;
        }
    }
    
    public static class MyBeanTransformerFactory implements CustomBeanTransformerSpi.Factory {
        public CustomBeanTransformerSpi newCustomBeanTransformer(BeanTransformerSpi beanTransformer) {
            return new MyBeanTransformer();
        }
    }

    @Test(expected=BeanlibException.class)
    public void testReplicationFailure() {
        // Initialize a bean
        Bean from = new Bean();
        from.setPoint(new Point(1,2));
        // Tries to replicate the bean via the default replicator
        BeanReplicator replicator = new BeanReplicator();
        // Will cause exception, as Point has no default constructor
        replicator.replicateBean(from);
    }
    
    @Test
    public void testCustomReplicationSuccess() {
        // Initialize a bean
        Bean from = new Bean();
        from.setPoint(new Point(1,2));
        // Partially overrides the default transformer behavior
        BeanTransformerSpi transformer = new BeanTransformer();
        transformer.initCustomTransformerFactory(new MyBeanTransformerFactory());
        BeanReplicator replicator = new BeanReplicator(transformer);
        Bean to = replicator.replicateBean(from); // now works!
        System.out.println(to);
        
        assertThat(to.getPoint(), is(from.getPoint()));
        assertThat(to.getCollection(), is(nullValue()));

        assertThat(to, not(sameInstance(from)));
        assertThat(to.getPoint(), not(sameInstance(from.getPoint())));
        assertThat(from.getCollection(), is(nullValue()));
    }
    
    @Test
    public void testCustomCollectionReplication() {
        Bean from = new Bean();
        final Point p0 = new Point(3,4);
        final Point p3 = p0;
        Collection<Point> col = Arrays.asList(p0, null, new Point(5,6), p3);
        from.setCollection(col);
        BeanTransformerSpi transformer = new BeanTransformer();
        transformer.initCustomTransformerFactory(new MyBeanTransformerFactory());
        BeanReplicator replicator = new BeanReplicator(transformer);
        Bean to = replicator.replicateBean(from); // now works!
        System.out.println(to);
        
        assertThat(to.getPoint(), is(nullValue()));
        assertThat(from.getPoint(), is(nullValue()));
        
        assertThat(to, not(sameInstance(from)));
        assertThat(from.getCollection(), is(to.getCollection()));
        assertThat(from.getCollection(), not(sameInstance(to.getCollection())));
        
        List<Point> toList = (List<Point>)to.getCollection();
        assertThat(toList.get(0), sameInstance(toList.get(3)));
        assertThat(toList.get(0), not(sameInstance(p0)));
    }
    
    @Test
    public void testCustomArrayReplication() {
        final Bean from = new Bean();
        final Point p0 = new Point(3,4);
        final Point p3 = p0;
        Point[] a = {p0, null, new Point(5,6), p3};
        from.setArray(a);
        BeanTransformerSpi transformer = new BeanTransformer();
        transformer.initCustomTransformerFactory(new MyBeanTransformerFactory());
        BeanReplicator replicator = new BeanReplicator(transformer);
        Bean to = replicator.replicateBean(from); // now works!
        System.out.println(to);
        
        assertThat(to.getPoint(), is(nullValue()));
        assertThat(from.getPoint(), is(nullValue()));

        assertThat(to.getCollection(), is(nullValue()));
        assertThat(from.getCollection(), is(nullValue()));
        
        assertThat(to, not(sameInstance(from)));
        assertTrue(Arrays.equals(from.getArray(), to.getArray()));
        assertThat(from.getArray(), not(sameInstance(to.getArray())));
        
        assertThat(to.getArray()[0], sameInstance(to.getArray()[3]));
        assertThat(to.getArray()[0], not(sameInstance(p0)));
    }
    
    @Test
    public void testCustomMapReplication() {
        Bean from = new Bean();
        final Point p0 = new Point(3,4);
        final Point p56 = new Point(5,6);
        final Point p3 = p0;
        Map<String,Point> map = new HashMap<String,Point>();
        map.put("p0", p0);
        map.put("pnull", null);
        map.put("p5-6", p56);
        map.put("p3", p3);
        from.setMap(map);
        BeanTransformerSpi transformer = new BeanTransformer();
        transformer.initCustomTransformerFactory(new MyBeanTransformerFactory());
        BeanReplicator replicator = new BeanReplicator(transformer);
        Bean to = replicator.replicateBean(from); // now works!
        System.out.println(to);
        
        assertThat(to.getPoint(), is(nullValue()));
        assertThat(from.getPoint(), is(nullValue()));

        assertThat(to.getCollection(), is(nullValue()));
        assertThat(from.getCollection(), is(nullValue()));

        assertThat(to.getArray(), is(nullValue()));
        assertThat(from.getArray(), is(nullValue()));
        
        assertThat(to, not(sameInstance(from)));
        assertThat(from.getMap(), is(to.getMap()));
        assertThat(from.getMap(), not(sameInstance(to.getMap())));
        
        assertThat(to.getMap().get("pnull"), is(nullValue()));
        assertTrue(to.getMap().containsKey("pnull"));

        assertThat(to.getMap().get("p0"), sameInstance(to.getMap().get("p3")));
        assertThat(to.getMap().get("p0"), is(p0));
        assertThat(to.getMap().get("p0"), not(sameInstance(p0)));

        assertThat(to.getMap().get("p5-6"), is(p56));
        assertThat(to.getMap().get("p5-6"), not(sameInstance(p56)));
    }
    
    public static junit.framework.Test suite() {
        return new JUnit4TestAdapter(new Object(){}.getClass().getEnclosingClass());
    }
}
