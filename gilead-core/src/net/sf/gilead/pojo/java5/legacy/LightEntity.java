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

package net.sf.gilead.pojo.java5.legacy;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import net.sf.gilead.pojo.base.ILightEntity;

/**
 * Abstract POJO with minimal proxy informations handling This proxy information is encoded for GWT serialization
 * compatibility (Gilead 1.2)
 * 
 * @author bruno.marchesson
 */
public abstract class LightEntity implements ILightEntity, Serializable {
	// -----
	// Attributes
	// -----
	/**
	 * Serialization ID
	 */
	private static final long serialVersionUID = 1061336746068017740L;

	/**
	 * Map of persistence proxy informations. The key is the property name, the value is a map with persistence
	 * informations filled by the persistence util implementation
	 */
	protected Map<String, String> _proxyInformations;

	/**
	 * Map of persistence initialisation state. The key is the property name, the value is a map with persistence
	 * informations filled by the persistence util implementation
	 */
	protected Map<String, Boolean> _initializationMap;

	// -------------------------------------------------------------------------
	//
	// Constructor
	//
	// -------------------------------------------------------------------------
	/**
	 * Constructor
	 */
	public LightEntity() {
		super();
	}

	// -------------------------------------------------------------------------
	//
	// Public interface
	//
	// -------------------------------------------------------------------------
	/**
	 * Add proxy information
	 */
	@Override
	public void addProxyInformation(String property, Object proxyInfo) {
		if (_proxyInformations == null) {
			_proxyInformations = new HashMap<String, String>();
		}
		_proxyInformations.put(property, (String) proxyInfo);
	}

	/*
	 * (non-Javadoc)
	 * @see net.sf.gilead.pojo.base.ILightEntity#removeProxyInformation(java.lang .String)
	 */
	@Override
	public void removeProxyInformation(String property) {
		if (_proxyInformations != null) {
			_proxyInformations.remove(property);
		}
	}

	/*
	 * (non-Javadoc)
	 * @see net.sf.gilead.pojo.base.ILightEntity#getProxyInformation(java.lang.String )
	 */
	@Override
	public Object getProxyInformation(String property) {
		if (_proxyInformations != null) {
			return _proxyInformations.get(property);
		} else {
			return null;
		}
	}

	/*
	 * (non-Javadoc)
	 * @see org.dotnetguru.lazykiller.pojo.ILazyPojo#getLazyString()
	 */
	@Override
	public String getDebugString() {
		if (_proxyInformations != null) {
			return _proxyInformations.toString();
		} else {
			return null;
		}
	}

	/*
	 * (non-Javadoc)
	 * @see net.sf.gilead.pojo.base.ILightEntity#isInitialized(java.lang.String)
	 */
	@Override
	public boolean isInitialized(String property) {
		if (_initializationMap == null) {
			return true;
		}

		Boolean initialized = _initializationMap.get(property);
		if (initialized == null) {
			return true;
		}
		return initialized.booleanValue();
	}

	/*
	 * (non-Javadoc)
	 * @see net.sf.gilead.pojo.base.ILightEntity#setInitialized(java.lang.String, boolean)
	 */
	@Override
	public void setInitialized(String property, boolean initialized) {
		if (_initializationMap == null) {
			_initializationMap = new HashMap<String, Boolean>();
		}
		_initializationMap.put(property, initialized);
	}

	/*
	 * (non-Javadoc)
	 * @see net.sf.gilead.pojo.gwt.IGwtSerializableParameter#getValue()
	 */
	@Override
	public Object getUnderlyingValue() {
		return this;
	}
}