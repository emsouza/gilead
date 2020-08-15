package net.sf.gilead.core.beanlib.transformer;

import net.sf.beanlib.PropertyInfo;
import net.sf.beanlib.spi.CustomBeanTransformerSpi;

/**
 * The UnionCustomBeanTransformer hold an array of CustomBeanTransformerSpi, checks if a given instance of a source
 * class if transformable into a destination class iterating over the array of CustomBeanTransformerSpi finding the
 * first appropriate one. Transforms the given instance of the source class into and instance of a destination class
 * iterating over the array of CustomBeanTransformerSpi finding the first appropriate one. This is a copy and paste from
 * http://groups.google.com/group/beanlib/browse_thread/thread/f300b5470c08f683
 *
 * @author Hanson Char
 */
public class UnionCustomBeanTransformer implements CustomBeanTransformerSpi {

    /**
     * Current custom transformers
     */
    private CustomBeanTransformerSpi[] customTransformers;

    /**
     * Register the array of CustomBeanTransformerSpi used by the class
     *
     * @param customTransformers
     */
    public UnionCustomBeanTransformer(CustomBeanTransformerSpi... customTransformers) {
        this.customTransformers = customTransformers;
    }

    /**
     * Iterate over the given array of CustomBeanTransformerSpi, checking if there is a valid transformer to transform
     * the 'from' object, to the target 'toClass'
     *
     * @param from the source object instance
     * @param toClass the destination class to transform the 'from' object in
     * @return true if a custom bean transformer was found, false if not
     */
    @Override
    public boolean isTransformable(Object from, Class<?> toClass, PropertyInfo info) {
        // Iterate over custom transformers
        for (CustomBeanTransformerSpi cbt : customTransformers) {
            if (cbt.isTransformable(from, toClass, info)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Transforms 'in' object into an instance of class 'toClass', using the first valid transformer in the
     * _customTransformers array. Returns null if no valid customTransmer is available.
     *
     * @param in the source object to be transformed
     * @param toClass the destination class which the instance will be returned
     * @param propertyInfo the property which will be passed on the to the valid CustomBeanTransformerSpi registered
     * @return instance of the 'toClass' as a result of a transformation of the 'in' object
     */
    @Override
    public <T> T transform(Object in, Class<T> toClass, PropertyInfo propertyInfo) {
        boolean isTransformed = false;
        T out = null;

        for (CustomBeanTransformerSpi cbt : customTransformers) {
            if (cbt.isTransformable(in, toClass, propertyInfo)) {
                out = cbt.transform(isTransformed ? out : in, toClass, propertyInfo);
                isTransformed = true;
            }
        }

        return out;
    }
}
