package net.sf.gilead.core.beanlib.finder;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

import net.sf.beanlib.spi.BeanMethodFinder;
import net.sf.gilead.util.IntrospectionHelper;

/**
 * Fast Private Reader Method finder, inspired by the beanlib one but relying on IntropsectionHelper, that caches
 * getDeclaredMethod results
 * 
 * @author bruno.marchesson
 */
public class FastPrivateReaderMethodFinder implements BeanMethodFinder {

    @Override
    public Method find(final String propertyName, Object bean) {
        String s = propertyName;

        if (Character.isLowerCase(propertyName.charAt(0))) {
            s = propertyName.substring(0, 1).toUpperCase();

            if (propertyName.length() > 1) {
                s += propertyName.substring(1);
            }
        }

        Class<?> beanClass = bean.getClass();
        try {
            Method reader = IntrospectionHelper.getRecursiveDeclaredMethod(beanClass, "get" + s, (Class[]) null);
            if (isStatic(reader) == false) {
                return reader;
            } else {
                // Private or static method
                return null;
            }
        } catch (NoSuchMethodException ignore) {
            try {
                Method reader = IntrospectionHelper.getRecursiveDeclaredMethod(beanClass, "is" + s, (Class[]) null);
                if (isStatic(reader) == false) {
                    return reader;
                } else {
                    // Private or static method
                    return null;
                }
            } catch (NoSuchMethodException ex) {
                // Not found
                return null;
            }
        }
    }

    boolean isStatic(Method m) {
        final int modifier = m.getModifiers();
        return Modifier.isStatic(modifier);
    }
}
