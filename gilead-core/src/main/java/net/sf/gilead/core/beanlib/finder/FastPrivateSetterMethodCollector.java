package net.sf.gilead.core.beanlib.finder;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import net.sf.beanlib.spi.BeanMethodCollector;

/**
 * Fast Private Setter Method collector, inspired by the beanlib one but relying on IntropsectionHelper, that caches
 * getDeclaredMethod results
 * 
 * @author bruno.marchesson
 */
public class FastPrivateSetterMethodCollector implements BeanMethodCollector {

    /**
     * Cache map
     */
    private HashMap<Class<?>, Method[]> cache = new HashMap<>();

    @Override
    public Method[] collect(Object bean) {
        Class<?> beanClass = bean.getClass();

        if (cache.containsKey(beanClass) == false) {
            // Get all methods declared by the class or interface.
            // This includes public, protected, default (package) access,
            // and private methods, but excludes inherited methods.
            Set<Method> set = new HashSet<>();

            while (beanClass != Object.class) {
                for (Method m : beanClass.getDeclaredMethods()) {
                    if (!m.getName().startsWith(getMethodPrefix())) {
                        continue;
                    }
                    if (m.getParameterTypes().length != 1) {
                        continue;
                    }
                    final int mod = m.getModifiers();

                    if (Modifier.isStatic(mod)) {
                        continue;
                    }
                    // Adds the specified element to the set if it is not already present
                    set.add(m);
                }
                // climb to the super class and repeat
                beanClass = beanClass.getSuperclass();
            }

            // Add to cache
            cache.put(beanClass, set.toArray(new Method[set.size()]));
        }
        return cache.get(beanClass);

    }

    @Override
    public String getMethodPrefix() {
        return "set";
    }
}
