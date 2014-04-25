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
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;

import java.lang.reflect.Method;
import java.util.Collections;
import java.util.Iterator;
import java.util.Map;

import junit.framework.JUnit4TestAdapter;
import net.sf.beanlib.spi.PropertyFilter;

import org.apache.log4j.Logger;
import org.junit.Test;

/**
 * @author Joe D. Velopar
 */
public class HibernateBeanReplicatorTestMap 
{
	private Logger log = Logger.getLogger(this.getClass());
    
    @Test
	public void testDeepCopyMap() {
		FooWithMap fooMap = new FooWithMap(null);
		fooMap.addToMap("1", "a");
		fooMap.addToMap("2", "b");
		// Test recursive references
		fooMap.addToMap("3", fooMap);
		
		FooWithList fooList = new FooWithList();
		fooList.addToList("1");
		fooList.addToList("2");
		fooList.setFooWithList(fooList);
		// Test recursive references
		fooList.addToList(fooList);
		fooList.addToList(fooList.getList());
		fooMap.addToMap("4", fooList);
		FooWithMap toMap = new Hibernate3BeanReplicator().deepCopy(fooMap);

		assertFalse(fooMap.getMap() == toMap.getMap());
		
		Iterator itr1=fooMap.getMap().entrySet().iterator();
		Iterator itr2=toMap.getMap().entrySet().iterator();
		
		while (itr1.hasNext()) {
			Map.Entry n1 = (Map.Entry)itr1.next();
			Map.Entry n2 = (Map.Entry)itr2.next();
			log.debug("n1="+n1+", n2="+n2);
			
			if (n1.getKey()  instanceof String && n1.getValue() instanceof String) {
				assertEquals(n1, n2);
			}
		}
		assertFalse(itr2.hasNext());
	}
    
    @Test
    public void testCopyMap() {
        FooWithMap fooMap = new FooWithMap(null);
        fooMap.addToMap("1", "a");
        fooMap.addToMap("2", "b");
        
        {
            FooWithMap toFooWithMap = new Hibernate3BeanReplicator().copy(fooMap);
            Map toMap = toFooWithMap.getMap();
            toMap.size();
//            log.info("toMap.size()=" + toMap.size());
            assertEquals(toMap.size(), 2);
        }
        {
            Hibernate3BeanReplicator r = new Hibernate3BeanReplicator();
            r.getHibernatePropertyFilter().withCollectionPropertyNameSet(null);
            FooWithMap toFooWithMap = r.copy(fooMap)
                                                    ;
            Map toMap = toFooWithMap.getMap();
            toMap.size();
//            log.info("toMap.size()=" + toMap.size());
            assertEquals(toMap.size(), 2);
        }
        {
            Hibernate3BeanReplicator r = new Hibernate3BeanReplicator();
            r.getHibernatePropertyFilter().withCollectionPropertyNameSet(Collections.EMPTY_SET);
            FooWithMap toFooWithMap = r.copy(fooMap);
            Map toMap = toFooWithMap.getMap();
            assertNull(toMap);
        }
        {
            Hibernate3BeanReplicator r = new Hibernate3BeanReplicator();
            r.getHibernatePropertyFilter().withVetoer(new PropertyFilter() {
                                            public boolean propagate(String propertyName, Method readerMethod) {
                                                return !"map".equals(propertyName);
                                            }
                                        });
            FooWithMap toFooWithMap = r.copy(fooMap);
            Map toMap = toFooWithMap.getMap();
            assertNull(toMap);
        }
    }
    
    public static junit.framework.Test suite() {
        return new JUnit4TestAdapter(HibernateBeanReplicatorTestMap.class);
    }
}
