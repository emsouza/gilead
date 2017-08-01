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

package net.sf.gilead.core.store;

import java.io.Serializable;
import java.util.Map;

/**
 * Handler for proxy informations storage and recovery
 *
 * @author bruno.marchesson
 */
public interface ProxyStore {

    /**
     * Store the argument proxy informations.
     *
     * @param cloneBean the cloned, target bean
     * @param persistentBean the persistent, source bean
     * @param property the proxy property name
     * @param proxyInformations the associated proxy informations
     */
    void storeProxyInformations(Object cloneBean, Object persistentBean, String property, Map<String, Serializable> proxyInformations);

    /**
     * Remove the proxy informations from the property of the argument object.
     *
     * @param pojo the root pojo
     * @param property the proxy property name
     */
    void removeProxyInformations(Object object, String property);

    /**
     * Get the proxy informations for the argument pojo and properties
     *
     * @param pojo the root pojo
     * @param property the proxy property name
     * @return the proxy informations if they exists, null otherwise
     */
    Map<String, Serializable> getProxyInformations(Object pojo, String property);

    /**
     * Clean up the proxy store after a complete serialization process
     */
    void cleanUp();
}
