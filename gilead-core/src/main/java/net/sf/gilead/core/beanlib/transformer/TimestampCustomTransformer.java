package net.sf.gilead.core.beanlib.transformer;

import java.sql.Timestamp;
import java.util.Date;
import java.util.Map;

import net.sf.beanlib.PropertyInfo;
import net.sf.beanlib.spi.BeanTransformerSpi;
import net.sf.beanlib.spi.CustomBeanTransformerSpi;

/**
 * Timestamp transformer. Needed to keep nanoseconds part (by default, beanLib will convert it to simple Date if the
 * setter only declares an argument of type java.util.Date)
 * 
 * @author BMARCHESSON
 */
public class TimestampCustomTransformer implements CustomBeanTransformerSpi {

    private BeanTransformerSpi beanTransformer;

    /**
     * Constructor
     * 
     * @param beanTransformer
     */
    public TimestampCustomTransformer(final BeanTransformerSpi beanTransformer) {
        this.beanTransformer = beanTransformer;
    }

    /**
     * Filter method
     */
    @Override
    public boolean isTransformable(Object from, Class<?> toClass, PropertyInfo info) {
        return ((from instanceof Timestamp) && (toClass == Date.class));
    }

    /**
     * Transformation method
     */
    @Override
    @SuppressWarnings("unchecked")
    public <T> T transform(Object in, Class<T> toClass, PropertyInfo info) {
        Map<Object, Object> cloneMap = beanTransformer.getClonedMap();
        Object clone = cloneMap.get(in);

        if (clone != null) {
            return (T) clone;
        }

        Timestamp date = (Timestamp) in;
        clone = new Timestamp(date.getTime());

        ((Timestamp) clone).setNanos(date.getNanos());
        cloneMap.put(in, clone);

        return (T) clone;
    }
}
