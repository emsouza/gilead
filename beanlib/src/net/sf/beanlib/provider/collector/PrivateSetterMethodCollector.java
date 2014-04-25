/*
 * Copyright 2005 The Apache Software Foundation.
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
package net.sf.beanlib.provider.collector;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.HashSet;
import java.util.Set;

import net.sf.beanlib.spi.BeanMethodCollector;

/**
 * Supports collecting JavaBean setter methods, both public, protected and private.
 *   
 * @author Joe D. Velopar
 */
public class PrivateSetterMethodCollector implements BeanMethodCollector {
    public Method[] collect(Object bean) {
        Class<?> beanClass = bean.getClass();
        // Get all methods declared by the class or interface.
        // This includes public, protected, default (package) access, 
        // and private methods, but excludes inherited methods.
        Set<Method> set = new HashSet<Method>();
        
        while (beanClass != Object.class) 
        {
            for (Method m : beanClass.getDeclaredMethods()) 
            {
                if (!m.getName().startsWith(getMethodPrefix()))
                    continue;
                if (m.getParameterTypes().length != 1)
                    continue;
                final int mod = m.getModifiers();
                
                if (Modifier.isStatic(mod))
                    continue;
                // Adds the specified element to the set if it is not already present
                set.add(m);
            }
            // climb to the super class and repeat
            beanClass = beanClass.getSuperclass();
        }
        return set.toArray(new Method[set.size()]);
    }

    public String getMethodPrefix() {
        return "set";
    }
}
