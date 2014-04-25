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

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import junit.framework.JUnit4TestAdapter;

import org.junit.Test;

/**
 * @author Joe D. Velopar
 */
public class HibernateBeanReplicatorTestMySet 
{
    @Test
	public void testDeepCopyMap() {
		FooWithMySet fooWithMySet = new FooWithMySet();
		fooWithMySet.addToMySet("a");
		fooWithMySet.addToMySet("b");
		// Test recursive references
		fooWithMySet.addToMySet(fooWithMySet);
		
		FooWithList fooList = new FooWithList();
		fooList.addToList("1");
		fooList.addToList("2");
		fooList.setFooWithList(fooList);
		// Test recursive references
		fooList.addToList(fooList);
		fooList.addToList(fooList.getList());
		fooWithMySet.addToMySet(fooList);
		FooWithMySet toSet = new Hibernate3BeanReplicator().deepCopy(fooWithMySet);

		assertFalse(fooWithMySet.getMySet() == toSet.getMySet());
        assertTrue(fooWithMySet.getMySet().size() == toSet.getMySet().size());
		
	}
    
    public static junit.framework.Test suite() {
        return new JUnit4TestAdapter(HibernateBeanReplicatorTestMySet.class);
    }
}
