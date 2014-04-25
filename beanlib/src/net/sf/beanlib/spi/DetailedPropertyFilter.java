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
package net.sf.beanlib.spi;

import java.lang.reflect.Method;

import net.sf.beanlib.provider.JavaBeanDetailedPropertyFilter;

/**
 * Used to control if a property should be propagated.
 * 
 * @author Joe D. Velopar
 */
public interface DetailedPropertyFilter {
    public static final DetailedPropertyFilter ALWAYS_PROPAGATE = new DetailedPropertyFilter() {
        public boolean propagate(
            String propertyName, 
            Object fromBean, 
            Method readerMethod, 
            Object toBean, 
            Method setterMethod) 
        {
            return true;
        }
    };
    public static final DetailedPropertyFilter JAVABEAN_PROPAGATE = new JavaBeanDetailedPropertyFilter();
    /**
     * Returns true if the given property should be propagated;
     * false otherwise.
     * @param propertyName property name.
     * @param fromBean from bean.
     * @param readerMethod reader method of the property name.
     * @param toBean to bean.
     * @param setterMethod setter method of the property name.
     * @return true if the given property should be propagated.
     */
    public boolean propagate(String propertyName, 
            Object fromBean, Method readerMethod, 
            Object toBean, Method setterMethod);
}
