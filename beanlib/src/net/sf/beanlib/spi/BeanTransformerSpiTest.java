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
package net.sf.beanlib.spi;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertSame;
import junit.framework.JUnit4TestAdapter;
import net.sf.beanlib.BeanlibException;
import net.sf.beanlib.PropertyInfo;
import net.sf.beanlib.provider.BeanTransformer;
import net.sf.beanlib.provider.replicator.BeanReplicator;

import org.junit.Test;

/**
 * @author Joe D. Velopar
 */
public class BeanTransformerSpiTest {
    private static final A a = new A(new Point(1, 2));

    // BeanReplicator assumes there exists a default no-arg constructor by default.
    @Test(expected=NoSuchMethodException.class)
    public void testNoEmptyConstructorFailure() throws Throwable {
        BeanReplicator beanReplicator = new BeanReplicator();
        try {
            beanReplicator.replicateBean(a);
        } catch(BeanlibException ex) {
            throw ex.getCause();
        }
    }
    
    // Uses a custom transfer to deal with there case when a default no-arg constructor doesn't exist.
    @Test
    public void testNoEmtyConstructor() {
        BeanReplicator beanReplicator = new BeanReplicator(customTransformer());
        A a2 = beanReplicator.replicateBean(a);
        assertNotSame(a, a2);
        assertEquals(a, a2);
        
        assertNotSame(a.getPoint(), a2.getPoint());
        assertEquals(a.getPoint(), a2.getPoint());
        
        assertNotSame(a.getPoint2(), a2.getPoint2());
        assertEquals(a.getPoint2(), a2.getPoint2());
        
        assertSame(a.getPoint(), a.getPoint2());
        assertSame(a2.getPoint(), a2.getPoint2());
    }
    
    private BeanTransformerSpi customTransformer() {
        BeanTransformerSpi beanTransformer = new BeanTransformer();
        return beanTransformer.initCustomTransformerFactory(new CustomBeanTransformerSpi.Factory() {
            public CustomBeanTransformerSpi newCustomBeanTransformer(BeanTransformerSpi beanTransformer) {
                return new CustomBeanTransformerSpi() {
                    public boolean isTransformable(Object from, Class<?> toClass, PropertyInfo propertyInfo) {
                        return toClass == Point.class;
                    }

                    @SuppressWarnings("unchecked")
                    public <T> T transform(Object in, Class<T> toClass, PropertyInfo propertyInfo) {
                        Point from = (Point)in;
                        // Note the framework takes care of the issue of object identity,
                        // so we don't need to here.
                        return (T)new Point(from.getX(), from.getY());
                    }
                };
            }
        });
    }
    
    public static junit.framework.Test suite() {
        return new JUnit4TestAdapter(new Object(){}.getClass().getEnclosingClass());
    }
}
