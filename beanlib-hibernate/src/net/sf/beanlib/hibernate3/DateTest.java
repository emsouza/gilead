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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import java.sql.Timestamp;
import java.util.Date;
import java.util.Map;

import junit.framework.JUnit4TestAdapter;
import net.sf.beanlib.PropertyInfo;
import net.sf.beanlib.hibernate.HibernateBeanReplicator;
import net.sf.beanlib.spi.BeanTransformerSpi;
import net.sf.beanlib.spi.CustomBeanTransformerSpi;

import org.junit.Test;

/**
 * @author Joe D. Velopar
 */
public class DateTest 
{
    private static class Pojo 
    {
        private Date date = new Timestamp(new Date().getTime());
        // reference to the same date instance
        private Date dateRef = date;
        private String text = "whatever";
        
        public Date getDate() {
            return date;
        }

        public void setDate(Date date) {
            this.date = date;
        }

        public String getText() {
            return text;
        }

        public void setText(String text) {
            this.text = text;
        }

        public Date getDateRef() {
            return dateRef;
        }

        public void setDateRef(Date dateRef) {
            this.dateRef = dateRef;
        }
    }
    
    @Test 
    public void testConvertTimestampToDate() 
    {
        final Pojo source = new Pojo();
        // Replicate Timestamp into Date
        HibernateBeanReplicator replicator =
            new Hibernate3BeanReplicator()
            .initCustomTransformerFactory(
                new CustomBeanTransformerSpi.Factory() 
                {
                    public CustomBeanTransformerSpi newCustomBeanTransformer(final BeanTransformerSpi beanTransformer) 
                    {
                        return new CustomBeanTransformerSpi() 
                        {

                            public boolean isTransformable(Object from, Class<?> toClass, PropertyInfo propertyInfo) {
                                return from instanceof Date && toClass == Date.class;
                            }

                            public <T> T transform(Object in, Class<T> toClass, PropertyInfo propertyInfo) {
                                assertTrue("date".equals(propertyInfo.getPropertyName()) || "dateRef".equals(propertyInfo.getPropertyName()));
                                assertSame(source, propertyInfo.getFromBean());
                                assertNotSame(source, propertyInfo.getToBean());
                                Map<Object,Object> cloneMap = beanTransformer.getClonedMap();
                                Object clone = cloneMap.get(in);
                                  
                                if (clone != null)
                                    return (T)clone;
                                Date d = (Date)in;
                                clone = new Date(d.getTime());
                                cloneMap.put(in, clone);
                                return (T)clone;
                            }
                        };
                    }
                });
        Pojo clone = replicator.deepCopy(source);
        
        assertNotSame(clone, source);
        assertEquals(clone.getText(), source.getText());
        
        assertSame(source.getDate().getClass(), Timestamp.class);
        assertSame(clone.getDate().getClass(), Date.class);
        
        assertTrue(clone.getDate().getTime() == source.getDate().getTime());
        
        assertSame(source.getDate(), source.getDateRef());
        assertSame(clone.getDate(), clone.getDateRef());
        assertNotSame(source.getDate(), clone.getDate());
    }
    
    public static junit.framework.Test suite() {
        return new JUnit4TestAdapter(DateTest.class);
    }
}
