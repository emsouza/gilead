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
package net.sf.beanlib.hibernate3;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertTrue;
import junit.framework.JUnit4TestAdapter;
import net.sf.beanlib.PropertyInfo;
import net.sf.beanlib.provider.BeanTransformer;
import net.sf.beanlib.provider.replicator.BeanReplicator;
import net.sf.beanlib.spi.BeanTransformerSpi;
import net.sf.beanlib.spi.CustomBeanTransformerSpi;

import org.junit.Test;

/**
 * @author Joe D. Velopar
 */
public class CustomBeanTransformerSpiTest {
    static class A {
        private B b;

        public B getB() {
            return b;
        }

        public void setB(B b) { this.b = b; }    
    }

    static abstract class B {
    }

    static class B1 extends B {
        private B b;

        public B getB() {
            return b;
        }

        public void setB(B b) { this.b = b; }    
    }

    static class B2 extends B {
    }

    private static class MyCustomBeanTransformer implements CustomBeanTransformerSpi {
        public static class Factory implements CustomBeanTransformerSpi.Factory {
          public CustomBeanTransformerSpi newCustomBeanTransformer(BeanTransformerSpi beanTransformer) {
              return new MyCustomBeanTransformer(beanTransformer);
          }
        }
        private final BeanTransformerSpi beanTransformer;
        
        private MyCustomBeanTransformer(BeanTransformerSpi beanTransformer) {
          this.beanTransformer = beanTransformer;
        }

        public boolean isTransformable(Object from, Class<?> toClass, PropertyInfo propertyInfo) {
            return from != null 
                && toClass == B.class;
        }
        
        @SuppressWarnings("unchecked")
        public <T> T transform(Object in, Class<T> toClass, PropertyInfo propertyInfo) {
           return (T)beanTransformer.getBeanReplicatable()
                                    .replicateBean(in, in.getClass());
        }
    }

    @Test
    public void testAbstractClassCopyViaBeanReplicator() {
        A fromA = new A();
        B1 fromB1 = new B1();
        B2 fromB2 = new B2();
        fromB1.setB(fromB2);
        fromA.setB(fromB1);
        A toA = new BeanReplicator(
                    new BeanTransformer()
                       .initCustomTransformerFactory(
                               new MyCustomBeanTransformer.Factory()))
                            .replicateBean(fromA, fromA.getClass());
        assertNotNull(toA);
        assertNotSame(fromA, toA);

        assertTrue(toA.getB().getClass() == B1.class);
        B1 toB1 = (B1)toA.getB();
        assertNotSame(fromA.getB(), toA.getB());

        assertTrue(toB1.getB().getClass() == B2.class);
        B2 toB2 = (B2)toB1.getB();
        assertNotSame(fromB2, toB2);
    }

    @Test
    public void testAbstractClassCopyViaBeanReplicator2() {
        A fromA = new A();
        B1 fromB1 = new B1();
        B2 fromB2 = new B2();
        fromB1.setB(fromB2);
        fromA.setB(fromB1);
        A toA = new BeanReplicator(
                                    new BeanTransformer())
                              .replicateBean(fromA, fromA.getClass());
        assertNotNull(toA);
        assertNotSame(fromA, toA);

        assertTrue(toA.getB().getClass() == B1.class);
        B1 toB1 = (B1)toA.getB();
        assertNotSame(fromA.getB(), toA.getB());

        assertTrue(toB1.getB().getClass() == B2.class);
        B2 toB2 = (B2)toB1.getB();
        assertNotSame(fromB2, toB2);
    }
    
    @Test
    public void testAbstractClassCopyViaHibernate3BeanReplicator() 
    {
        A fromA = new A();
        B1 fromB1 = new B1();
        B2 fromB2 = new B2();
        fromB1.setB(fromB2);
        fromA.setB(fromB1);
        A toA = new Hibernate3BeanReplicator()
                    .initCustomTransformerFactory(
                            new MyCustomBeanTransformer.Factory())
                    .copy(fromA);
        assertNotNull(toA);
        assertNotSame(fromA, toA);

        assertTrue(toA.getB().getClass() == B1.class);
        B1 toB1 = (B1)toA.getB();
        assertNotSame(fromA.getB(), toA.getB());

        assertTrue(toB1.getB().getClass() == B2.class);
        B2 toB2 = (B2)toB1.getB();
        assertNotSame(fromB2, toB2);
    }
    
    @Test
    public void testAbstractClassCopyViaHibernate3BeanReplicator2() 
    {
        A fromA = new A();
        B1 fromB1 = new B1();
        B2 fromB2 = new B2();
        fromB1.setB(fromB2);
        fromA.setB(fromB1);
        A toA = new Hibernate3BeanReplicator()
                .copy(fromA);
        assertNotNull(toA);
        assertNotSame(fromA, toA);

        assertTrue(toA.getB().getClass() == B1.class);
        B1 toB1 = (B1)toA.getB();
        assertNotSame(fromA.getB(), toA.getB());

        assertTrue(toB1.getB().getClass() == B2.class);
        B2 toB2 = (B2)toB1.getB();
        assertNotSame(fromB2, toB2);
    }
    
    public static junit.framework.Test suite() {
        return new JUnit4TestAdapter(CustomBeanTransformerSpiTest.class);
    }

}
