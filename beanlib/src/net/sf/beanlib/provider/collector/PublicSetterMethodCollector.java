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
import java.util.ArrayList;
import java.util.List;

import net.sf.beanlib.spi.BeanMethodCollector;

/**
 * Supports collecting JavaBean public setter methods.
 *   
 * @author Joe D. Velopar
 */
public class PublicSetterMethodCollector implements BeanMethodCollector {
	public Method[] collect(Object bean) {
		// Get all the public member methods of the class or interface,
		// including those declared by the class or interface and
		// those inherited from superclasses and superinterfaces.
		Method[] ma = bean.getClass().getMethods();
		List<Method> list = new ArrayList<Method>();
		
        for (Method m : ma) {
            if (m.getParameterTypes().length == 1) {
                if (m.getName().startsWith(getMethodPrefix()))
                    list.add(m);
            }
        }
		return list.toArray(new Method[list.size()]);
	}

    public String getMethodPrefix() {
        return "set";
    }
}
