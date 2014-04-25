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

import java.util.ArrayList;
import java.util.List;

import junit.framework.JUnit4TestAdapter;
import net.sf.beanlib.hibernate.HibernateBeanReplicator;

import org.apache.log4j.Logger;
import org.junit.Test;

/**
 * @author Joe D. Velopar
 */
public class SubTypeTest {
	Logger log = Logger.getLogger(this.getClass());
	public static abstract class A{};
	public static class B extends A{};
	public static class C extends A{};

	public static class D {
		private List<A> list = new ArrayList<A>();

		public List<A> getList() {
			return list;
		}

		public void setList(List<A> list) {
			this.list = list;
		}
		public void addToList(A a) {
			list.add(a);
		}
	}
    
    @Test
	public void testCopy() {
		D d = new D();
		d.addToList(new B());
		d.addToList(new C());
		
		HibernateBeanReplicator replicator = new Hibernate3BeanReplicator();
		D d2 = replicator.deepCopy(d);
		assertTrue(d2.getList().size() == d.getList().size());
		
		for (A a : d2.getList()) {
			Class type = a.getClass();
			log.info(type);
			assertFalse(type == A.class);
			assertTrue(type == B.class || type == C.class);
		}
	}

    public static junit.framework.Test suite() {
        return new JUnit4TestAdapter(SubTypeTest.class);
    }
}
