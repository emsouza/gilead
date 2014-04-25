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

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertSame;
import junit.framework.JUnit4TestAdapter;

import org.junit.Test;

/**
 * @author Joe D. Velopar
 */
public class Hibernate3DtoCopierTest
{
    public static class Parent {
        private Foo foo;

        public Foo getFoo()
        {
            return foo;
        }

        public void setFoo(Foo foo)
        {
            this.foo = foo;
        }
    }
    public static class Foo extends Parent {
    }
    
    public static class Child extends Foo {
        private Parent parent;

        public Parent getParent()
        {
            return parent;
        }

        public void setParent(Parent parent)
        {
            this.parent = parent;
        }
    }
    
    @Test public void test() {
        Parent parent = new Parent();
        Child child = new Child();
        child.setParent(parent);
        parent.setFoo(child);
        
        Hibernate3DtoCopier copier = new Hibernate3DtoCopier("net.sf.beanlib", this.getClass());
        Child childCopy = copier.hibernate2dto(child);
        
        assertNotSame(childCopy, child);
        assertNotNull(childCopy.getParent());
        assertSame(childCopy, childCopy.getParent().getFoo());
    }
    
    public static junit.framework.Test suite() {
        return new JUnit4TestAdapter(Hibernate3DtoCopierTest.class);
    }

}
