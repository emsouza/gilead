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

import net.sf.beanlib.spi.BeanMethodFinder;
import net.sf.gilead.util.IntrospectionHelper;

/**
 * Fast Private Reader Method finder, inspired by the beanlib one but relying on IntropsectionHelper, that caches
 * getDeclaredMethod results
 * 
 * @author bruno.marchesson
 */
public class FastPrivateReaderMethodFinder implements BeanMethodFinder {
	// -------------------------------------------------------------------------
	//
	// BeanMethodFinder implementation
	//
	// -------------------------------------------------------------------------

	@Override
	public Method find(final String propertyName, Object bean) {
		String s = propertyName;

		if (Character.isLowerCase(propertyName.charAt(0))) {
			s = propertyName.substring(0, 1).toUpperCase();

			if (propertyName.length() > 1)
				s += propertyName.substring(1);
		}

		Class<?> beanClass = bean.getClass();
		try {
			Method reader = IntrospectionHelper.getRecursiveDeclaredMethod(beanClass, "get" + s, (Class[]) null);
			if (isStatic(reader) == false) {
				return reader;
			} else {
				// Private or static method
				//
				return null;
			}
		} catch (NoSuchMethodException ignore) {
			try {
				Method reader = IntrospectionHelper.getRecursiveDeclaredMethod(beanClass, "is" + s, (Class[]) null);
				if (isStatic(reader) == false) {
					return reader;
				} else {
					// Private or static method
					//
					return null;
				}
			} catch (NoSuchMethodException ex) {
				// Not found
				//
				return null;
			}
		}
	}

	boolean isStatic(Method m) {
		final int modifier = m.getModifiers();
		return Modifier.isStatic(modifier);
	}
}
