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
package net.sf.beanlib.provider.finder;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

import net.sf.beanlib.spi.BeanMethodFinder;

/**
 * Supports finding JavaBean reader method which is either public or protected.
 *   
 * @author Joe D. Velopar
 */
public class ProtectedReaderMethodFinder implements BeanMethodFinder {
    public Method find(final String propertyName, Object bean) {
        String s = propertyName;
        
        if (Character.isLowerCase(propertyName.charAt(0))) {
            s = propertyName.substring(0, 1).toUpperCase();
            
            if (propertyName.length() > 1)
                s += propertyName.substring(1);
        }
        Class<?> beanClass = bean.getClass();
        final int maxLengthOfGetterMethodName = s.length() + 3;
        
        while (beanClass != Object.class) {
            // Find the declared member method of the class or interface,
            // recursively on super classes and interfaces as necessary.
            for (Method m: beanClass.getDeclaredMethods()) {
                final int mod = m.getModifiers();
                
                if (qualified(m, mod)) {
                    final String methodName = m.getName();
                    
                    if (methodName.length() == maxLengthOfGetterMethodName) {
                        if (methodName.endsWith(s) 
                        &&  methodName.startsWith("get"))
                            return m;
                    }
                    else if (methodName.length() == maxLengthOfGetterMethodName-1) {
                        if (methodName.endsWith(s) 
                        &&  methodName.startsWith("is"))
                            return m;
                    }
                }
            }
            // climb to the super class and repeat
            beanClass = beanClass.getSuperclass();
        }
        return null;
    }
    
    protected boolean qualified(Method m, int mod) {
        return (Modifier.isPublic(mod) || Modifier.isProtected(mod)) 
            && !Modifier.isStatic(mod)
            && m.getParameterTypes().length == 0
            ;
    }
}
