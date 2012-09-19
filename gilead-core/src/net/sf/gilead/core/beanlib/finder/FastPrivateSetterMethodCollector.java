/*
 * Copyright 2009 The Apache Software Foundation.
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
	// ----
	// Attributes
	// ----
	/**
	 * Cache map
	 */
	private HashMap<Class<?>, Method[]> _cache = new HashMap<Class<?>, Method[]>();

	// -------------------------------------------------------------------------
	//
	// BeanMethodFinder implementation
	//
	// -------------------------------------------------------------------------

	@Override
	public Method[] collect(Object bean) {
		Class<?> beanClass = bean.getClass();

		if (_cache.containsKey(beanClass) == false) {
			// Get all methods declared by the class or interface.
			// This includes public, protected, default (package) access,
			// and private methods, but excludes inherited methods.
			Set<Method> set = new HashSet<Method>();

			while (beanClass != Object.class) {
				for (Method m : beanClass.getDeclaredMethods()) {
					if (!m.getName().startsWith(getMethodPrefix()))
						continue;
					if (m.getParameterTypes().length != 1)
						continue;
					final int mod = m.getModifiers();

					if (Modifier.isStatic(mod))
						continue;
					// Adds the specified element to the set if it is not
					// already present
					set.add(m);
				}
				// climb to the super class and repeat
				beanClass = beanClass.getSuperclass();
			}

			// Add to cache
			//
			_cache.put(beanClass, set.toArray(new Method[set.size()]));
		}
		return _cache.get(beanClass);

	}

	@Override
	public String getMethodPrefix() {
		return "set";
	}
}
