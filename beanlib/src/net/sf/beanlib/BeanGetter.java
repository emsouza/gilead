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
package net.sf.beanlib;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Array;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;

/**
 * Bean Getter.
 * 
 * @author Joe D. Velopar
 */
public class BeanGetter {
	/** Singleton instance. */
//	private static final boolean debug = false;
	private final Logger log = Logger.getLogger(this.getClass());

	/** Returns the string value of the given property of the specified java bean class. */
	public final String getPropertyAsString(
		Object bean,
		PropertyDescriptor pd) {
		Method m = pd.getReadMethod();
		Object ret;
		try {
			ret = m.invoke(bean);
		} catch (IllegalAccessException e) {
			log.error("", e);
			throw new BeanlibException(e);
		} catch (InvocationTargetException e) {
			log.error("", e.getTargetException());
			throw new BeanlibException(e.getTargetException());
		}
		return ret == null ? (String) ret : ret.toString();
	}
	/** Returns the property name to descriptor map for the given bean class. */
	public final Map<String,PropertyDescriptor> getPropertyName2DescriptorMap(Class<?> beanClass) {
		Map<String,PropertyDescriptor> map = new HashMap<String,PropertyDescriptor>();
		BeanInfo bi;
		try {
			bi = Introspector.getBeanInfo(beanClass);
		} catch (IntrospectionException e) {
			log.error("", e);
			throw new BeanlibException(e);
		}
		PropertyDescriptor[] pda = bi.getPropertyDescriptors();

		for (int i = pda.length - 1; i > -1; i--) {
			PropertyDescriptor pd = pda[i];
			map.put(pd.getName(), pd);
		}
		return Collections.unmodifiableMap(map);
	}
	/**
	 * Returns a hash code modified from the input hash code
	 * with the value returned from a JavaBean reader method
	 * using the algorithm
	 * suggested in Item 8 of Effective Java by Joshua Bloch.
	 * @param hashCode starting hash code
	 * @param m a JavaBean reader method
	 * @param bean JavaNean object
	 */
	private int beanReaderHash(int hashCode, Method m, Object bean)
		throws IllegalAccessException, InvocationTargetException {
		int result = hashCode == 0 ? 17 : hashCode;
		Object v = m.invoke(bean);
		if (v == null)
			return result;
		Class<?> c = m.getReturnType();

		return hashReturnValue(result, c, v);
	}
	/**
	 * @param hashCode hash code value before invoking this method
	 * @param c class of value returned from a JavaBean reader method
	 * @param v value returned from a JavaBean reader method
	 * @return the hash code by taking into account
	 * the give value returned from a JavaBean reader method
	 */
	private int hashReturnValue(int hashCode, Class<?> c, Object v) {
		if (v == null)
			return hashCode;
		if (c == Boolean.class) {
			boolean b = ((Boolean) v).booleanValue();
			return b ? hashCode : addBeanHashCode(hashCode, 1);
		}
		if (c == Byte.class) {
			Byte b = (Byte) v;
			return addBeanHashCode(hashCode, b.byteValue());
		}
		if (c == Character.class) {
			Character ch = (Character) v;
			return addBeanHashCode(hashCode, ch.charValue());
		}
		if (c == Short.class) {
			Short s = (Short) v;
			return addBeanHashCode(hashCode, s.shortValue());
		}
		if (c == Integer.class) {
			Integer i = (Integer) v;
			return addBeanHashCode(hashCode, i.intValue());
		}
		if (c == Long.class) {
			Long L = (Long) v;
			long l = L.longValue();
			return addBeanHashCode(hashCode, (int) (l ^ (l >>> 32)));
		}
		if (c == Float.class) {
			Float F = (Float) v;
			return addBeanHashCode(
				hashCode,
				Float.floatToIntBits(F.floatValue()));
		}
		if (c == Array.class) {
			Object[] a = (Object[]) v;

			for (int i = a.length - 1; i > -1; i--) {
				Object o = a[i];
				hashCode =
					addBeanHashCode(
						hashCode,
						hashReturnValue(hashCode, o.getClass(), o));
			}
			return hashCode;
		}
		// Object reference
		return addBeanHashCode(hashCode, v.hashCode());
	}
	/**
	 * @param hashCode hash code to add to
	 * @param delta value to be added to the hash code
	 * @return resultant hash code added with the given delta value.
	 */
	private int addBeanHashCode(int hashCode, int delta) {
		return 37 * hashCode + delta;
	}
	/**
	 * Returns a hash code for the given JavaBean object
	 * for all the reader methods 
	 * using the algorithm suggested in 
	 * Item 8 of Effective Java by Joshua Bloch.
	 *	
	 * @param bean java bean for which the hash code is to be calculated.
	 * @return the hash code for the given java bean.
	 */
	public int getBeanHashCode(Object bean) {
		Class<?> beanClass = bean.getClass();
		BeanInfo bi;
		try {
			bi = Introspector.getBeanInfo(beanClass);
		} catch (IntrospectionException e) {
			log.error("", e);
			throw new BeanlibException(e);
		}
		int hashCode = 0;
		PropertyDescriptor[] pda = bi.getPropertyDescriptors();

		try {
			for (int i = pda.length - 1; i > -1; i--) {
				PropertyDescriptor pd = pda[i];
				Method m = pd.getReadMethod();
				hashCode = beanReaderHash(hashCode, m, bean);
			}
		} catch (IllegalAccessException e) {
			log.error("", e);
			throw new BeanlibException(e);
		} catch (InvocationTargetException e) {
			Throwable t = e.getTargetException();
			log.error("", t);
			throw new BeanlibException(t);
		}
		return hashCode;
	}
}