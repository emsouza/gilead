/*
 * Copyright 2007 The Apache Software Foundation.
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
import java.util.Arrays;
import java.util.Comparator;

import net.sf.beanlib.spi.BeanMethodCollector;

/**
 * @author Joe D. Velopar
 */
class SortedMethodCollector implements BeanMethodCollector  
{
    private final boolean asc;
    private final BeanMethodCollector methodCollector = new PublicSetterMethodCollector();
    
    SortedMethodCollector(boolean asc) {
        this.asc = asc;
    }
    
    public Method[] collect(Object bean) {
        Method[] ma = methodCollector.collect(bean);
        Comparator<? super Method> comp = new Comparator<Method>() {
            public int compare(Method m1, Method m2) {
                int ret = m1.getName().compareTo(m2.getName());
                return asc ? ret : (0 - ret);
            }
        };
        Arrays.sort(ma, comp);
        return ma;
    }

    public String getMethodPrefix() {
        return methodCollector.getMethodPrefix();
    }
}
