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
package net.sf.beanlib.provider.replicator;

import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import junit.framework.JUnit4TestAdapter;
import net.sf.beanlib.provider.BeanPopulator;
import net.sf.beanlib.provider.BeanTransformer;
import net.sf.beanlib.spi.DetailedPropertyFilter;

import org.junit.Test;

/**
 * @author Joe D. Velopar
 */
public class HeteroImmutableReplicatorTest {
    public static enum FromEnum {
        F1,
        F2, 
        F3,
        ;
    }
    
    public static enum ToEnum {
        T1, 
        T2, 
        T3,
        ;
    }
    
    public static class FromBean {
        FromEnum enumMember;

        public FromEnum getEnumMember() {
            return enumMember;
        }

        public void setEnumMember(FromEnum enumMember) {
            this.enumMember = enumMember;
        }
    }
    
    public static class ToBean {
        ToEnum enumMember;

        public ToEnum getEnumMember() {
            return enumMember;
        }

        public void setEnumMember(ToEnum enumMember) {
            this.enumMember = enumMember;
        }
    }
    
    @Test
    public void testDefaultPoputlateEnum() 
    {
        for (FromEnum e : FromEnum.values())
        {
            FromBean from = new FromBean();
            from.setEnumMember(e);
            FromBean to = new FromBean();
            assertTrue(from.getEnumMember() == e);
            assertNull(to.getEnumMember());
            
            new BeanPopulator(from, to)
                .initDetailedPropertyFilter(DetailedPropertyFilter.ALWAYS_PROPAGATE)
                .populate();
            assertTrue(to.getEnumMember() == from.getEnumMember());
        }
        for (FromEnum e : FromEnum.values())
        {
            FromBean from = new FromBean();
            from.setEnumMember(e);
            FromBean to = new FromBean();
            assertTrue(from.getEnumMember() == e);
            assertNull(to.getEnumMember());
            
            new BeanPopulator(from, to)
                .initDetailedPropertyFilter(DetailedPropertyFilter.ALWAYS_PROPAGATE)
                .initTransformer(null)
                .populate();
            assertTrue(to.getEnumMember() == from.getEnumMember());
        }
    }
    
    @Test
    public void testPoputlateEnum() 
    {
        for (FromEnum e : FromEnum.values())
        {
            FromBean from = new FromBean();
            from.setEnumMember(e);
            FromBean to = new FromBean();
            assertTrue(from.getEnumMember() == e);
            assertNull(to.getEnumMember());
            
            new BeanPopulator(from, to)
                .initDetailedPropertyFilter(DetailedPropertyFilter.ALWAYS_PROPAGATE)
                .initTransformer(
                    new BeanTransformer()
                                   .initImmutableReplicatableFactory(HeteroImmutableReplicator.factory)
                                )
                .populate();
            assertTrue(to.getEnumMember() == from.getEnumMember());
        }
        for (FromEnum e : FromEnum.values())
        {
            FromBean from = new FromBean();
            from.setEnumMember(e);
            FromBean to = new FromBean();
            assertTrue(from.getEnumMember() == e);
            assertNull(to.getEnumMember());
            
            new BeanPopulator(from, to)
                .initDetailedPropertyFilter(DetailedPropertyFilter.ALWAYS_PROPAGATE)
                .initTransformer(null)
                .populate();
            assertTrue(to.getEnumMember() == from.getEnumMember());
        }
    }
    
    @Test
    public void testPopulateDefaultAcrossEnums() 
    {
        for (FromEnum e : FromEnum.values())
        {
            FromBean from = new FromBean();
            from.setEnumMember(e);
            ToBean to = new ToBean();
            assertTrue(from.getEnumMember() == e);
            assertNull(to.getEnumMember());
            
            new BeanPopulator(from, to)
                .initDetailedPropertyFilter(DetailedPropertyFilter.ALWAYS_PROPAGATE)
                .populate();
            assertTrue(to.getEnumMember() == null);
        }
    }
    
    @Test
    public void testPopulateAcrossEnums()
    {
        for (FromEnum e : FromEnum.values())
        {
            FromBean from = new FromBean();
            from.setEnumMember(e);
            ToBean to = new ToBean();
            assertTrue(from.getEnumMember() == e);
            assertNull(to.getEnumMember());
            
            new BeanPopulator(from, to)
                .initDetailedPropertyFilter(DetailedPropertyFilter.ALWAYS_PROPAGATE)
                .initTransformer(
                    new BeanTransformer()
                                   .initImmutableReplicatableFactory(HeteroImmutableReplicator.factory)
                ).populate();
            assertTrue(to.getEnumMember() == ToEnum.values()[e.ordinal()]);
        }
    }
    
    public static junit.framework.Test suite() {
        return new JUnit4TestAdapter(HeteroImmutableReplicatorTest.class);
    }
}
