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
import java.util.Map;

import javax.servlet.http.HttpSession;

import net.sf.gilead.exception.ProxyStoreException;

/**
 * Proxy store for stateful web application
 * 
 * @author bruno.marchesson
 */
public class HttpSessionProxyStore extends AbstractStatefulProxyStore {
	// ----
	// Attributes
	// ----
	/**
	 * The storage thread local
	 */
	private static ThreadLocal<HttpSession> _httpSession = new ThreadLocal<HttpSession>();

	// ----
	// Properties
	// ----
	/**
	 * Store the current HTTP session in the thread local
	 */
	public static void setHttpSession(HttpSession session) {
		_httpSession.set(session);
	}

	// -------------------------------------------------------------------------
	//
	// Abstract methods implementation
	//
	// -------------------------------------------------------------------------
	@Override
	public void delete(String key) {
		getSession().removeAttribute(key);
	}

	@Override
	@SuppressWarnings("unchecked")
	public Map<String, Serializable> get(String key) {
		return (Map<String, Serializable>) getSession().getAttribute(key);
	}

	@Override
	public void store(String key, Map<String, Serializable> proxyInformation) {
		getSession().setAttribute(key, proxyInformation);
	}

	// -------------------------------------------------------------------------
	//
	// Internal methods
	//
	// ------------------------------------------------------------------------
	/**
	 * @return the HTTP session stored in thread local
	 */
	private HttpSession getSession() {
		HttpSession session = (HttpSession) _httpSession.get();
		if (session == null) {
			throw new ProxyStoreException("No HTTP session stored", null);
		}
		return session;
	}
}
