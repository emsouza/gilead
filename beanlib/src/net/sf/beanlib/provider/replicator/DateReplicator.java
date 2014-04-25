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

import java.sql.Time;
import java.sql.Timestamp;
import java.util.Date;

import net.jcip.annotations.ThreadSafe;
import net.sf.beanlib.spi.BeanTransformerSpi;
import net.sf.beanlib.spi.replicator.DateReplicatorSpi;

/**
 * Default implementation of {@link net.sf.beanlib.spi.replicator.DateReplicatorSpi}.
 * 
 * @author Joe D. Velopar
 */
public class DateReplicator extends ReplicatorTemplate implements DateReplicatorSpi
{
    private static final Factory factory = new Factory();
    
    /**
     * Factory for {@link DateReplicator}
     * 
     * @author Joe D. Velopar
     */
    @ThreadSafe
    private static class Factory implements DateReplicatorSpi.Factory {
        private Factory() {}
        
        public DateReplicator newDateReplicatable(BeanTransformerSpi beanTransformer) {
            return new DateReplicator(beanTransformer);
        }
    }

    public static DateReplicator newDateReplicatable(BeanTransformerSpi beanTransformer) {
        return factory.newDateReplicatable(beanTransformer);
    }
    
    protected DateReplicator(BeanTransformerSpi beanTransformer) 
    {
        super(beanTransformer);
    }
    
    public <T> T replicateDate(Date fromDate, Class<T> toClass)
    {
        if (!toClass.isAssignableFrom(fromDate.getClass()))
            return null;
        // Timestamp
        if (fromDate instanceof Timestamp) 
        {
            Timestamp toDate = new Timestamp(fromDate.getTime());
            putTargetCloned(fromDate, toDate);
            return toClass.cast(toDate);
        }
        // Time
        if (fromDate instanceof Time) 
        {
            Time toDate = new Time(fromDate.getTime());
            putTargetCloned(fromDate, toDate);
            return toClass.cast(toDate);
        }
        // java.sql.Date
        if (fromDate instanceof java.sql.Date) 
        {
            java.sql.Date toDate = new java.sql.Date(fromDate.getTime());
            putTargetCloned(fromDate, toDate);
            return toClass.cast(toDate);
        }
        // Date
        Date toDate = new Date(fromDate.getTime());
        putTargetCloned(fromDate, toDate);
        return toClass.cast(toDate);
    }
}
