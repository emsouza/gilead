/*
 * Copyright 2008 The Apache Software Foundation.
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
package net.sf.beanlib.joda;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.CoreMatchers.sameInstance;
import static org.junit.Assert.assertThat;
import junit.framework.JUnit4TestAdapter;
import net.sf.beanlib.provider.replicator.BeanReplicator;
import net.sf.beanlib.spi.ChainedCustomBeanTransformer;
import net.sf.beanlib.spi.TrivialCustomBeanTransformerFactories;

import org.joda.time.DateMidnight;
import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.joda.time.LocalDateTime;
import org.joda.time.LocalTime;
import org.junit.Test;

/**
 * @author Hanson Char
 */

public class JodaTimeTransformerTest {
    @Test
    public void testIdentityTransformer() {
        BeanReplicator[] beanReplicators = {
                new BeanReplicator(new JodaTimeTransformer.Factory()),
                new BeanReplicator(new ChainedCustomBeanTransformer.Factory(new JodaTimeTransformer.Factory())), 
            };

        for (BeanReplicator beanReplicator : beanReplicators) {
            JodaTestBean from = new JodaTestBean();
            from.setDateTime(new DateTime());
            from.setDateMidnight(new DateMidnight());
            from.setLocalDate(new LocalDate());
            from.setLocalDateTime(new LocalDateTime());
            from.setLocalTime(new LocalTime());

            JodaTestBean to = beanReplicator.replicateBean(from);

            assertThat(from.getDateTime(), is(sameInstance(to.getDateTime())));
            assertThat(from.getDateMidnight(), is(sameInstance(to.getDateMidnight())));
            assertThat(from.getLocalDate(), is(sameInstance(to.getLocalDate())));
            assertThat(from.getLocalTime(), is(sameInstance(to.getLocalTime())));
        }
    }

    @Test
    public void testDefaultTransformer() {
        JodaTestBean from = new JodaTestBean();
        from.setDateTime(new DateTime());
        from.setDateMidnight(new DateMidnight());
        from.setLocalDate(new LocalDate());
        from.setLocalDateTime(new LocalDateTime());
        from.setLocalTime(new LocalTime());

        BeanReplicator beanReplicator = new BeanReplicator();
        JodaTestBean to = beanReplicator.replicateBean(from);

        assertThat(from.getDateTime(), not(sameInstance(to.getDateTime())));
        assertThat(to.getDateTime(), not(nullValue()));

        assertThat(from.getDateMidnight(), not(sameInstance(to.getDateMidnight())));
        assertThat(to.getDateMidnight(), not(nullValue()));

        assertThat(from.getLocalDate(), not(sameInstance(to.getLocalDate())));
        assertThat(to.getLocalDate(), not(nullValue()));

        assertThat(from.getLocalTime(), not(sameInstance(to.getLocalTime())));
        assertThat(to.getLocalTime(), not(nullValue()));
    }

    @Test
    public void testNoopTransformer() {
        JodaTestBean from = new JodaTestBean();
        from.setDateTime(new DateTime());
        from.setDateMidnight(new DateMidnight());
        from.setLocalDate(new LocalDate());
        from.setLocalDateTime(new LocalDateTime());
        from.setLocalTime(new LocalTime());

        BeanReplicator beanReplicator = new BeanReplicator(
                TrivialCustomBeanTransformerFactories.getNoopCustomTransformerFactory());
        JodaTestBean to = beanReplicator.replicateBean(from);

        assertThat(from.getDateTime(), not(sameInstance(to.getDateTime())));
        assertThat(to.getDateTime(), not(nullValue()));

        assertThat(from.getDateMidnight(), not(sameInstance(to.getDateMidnight())));
        assertThat(to.getDateMidnight(), not(nullValue()));

        assertThat(from.getLocalDate(), not(sameInstance(to.getLocalDate())));
        assertThat(to.getLocalDate(), not(nullValue()));

        assertThat(from.getLocalTime(), not(sameInstance(to.getLocalTime())));
        assertThat(to.getLocalTime(), not(nullValue()));
    }

    @Test
    public void testChainedNullTransformer() {
        BeanReplicator[] beanReplicators = {
                new BeanReplicator(
                        new ChainedCustomBeanTransformer.Factory(
                                new JodaTimeTransformer.Factory(),
                                    TrivialCustomBeanTransformerFactories.getNullCustomTransformerFactory())),
                new BeanReplicator(
                        new ChainedCustomBeanTransformer.Factory(
                                TrivialCustomBeanTransformerFactories.getNoopCustomTransformerFactory(),
                                TrivialCustomBeanTransformerFactories.getNullCustomTransformerFactory())), 
        };

        for (BeanReplicator beanReplicator : beanReplicators) {
            JodaTestBean from = new JodaTestBean();
            from.setDateTime(new DateTime());
            from.setDateMidnight(new DateMidnight());
            from.setLocalDate(new LocalDate());
            from.setLocalDateTime(new LocalDateTime());
            from.setLocalTime(new LocalTime());

            JodaTestBean to = beanReplicator.replicateBean(from);

            assertThat(to.getDateTime(), is(nullValue()));
            assertThat(to.getDateMidnight(), is(nullValue()));
            assertThat(to.getLocalDate(), is(nullValue()));
            assertThat(to.getLocalTime(), is(nullValue()));
        }
    }
    
    public static junit.framework.Test suite() {
        return new JUnit4TestAdapter(new Object(){}.getClass().getEnclosingClass());
    }
}
