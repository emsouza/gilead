package net.sf.gilead.core.beanlib.transformer;

import java.util.Map;

import net.sf.beanlib.PropertyInfo;
import net.sf.beanlib.spi.BeanTransformerSpi;
import net.sf.beanlib.spi.CustomBeanTransformerSpi;

/**
 * StackTraceElement transformer. Used to clone an exception that contains a persistent POJO
 * 
 * @author BMARCHESSON
 */
public class StackTraceElementCustomTransformer implements CustomBeanTransformerSpi {

    private BeanTransformerSpi beanTransformer;

    /**
     * Constructor
     * 
     * @param beanTransformer
     */
    public StackTraceElementCustomTransformer(final BeanTransformerSpi beanTransformer) {
        this.beanTransformer = beanTransformer;
    }

    /**
     * Filter method
     */
    @Override
    public boolean isTransformable(Object from, Class<?> toClass, PropertyInfo info) {
        return (toClass == StackTraceElement.class);
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

        StackTraceElement stackTraceElement = (StackTraceElement) in;
        clone = new StackTraceElement(stackTraceElement.getClassName(), stackTraceElement.getMethodName(), stackTraceElement.getFileName(),
                stackTraceElement.getLineNumber());

        cloneMap.put(in, clone);

        return (T) clone;
    }
}
