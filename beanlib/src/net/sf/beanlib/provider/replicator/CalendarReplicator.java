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
package net.sf.beanlib.provider.replicator;

import java.util.Calendar;

import net.jcip.annotations.ThreadSafe;
import net.sf.beanlib.spi.BeanTransformerSpi;
import net.sf.beanlib.spi.replicator.CalendarReplicatorSpi;

/**
 * Default implementation of {@link CalendarReplicatorSpi}.
 * 
 * @author Joe D. Velopar
 */
public class CalendarReplicator extends ReplicatorTemplate implements CalendarReplicatorSpi
{
    private static final Factory factory = new Factory();
    
    /**
     * Factory for {@link CalendarReplicator}
     * 
     * @author Joe D. Velopar
     */
    @ThreadSafe
    private static class Factory implements CalendarReplicatorSpi.Factory {
        private Factory() {}
        
        public CalendarReplicator newCalendarReplicatable(BeanTransformerSpi beanTransformer) {
            return new CalendarReplicator(beanTransformer);
        }
    }

    public static CalendarReplicator newCalendarReplicatable(BeanTransformerSpi beanTransformer) {
        return factory.newCalendarReplicatable(beanTransformer);
    }
    
    protected CalendarReplicator(BeanTransformerSpi beanTransformer) 
    {
        super(beanTransformer);
    }
    
    public <T> T replicateCalendar(Calendar from, Class<T> toClass)
    {
        if (!toClass.isAssignableFrom(from.getClass()))
            return null;
        Calendar to = (Calendar)from.clone();
        putTargetCloned(from, to);
        return toClass.cast(to);
    }
}
