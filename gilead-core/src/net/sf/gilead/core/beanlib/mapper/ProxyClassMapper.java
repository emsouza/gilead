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
package net.sf.gilead.core.beanlib.mapper;

import net.sf.gilead.core.IPersistenceUtil;
import net.sf.gilead.core.beanlib.IClassMapper;
import net.sf.gilead.proxy.AdditionalCodeManager;
import net.sf.gilead.proxy.ProxyManager;
import net.sf.gilead.proxy.xml.AdditionalCode;

/**
 * Class mapper for domain and proxy
 * 
 * @author bruno.marchesson
 */
public class ProxyClassMapper implements IClassMapper {
	// ----
	// Attributes
	// ----
	/**
	 * The associated persistence util
	 */
	protected IPersistenceUtil _persistenceUtil;

	/**
	 * For newly created proxy, must we use Java5 or Java 1.4 generator
	 */
	protected boolean _java5 = true;

	// ----
	// Properties
	// ----
	/**
	 * @return the associated Persistence Util
	 */
	public IPersistenceUtil getPersistenceUtil() {
		return _persistenceUtil;
	}

	/**
	 * @param persistenceUtil the persistenceUtil to set
	 */
	public void setPersistenceUtil(IPersistenceUtil persistenceUtil) {
		_persistenceUtil = persistenceUtil;
	}

	/**
	 * @return is Java5 used for proxy generation ?
	 */
	public boolean isJava5() {
		return _java5;
	}

	/**
	 * @param java5 must Java5 be used for proxy generation ?
	 */
	public void setJava5(boolean java5) {
		_java5 = java5;
	}

	// -------------------------------------------------------------------------
	//
	// IClassMapper implementation
	//
	// -------------------------------------------------------------------------
	/*
	 * (non-Javadoc)
	 * @see net.sf.gilead.core.beanlib.IClassMapper#getSourceClass(java.lang.Class)
	 */
	@Override
	public Class<?> getSourceClass(Class<?> targetClass) {
		return ProxyManager.getInstance().getSourceClass(targetClass);
	}

	/*
	 * (non-Javadoc)
	 * @see net.sf.gilead.core.beanlib.IClassMapper#getTargetClass(java.lang.Class <?>)
	 */
	@Override
	public Class<?> getTargetClass(Class<?> sourceClass) {
		Class<?> proxyClass = ProxyManager.getInstance().getProxyClass(sourceClass);

		if (proxyClass == null) {
			// Force proxy generation for persistent class
			//
			if (_persistenceUtil == null) {
				throw new RuntimeException("Missing PersistenceUtil in ProxyClassMapper : please fill this member...");
			}
			if (_persistenceUtil.isPersistentClass(sourceClass)) {
				AdditionalCode additionalCode;
				if (_java5 == true) {
					additionalCode = AdditionalCodeManager.getInstance().getAdditionalCode(ProxyManager.JAVA_5_LAZY_POJO);
				} else {
					additionalCode = AdditionalCodeManager.getInstance().getAdditionalCode(ProxyManager.JAVA_14_LAZY_POJO);
				}

				proxyClass = ProxyManager.getInstance().generateProxyClass(sourceClass, additionalCode);
			}
		}

		return proxyClass;
	}
}