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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import net.sf.beanlib.spi.BeanMethodCollector;

/**
 * Supports collecting JavaBean public setter methods.
 * This class differs from the 
 * {@link net.sf.beanlib.provider.collector.PublicSetterMethodCollector PublicSetterMethodCollector} 
 * in that {@link java.util.Collection Collection} and {@link java.util.Map Map} setter methods 
 * are guaranteed to be placed after the other setter methods in the returned 
 * {@link java.lang.reflect.Method Method} array.
 *   
 * @author Joe D. Velopar
 */
public class PublicSetterCollectionLastMethodCollector implements BeanMethodCollector  
{
    private final BeanMethodCollector publicSetterMethodCollector = new PublicSetterMethodCollector();

    public Method[] collect(Object bean) {
        Method[] ma = publicSetterMethodCollector.collect(bean);
        List<Method> colMethods = new ArrayList<Method>();
        
        for (Method m: ma) {
            Class<?> paramType = m.getParameterTypes()[0];
            
            if (Collection.class.isAssignableFrom(paramType) 
            ||  Map.class.isAssignableFrom(paramType))
                colMethods.add(m);
        }
        int colMethodsSize = colMethods.size();
        if (colMethodsSize == 0 || colMethodsSize == ma.length)
            return ma;
        List<Method> methods = new ArrayList<Method>(Arrays.asList(ma));
        methods.removeAll(colMethods);
        methods.toArray(ma);
        int i = methods.size();
        
        for (Method m: colMethods)
            ma[i++] = m;
        return ma;
    }

    public String getMethodPrefix() {
        return publicSetterMethodCollector.getMethodPrefix();
    }
}
