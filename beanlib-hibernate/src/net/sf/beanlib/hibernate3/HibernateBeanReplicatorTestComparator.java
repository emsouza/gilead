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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNotSame;

import java.util.Comparator;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;

import junit.framework.JUnit4TestAdapter;

import org.junit.Test;

/**
 * @author Joe D. Velopar
 */
public class HibernateBeanReplicatorTestComparator {
//	private Log log = LogFactory.getLog(this.getClass());
    
    private static Comparator<String> reverseComparator = new Comparator<String>() {
        public int compare(String s1, String s2) {
            return s2.compareTo(s1);
        }
        
    };
    
	@Test public void testDeepCopySet() {
        Set<String> fromSet = new TreeSet<String>();
		fromSet.add("1");
		fromSet.add("2");
		Set toSet = new Hibernate3BeanReplicator().deepCopy(fromSet, Set.class);
		assertNotSame(fromSet, toSet);
        assertEquals(fromSet.size(), toSet.size());
	}
    
    @Test public void testDeepCopySortedSetWithComparator() {
        SortedSet<String> fromSet = new TreeSet<String>(reverseComparator);
        fromSet.add("1");
        fromSet.add("2");
        SortedSet toSet = new Hibernate3BeanReplicator().deepCopy(fromSet, SortedSet.class);
        assertNotSame(fromSet, toSet);
        assertEquals(fromSet.size(), toSet.size());
        
        assertNotNull(fromSet.comparator());
        assertNotNull(toSet.comparator());
        assertNotSame(fromSet.comparator(), toSet.comparator());
    }

    @Test public void testDeepCopyMap() {
        Map<String,String> fromMap = new TreeMap<String,String>();
        fromMap.put("1", "1val");
        fromMap.put("2", "2val");
        Map toMap = new Hibernate3BeanReplicator().deepCopy(fromMap, Map.class);
        assertNotSame(fromMap, toMap);
        assertEquals(fromMap.size(), toMap.size());
    }

    @Test public void testDeepCopySortedMapWithComparator() {
        SortedMap<String,String> fromMap = new TreeMap<String,String>(reverseComparator);
        fromMap.put("1", "1val");
        fromMap.put("2", "2val");
        SortedMap toMap = new Hibernate3BeanReplicator().deepCopy(fromMap, SortedMap.class);
        assertNotSame(fromMap, toMap);
        assertEquals(fromMap.size(), toMap.size());
        
        assertNotNull(fromMap.comparator());
        assertNotNull(toMap.comparator());
        assertNotSame(fromMap.comparator(), toMap.comparator());
    }
    
    public static junit.framework.Test suite() {
        return new JUnit4TestAdapter(HibernateBeanReplicatorTestComparator.class);
    }
}
