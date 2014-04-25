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
package net.sf.beanlib.reflect;

import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.MethodInterceptor;

/**
 * Provides dynamic subclass instances similar to dynamic proxy of JDK.
 * 
 * @author Joe D. Velopar
 */
public class Subclass
{
    /** 
     * Returns a subclass instance of the given super class, with every method intercepted by the
     * given interceptor.
     */
    public static<T> T newInstance(Class<T> superClass, MethodInterceptor interceptor) 
    {
        Enhancer e = new Enhancer();
        e.setSuperclass(superClass);
        e.setCallback(interceptor);
        
        @SuppressWarnings("unchecked") T ret = (T)e.create();
        return ret;
    }
    
    private Subclass() {}
}
