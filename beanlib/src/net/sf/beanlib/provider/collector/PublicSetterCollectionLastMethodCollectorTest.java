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
package net.sf.beanlib.provider.collector;
import static org.junit.Assert.assertTrue;
import junit.framework.JUnit4TestAdapter;
import net.sf.beanlib.provider.BeanTransformer;
import net.sf.beanlib.provider.replicator.BeanReplicator;
import net.sf.beanlib.spi.BeanTransformerSpi;

import org.junit.Test;

/**
 * @author Joe D. Velopar
 */
public class PublicSetterCollectionLastMethodCollectorTest {
    /**
     * Properties are replicated in ascending order of the property names.
     */
    @Test
    public void testReplicateAscending() {
        A a1 = new A("a1");
        A a2 = new A();
        a1.addA(a1);
        a1.addA(a2);
        assertTrue(2 == a1.getAset().size());

        BeanTransformerSpi beanTransformer = new BeanTransformer();
        beanTransformer.initSetterMethodCollector(new SortedMethodCollector(true));
        A a1clone = new BeanReplicator(beanTransformer)
                                  .replicateBean(a1);
        // Note the missing element due to the element being added
        // to the Set before fully replicated.
        assertTrue(a1clone.getAset().size() == 1);
    }
    
    /**
     * Properties are replicated in descending order of the property names.
     */
    @Test
    public void testReplicateDescending() {
        A a1 = new A("a1");
        A a2 = new A();
        a1.addA(a1);
        a1.addA(a2);
        assertTrue(2 == a1.getAset().size());

        BeanTransformerSpi beanTransformer = new BeanTransformer();
        beanTransformer.initSetterMethodCollector(new SortedMethodCollector(false));
        A a1clone = new BeanReplicator(beanTransformer)
                                  .replicateBean(a1);
        // Note the element is not missing this time, for A's name is populated before
        // being added to the Set.
        assertTrue(a1clone.getAset().size() == 2);
    }
    
    @Test
    public void testCollectionLast() {
        A a1 = new A("a1");
        A a2 = new A();
        a1.addA(a1);
        a1.addA(a2);
        assertTrue(2 == a1.getAset().size());

        BeanTransformerSpi beanTransformer = new BeanTransformer();
        beanTransformer.initSetterMethodCollector(new PublicSetterCollectionLastMethodCollector());
        A a1clone = new BeanReplicator(beanTransformer)
                                  .replicateBean(a1);
        assertTrue(a1clone.getAset().size() == 2);
    }

    public static junit.framework.Test suite() {
        return new JUnit4TestAdapter(PublicSetterCollectionLastMethodCollectorTest.class);
    }
}
