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

package net.sf.gilead.proxy;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import net.sf.gilead.proxy.xml.AdditionalCode;

/**
 * Proxy manager
 * 
 * @author bruno.marchesson
 */
public class ProxyManager {
	// ----
	// Constants
	// ----
	/**
	 * Addition code for Java 1.4 Light entity
	 */
	public static final String JAVA_14_LAZY_POJO = "net/sf/gilead/proxy/xml/LightEntity.java14.xml";

	/**
	 * Addition code for Java 5 Light entity
	 */
	public static final String JAVA_5_LAZY_POJO = "net/sf/gilead/proxy/xml/LightEntity.java5.xml";

	// ----
	// Attributes
	// ----
	/**
	 * Unique instance of the singleton
	 */
	private static ProxyManager _instance = null;

	/**
	 * Proxy generator
	 */
	private ServerProxyGenerator _proxyGenerator;

	/**
	 * Map of the generated proxy
	 */
	private Map<Class<?>, Class<?>> _generatedProxyMap;

	// ----
	// Properties
	// ----
	/**
	 * @return the instance
	 */
	public static synchronized ProxyManager getInstance() {
		if (_instance == null) {
			_instance = new ProxyManager();
		}
		return _instance;
	}

	/**
	 * @return the proxy Generator
	 */
	public ServerProxyGenerator getProxyGenerator() {
		return _proxyGenerator;
	}

	/**
	 * @param generator the proxy Generator to set
	 */
	public void setProxyGenerator(ServerProxyGenerator generator) {
		_proxyGenerator = generator;
	}

	// -------------------------------------------------------------------------
	//
	// Constructor
	//
	// -------------------------------------------------------------------------
	/**
	 * Constructor
	 */
	protected ProxyManager() {
		_generatedProxyMap = new HashMap<Class<?>, Class<?>>();

		// Default proxy generator
		_proxyGenerator = new JavassistProxyGenerator();
	}

	// ------------------------------------------------------------------------
	//
	// Public interface
	//
	// ------------------------------------------------------------------------
	/**
	 * Generate a proxy class
	 * 
	 * @return the associated proxy class if found, null otherwise
	 */
	public synchronized Class<?> generateProxyClass(Class<?> clazz, AdditionalCode additionalCode) {
		Class<?> proxyClass = _generatedProxyMap.get(clazz);
		if (proxyClass == null) {
			// Generate proxy
			//
			proxyClass = _proxyGenerator.generateProxyFor(clazz, additionalCode);
			_generatedProxyMap.put(clazz, proxyClass);
		}

		return proxyClass;
	}

	/**
	 * @return the associated proxy class if found, null otherwise
	 */
	public Class<?> getProxyClass(Class<?> clazz) {
		return _generatedProxyMap.get(clazz);
	}

	/**
	 * @return the associated source class if found, null otherwise
	 */
	public Class<?> getSourceClass(Class<?> proxyClass) {
		// Iterate over proxy map
		//
		for (Entry<Class<?>, Class<?>> entry : _generatedProxyMap.entrySet()) {
			if (proxyClass.equals(entry.getValue())) {
				return entry.getKey();
			}
		}

		// Source class not found
		//
		return null;
	}

	/**
	 * Clear generated proxy classes (for multiple tests run)
	 */
	public void clear() {
		_generatedProxyMap.clear();
	}
}
