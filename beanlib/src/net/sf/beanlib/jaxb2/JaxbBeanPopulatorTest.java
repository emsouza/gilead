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
package net.sf.beanlib.jaxb2;

import junit.framework.JUnit4TestAdapter;
import net.sf.beanlib.provider.BeanPopulator;

import org.junit.Assert;
import org.junit.Test;

/**
 * @author Hanson Char
 */
public class JaxbBeanPopulatorTest 
{
    public static interface Stuffable {
        public String getStuff();
    }
    
    @Test
    public void testFluentSetter() {
        Stuffable from = new Stuffable() {
            public String getStuff() {
                return "Foo";
            }
        };
        
        Stuffable to = new Stuffable() {
            private String stuff;
            
            public String getStuff() {
                return stuff;
            }
            
            public Stuffable withStuff(String stuff) {
                this.stuff = stuff;
                return this;
            }
        };
        
        new BeanPopulator(from, to)
            .initSetterMethodCollector(new FluentSetterMethodCollector())
            .populate()
            ;
        Assert.assertEquals("Foo",to.getStuff());
    }
    
    @Test
    public void testFluentBeanWithJavaBeanSetter() {
        Stuffable from = new Stuffable() {
            public String getStuff() {
                return "Foo";
            }
        };
        
        Stuffable to = new Stuffable() {
            private String stuff;
            
            public String getStuff() {
                return stuff;
            }
            
            public Stuffable withStuff(String stuff) {
                this.stuff = stuff;
                return this;
            }
        };
        
        new BeanPopulator(from, to)
            .populate()
            ;
        Assert.assertNull(to.getStuff());
    }
    
    @Test
    public void testJavaBeanSetter() {
        Stuffable from = new Stuffable() {
            public String getStuff() {
                return "Foo";
            }
        };
        
        Stuffable to = new Stuffable() {
            private String stuff;
            
            public String getStuff() {
                return stuff;
            }
            
            public void setStuff(String stuff) {
                this.stuff = stuff;
            }
        };
        
        new BeanPopulator(from, to)
            .populate()
            ;
        Assert.assertEquals("Foo",to.getStuff());
    }
    
    @Test
    public void testJavaBeanWithFluentSetter() {
        Stuffable from = new Stuffable() {
            public String getStuff() {
                return "Foo";
            }
        };
        
        Stuffable to = new Stuffable() {
            private String stuff;
            
            public String getStuff() {
                return stuff;
            }
            
            public void setStuff(String stuff) {
                this.stuff = stuff;
            }
        };
        
        new BeanPopulator(from, to)
            .initSetterMethodCollector(new FluentSetterMethodCollector())
            .populate()
            ;
        Assert.assertNull(to.getStuff());
    }

    public static junit.framework.Test suite() {
        return new JUnit4TestAdapter(JaxbBeanPopulatorTest.class);
    }
}
