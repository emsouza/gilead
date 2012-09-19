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

package net.sf.gilead.core.store.stateful;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * In Memory Proxy Information Store. This class stores POJO in a simple hashmap
 * 
 * @author bruno.marchesson
 */
public class InMemoryProxyStore extends AbstractStatefulProxyStore {
	// ----
	// Attributes
	// ----
	/**
	 * The store hashmap
	 */
	protected Map<String, Map<String, Serializable>> _map = new HashMap<String, Map<String, Serializable>>();

	// -------------------------------------------------------------------------
	//
	// Abstract stateful proxy store implementation
	//
	// -------------------------------------------------------------------------
	@Override
	public void delete(String key) {
		_map.remove(key);
	}

	@Override
	public Map<String, Serializable> get(String key) {
		return _map.get(key);
	}

	@Override
	public void store(String key, Map<String, Serializable> proxyInformation) {
		_map.put(key, proxyInformation);
	}

}
