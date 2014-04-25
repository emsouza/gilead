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
package net.sf.beanlib.utils;

import java.lang.reflect.Field;

import net.sf.beanlib.BeanlibException;

/**
 * Debug Utilities.
 * 
 * @author Joe D. Velopar
 */
public class DebugUtils {
	/** 
	 * Returns the value of a declared field regardless of the privacy modifier.
	 * This breaks encapsulation, so please use it only for debugging purposes. 
	 */
	public Object getFieldValue(Class<?> c, String declaredFieldName, Object target) 
	{
		try {
			Field f = c.getDeclaredField(declaredFieldName);
			f.setAccessible(true);
			return f.get(target);
		} catch (SecurityException e) {
			throw new BeanlibException(e);
		} catch (NoSuchFieldException e) {
			throw new BeanlibException(e);
		} catch (IllegalArgumentException e) {
			throw new BeanlibException(e);
		} catch (IllegalAccessException e) {
			throw new BeanlibException(e);
		}
	}
}
