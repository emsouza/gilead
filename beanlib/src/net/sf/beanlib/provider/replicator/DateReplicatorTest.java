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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.sql.Time;
import java.sql.Timestamp;
import java.util.Date;

import junit.framework.JUnit4TestAdapter;
import net.sf.beanlib.provider.BeanPopulator;
import net.sf.beanlib.spi.DetailedPropertyFilter;

import org.junit.Test;

/**
 * @author Joe D. Velopar
 */
public class DateReplicatorTest {
    private static class Dates {
        private Date date = new Date();
        private java.sql.Date sqlDate = new java.sql.Date(date.getTime());
        private Time time = new Time(date.getTime());
        private Timestamp timestamp = new Timestamp(date.getTime());
        
        public Date getDate() {
            return date;
        }
        public void setDate(Date date) {
            this.date = date;
        }
        public java.sql.Date getSqlDate() {
            return sqlDate;
        }
        public void setSqlDate(java.sql.Date sqlDate) {
            this.sqlDate = sqlDate;
        }
        public Time getTime() {
            return time;
        }
        public void setTime(Time time) {
            this.time = time;
        }
        public Timestamp getTimestamp() {
            return timestamp;
        }
        public void setTimestamp(Timestamp timestamp) {
            this.timestamp = timestamp;
        }
    }

    
    @Test
    public void test() {
        Dates from = new Dates();
        assertNotNull(from.getDate());
        assertNotNull(from.getSqlDate());
        assertNotNull(from.getTime());
        assertNotNull(from.getTimestamp());

        Dates to = new Dates();
        to.setDate(null);
        to.setSqlDate(null);
        to.setTime(null);
        to.setTimestamp(null);
        assertNull(to.getDate());
        assertNull(to.getSqlDate());
        assertNull(to.getTime());
        assertNull(to.getTimestamp());
        
        new BeanPopulator(from, to)
            .initDetailedPropertyFilter(DetailedPropertyFilter.ALWAYS_PROPAGATE)
            .populate();

        assertEquals(from.getDate(), to.getDate());
        assertEquals(from.getSqlDate(), to.getSqlDate());
        assertEquals(from.getTime(), to.getTime());
        assertEquals(from.getTimestamp(), to.getTimestamp());
    }
    
    public static junit.framework.Test suite() {
        return new JUnit4TestAdapter(new Object(){}.getClass().getEnclosingClass());
    }
}
