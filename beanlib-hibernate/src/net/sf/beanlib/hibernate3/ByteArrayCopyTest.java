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

import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;

import junit.framework.JUnit4TestAdapter;
import net.sf.beanlib.provider.BeanTransformer;
import net.sf.beanlib.provider.replicator.BeanReplicator;

import org.junit.Test;

/**
 * @author Joe D. Velopar
 */
public class ByteArrayCopyTest 
{
    private static class P {
        private byte[] bytes;

        public byte[] getBytes() {
            return bytes;
        }

        public void setBytes(byte[] bytes) {
            this.bytes = bytes;
        } 
    }

    private static class PArray {
        private P[] pa;

        public P[] getPa() {
            return pa;
        }

        public void setPa(P[] pa) {
            this.pa = pa;
        }
    }
    
    @Test
    public void testArrays() {
        P p1 = new P();
        byte[] bytes = { 1, 2, 3 };
        p1.setBytes(bytes);

        P p2 = new P();
        byte[] bytes2 = { 4, 5, 6 };
        p2.setBytes(bytes2);
        
        P[] pa = { p1, p2 };
        PArray parray = new PArray();
        parray.setPa(pa);
        
        {
            PArray parray2 = new BeanReplicator(
                                new BeanTransformer())
                                    .replicateBean(parray, parray.getClass());
            
            assertNotSame(parray, parray2);
            assertNotSame(parray.getPa(), parray2.getPa());
        }

        {
            PArray parray2 = new Hibernate3BeanReplicator().copy(parray);

            assertNotSame(parray, parray2);
            assertNotSame(parray.getPa(), parray2.getPa());
        }

    }
    
    @Test
    public void testBeanReplicator() {
        P p1 = new P();
        byte[] bytes = { 1, 2, 3 };
        p1.setBytes(bytes);
        
        P p2 = new BeanReplicator(
                    new BeanTransformer())
                        .replicateBean(p1, p1.getClass());
        assertNotSame(p1, p2);
        assertNotSame(p1.getBytes(), p2.getBytes());
        assertTrue(Arrays.equals(p1.getBytes(), p2.getBytes()));
    }
    
    @Test
    public void testHibernate3BeanReplicator() {
        P p1 = new P();
        byte[] bytes = { 1, 2, 3 };
        p1.setBytes(bytes);
        
        P p2 = new Hibernate3BeanReplicator().copy(p1);
        assertNotSame(p1, p2);
        assertNotSame(p1.getBytes(), p2.getBytes());
        assertTrue(Arrays.equals(p1.getBytes(), p2.getBytes()));
    }
    
    public static junit.framework.Test suite() {
        return new JUnit4TestAdapter(ByteArrayCopyTest.class);
    }

}
