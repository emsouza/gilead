package net.sf.gilead.core.beanlib.transformer;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.sf.beanlib.spi.BeanTransformerSpi;
import net.sf.beanlib.spi.CustomBeanTransformerSpi;

/**
 * Factory for gilead custom transformers
 *
 * @author Alexandre Pretyman, Bruno Marchesson
 */
public class CustomTransformersFactory {

    private static final Logger LOGGER = LoggerFactory.getLogger(CustomTransformersFactory.class);

    /**
     * Singleton instance
     */
    private static CustomTransformersFactory INSTANCE = null;

    /**
     * List of custom bean transformers constructors for clone operation
     */
    private List<Constructor<CustomBeanTransformerSpi>> cloneTransformersList;

    /**
     * List of custom bean transformers constructors for merge operation
     */
    private List<Constructor<CustomBeanTransformerSpi>> mergeTransformersList;

    /**
     * @return the unique instance of the factory
     */
    public static synchronized CustomTransformersFactory getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new CustomTransformersFactory();
        }
        return INSTANCE;
    }

    /**
     * Private constructor
     */
    private CustomTransformersFactory() {
        cloneTransformersList = new ArrayList<Constructor<CustomBeanTransformerSpi>>();
        mergeTransformersList = new ArrayList<Constructor<CustomBeanTransformerSpi>>();

        // Transformers needed for Gilead
        addCustomBeanTransformer(TimestampCustomTransformer.class);
        addCustomBeanTransformer(StackTraceElementCustomTransformer.class);
    }

    /**
     * Add a custom bean transformer for both clone and merge
     */
    @SuppressWarnings("unchecked")
    public void addCustomBeanTransformer(Class transformerClass) {
        Constructor<CustomBeanTransformerSpi> constructor = getConstructorFor(transformerClass);
        cloneTransformersList.add(constructor);
        mergeTransformersList.add(constructor);
    }

    /**
     * Add a custom bean transformer for clone operation
     */
    @SuppressWarnings("unchecked")
    public void addCloneCustomBeanTransformer(Class transformerClass) {
        Constructor<CustomBeanTransformerSpi> constructor = getConstructorFor(transformerClass);
        cloneTransformersList.add(constructor);
    }

    /**
     * Add a custom bean transformer for merge operation
     */
    @SuppressWarnings("unchecked")
    public void addMergeCustomBeanTransformer(Class transformerClass) {
        Constructor<CustomBeanTransformerSpi> constructor = getConstructorFor(transformerClass);
        mergeTransformersList.add(constructor);
    }

    /**
     * Create a union transformer for clone if needed
     *
     * @param beanTransformer the input bean transformer
     * @return the beanlib CustomBeanTransformer.
     */
    public CustomBeanTransformerSpi createUnionCustomBeanTransformerForClone(BeanTransformerSpi beanTransformer) {
        int transformerCount = cloneTransformersList.size();
        if (transformerCount == 1) {
            // No additional custom transformer defined : just use the one
            return instantiate(cloneTransformersList.get(0), beanTransformer);
        } else {
            // Create each transformer
            CustomBeanTransformerSpi[] customBeanTransformers = new CustomBeanTransformerSpi[transformerCount];
            for (int index = 0; index < transformerCount; index++) {
                customBeanTransformers[index] = instantiate(cloneTransformersList.get(index), beanTransformer);
            }

            return new UnionCustomBeanTransformer(customBeanTransformers);
        }
    }

    /**
     * Create a union transformer for merge if needed
     *
     * @param beanTransformer the input bean transformer
     * @return the beanlib CustomBeanTransformer.
     */
    public CustomBeanTransformerSpi createUnionCustomBeanTransformerForMerge(BeanTransformerSpi beanTransformer) {
        int transformerCount = mergeTransformersList.size();
        if (transformerCount == 1) {
            // No additional custom transformer defined : just use the one
            return instantiate(mergeTransformersList.get(0), beanTransformer);
        } else {
            // Create each transformer
            CustomBeanTransformerSpi[] customBeanTransformers = new CustomBeanTransformerSpi[transformerCount];
            for (int index = 0; index < transformerCount; index++) {
                customBeanTransformers[index] = instantiate(mergeTransformersList.get(index), beanTransformer);
            }

            return new UnionCustomBeanTransformer(customBeanTransformers);
        }
    }

    /**
     * Get constructor for the argument class
     *
     * @param clazz the custom bean transformer class
     * @return the constructor that take a BeanTransformerSpi as argument from the class
     */
    protected Constructor<CustomBeanTransformerSpi> getConstructorFor(Class<CustomBeanTransformerSpi> clazz) {
        // Constructor has a transformer argument
        Class<?>[] ctArg = { BeanTransformerSpi.class };

        try {
            // Search for constructors with BeanTransformerSpi argument
            return clazz.getConstructor(ctArg);
        } catch (Exception e) {
            LOGGER.error("Cannot find constructor with BeanTransformerSpi argument for class " + clazz, e);
            throw new RuntimeException("Error retrieving constructor for " + clazz, e);
        }
    }

    /**
     * Create a new transformer instance
     *
     * @param constructor the constructor
     * @param beanTransformer the bean transformer needed argument
     * @return the CustomBeanTransformer
     */
    protected CustomBeanTransformerSpi instantiate(Constructor<CustomBeanTransformerSpi> constructor, final BeanTransformerSpi beanTransformer) {
        // Constructor argument
        Object[] initArgs = { beanTransformer };

        // New instance of custom transformer
        try {
            return constructor.newInstance(initArgs);
        } catch (Exception e) {
            // Throw unrecoverable error on exception
            LOGGER.error("Transformer initialization error", e);
            throw new RuntimeException("Transformer initialization error", e);
        }
    }
}
