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

import net.sf.beanlib.spi.BeanMethodFinder;

/**
 * Supports finding JavaBean public reader method.
 *   
 * @author Joe D. Velopar
 */
public class PublicReaderMethodFinder implements BeanMethodFinder {
    public Method find(final String propertyName, Object bean) {
        String s= propertyName;
        
        if (Character.isLowerCase(propertyName.charAt(0))) {
            s = propertyName.substring(0, 1).toUpperCase();
            
            if (propertyName.length() > 1)
                s += propertyName.substring(1);
        }
        Class<?> beanClass = bean.getClass();
        try {
            // Find the public member method of the class or interface,
            // recursively on super classes and interfaces as necessary.
            Method m = beanClass.getMethod("get" + s);
            if (m.getParameterTypes().length == 0)
                return m;
        } catch (NoSuchMethodException ignore) {
        }
        try {
            Method m = beanClass.getMethod("is" + s);
            if (m.getParameterTypes().length == 0)
                return m;
        } catch (NoSuchMethodException ignore) {
        }
        return null;
    }
}
