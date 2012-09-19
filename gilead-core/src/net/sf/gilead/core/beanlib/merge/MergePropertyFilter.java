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
package net.sf.gilead.core.beanlib.merge;

import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.Map;

import net.sf.beanlib.spi.DetailedPropertyFilter;
import net.sf.gilead.core.IPersistenceUtil;
import net.sf.gilead.core.annotations.AnnotationsManager;
import net.sf.gilead.core.beanlib.CloneAndMergeConstants;
import net.sf.gilead.core.store.IProxyStore;
import net.sf.gilead.pojo.base.ILightEntity;
import net.sf.gilead.util.IntrospectionHelper;

/**
 * Populatable for POJO with the lazy information of the Hibernate POJO This populatable is used to fill back an
 * Hibernate POJO from proxy information. Only the not lazy properties of the Hibernate POJO are filled. To be correct,
 * the clone and merge operations must be managed with the <b>same</b> Hibernate POJO, or a POJO with the <b>same</b>
 * fetching strategy
 * 
 * @author bruno.marchesson
 */
public class MergePropertyFilter implements DetailedPropertyFilter {
	// ----
	// Attributes
	// ----
	/**
	 * The associated persistence utils
	 */
	private IPersistenceUtil _persistenceUtil;

	/**
	 * The used Pojo store
	 */
	private IProxyStore _proxyStore;

	// ----
	// Properties
	// ----
	/**
	 * @return the persistence Util implementation to use
	 */
	public IPersistenceUtil getPersistenceUtil() {
		return _persistenceUtil;
	}

	/**
	 * @param util the persistenceUtil to set
	 */
	public void setPersistenceUtil(IPersistenceUtil util) {
		_persistenceUtil = util;
	}

	/**
	 * @return the proxy store
	 */
	public IProxyStore getProxyStore() {
		return _proxyStore;
	}

	/**
	 * @param store the proxy store to set
	 */
	public void setProxyStore(IProxyStore store) {
		_proxyStore = store;
	}

	// -------------------------------------------------------------------------
	//
	// Constructor
	//
	// -------------------------------------------------------------------------
	/**
	 * Constructor
	 */
	public MergePropertyFilter(IPersistenceUtil persistenceUtil, IProxyStore proxyStore) {
		setPersistenceUtil(persistenceUtil);
		setProxyStore(proxyStore);
	}

	// -------------------------------------------------------------------------
	//
	// DetailedBeanPopulatable implementation
	//
	// -------------------------------------------------------------------------
	/*
	 * (non-Javadoc)
	 * @see net.sf.beanlib.spi.DetailedBeanPopulatable#shouldPopulate(java.lang.String , java.lang.Object,
	 * java.lang.reflect.Method, java.lang.Object, java.lang.reflect.Method)
	 */
	@Override
	public boolean propagate(String propertyName, Object cloneBean, Method readerMethod, Object persistentBean, Method setterMethod) {
		// Always reset proxy information on stack
		//
		BeanlibThreadLocal.setProxyInformations(null);

		try {
			// Precondition checking
			//
			if ((CloneAndMergeConstants.PROXY_INFORMATIONS.equals(propertyName) == true)
					|| (CloneAndMergeConstants.INITIALIZATION_MAP.equals(propertyName) == true)) {
				return false;
			}

			// Get proxy informations
			//
			Map<String, Serializable> proxyInformations = _proxyStore.getProxyInformations(cloneBean, propertyName);

			// 'ReadOnly' or 'ServerOnly' annotation : original info was loaded,
			// do not copy
			//
			if (/*
				 * (AnnotationsManager.isServerOrReadOnly(cloneBean, propertyName)) ||
				 */
			(AnnotationsManager.isServerOrReadOnly(persistentBean, propertyName))) {
				// If the proxy was initialized before clone
				// force the merge value initialization now
				//
				if (isInitialized(proxyInformations)) {
					Object persistentValue = readPropertyValue(persistentBean, readerMethod.getName());
					_persistenceUtil.initialize(persistentValue);
				}
				return false;
			}

			if (proxyInformations == null) {
				// No proxy informations : just populate the property
				//
				return true;
			}

			// Get clone value
			//
			Object cloneValue = readPropertyValue(cloneBean, readerMethod.getName());

			Class<?> valueClass = readerMethod.getReturnType();
			boolean isCollection = Collection.class.isAssignableFrom(valueClass);
			boolean isMap = Map.class.isAssignableFrom(valueClass);

			if (isCollection) {
				if (isNullValue(cloneValue)) {
					// The value is now null : proxy is needed
					//
					// Set collection proxy
					//
					Object persistentCollection = _persistenceUtil.createPersistentCollection(persistentBean, proxyInformations, null);
					writePropertyValue(persistentBean, persistentCollection, setterMethod.getName(), setterMethod.getParameterTypes());
					//
					return false;
				} else {
					// Store proxy info for the copy operation
					//
					BeanlibThreadLocal.setProxyInformations(proxyInformations);
				}
			} else if (isMap) {
				if (isNullValue(cloneValue)) {
					// Set map proxy
					//
					Object persistentMap = _persistenceUtil.createPersistentMap(persistentBean, proxyInformations, null);
					writePropertyValue(persistentBean, persistentMap, setterMethod.getName(), setterMethod.getParameterTypes());
					return false;
				} else {
					// Store proxy info for the copy operation
					//
					BeanlibThreadLocal.setProxyInformations(proxyInformations);
				}
			} else if (isNullValue(cloneValue) && isInitialized(proxyInformations) == false) {
				// Set an entity proxy
				//
				Object proxy = _persistenceUtil.createEntityProxy(proxyInformations);
				if (proxy != null) {
					writePropertyValue(persistentBean, proxy, setterMethod.getName(), setterMethod.getParameterTypes());
				}

				// Skip beanlib in-depth population
				//
				return false;
			}

			return true;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	// -------------------------------------------------------------------------
	//
	// Internal method
	//
	// -------------------------------------------------------------------------
	/**
	 * Indicates if the argument property is lazy or not
	 * 
	 * @param proxyInfo serialized proxy informations
	 * @return
	 */
	protected boolean isInitialized(Map<String, Serializable> proxyInfo) {
		if (proxyInfo != null) {
			Serializable initialized = proxyInfo.get(ILightEntity.INITIALISED);
			if (initialized != null) {
				return ((Boolean) initialized).booleanValue();
			}
		}

		// The property has no proxy info or it does not
		// contains 'initialized' field
		//
		return true;
	}

	/**
	 * Read a property value, even if it has a private getter
	 */
	private Object readPropertyValue(Object bean, String propertyGetter) throws SecurityException, NoSuchMethodException, IllegalArgumentException,
			IllegalAccessException, InvocationTargetException {
		Method readMethod = IntrospectionHelper.getRecursiveDeclaredMethod(bean.getClass(), propertyGetter, (Class[]) null);
		readMethod.setAccessible(true);

		return readMethod.invoke(bean, (Object[]) null);
	}

	/**
	 * Read a property value, even if it has a private getter
	 */
	private void writePropertyValue(Object bean, Object value, String propertySetter, Class<?>... parameterTypes) throws SecurityException,
			NoSuchMethodException, IllegalArgumentException, IllegalAccessException, InvocationTargetException {
		Method writeMethod = IntrospectionHelper.getRecursiveDeclaredMethod(bean.getClass(), propertySetter, parameterTypes);
		writeMethod.setAccessible(true);

		writeMethod.invoke(bean, value);
	}

	/**
	 * Indicates if the argument value must be considered as null (empty collections or map are also considered as null)
	 * 
	 * @param value
	 * @return
	 */
	private boolean isNullValue(Object value) {
		if (value == null) {
			return true;
		}
		// else if (value instanceof Collection)
		// {
		// return ((Collection<?>) value).isEmpty();
		// }
		// else if (value instanceof Map)
		// {
		// return ((Map<?,?>)value).isEmpty();
		// }

		return false;
	}
}
