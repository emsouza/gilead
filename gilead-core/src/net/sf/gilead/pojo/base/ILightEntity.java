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

package net.sf.gilead.pojo.base;

import net.sf.gilead.pojo.gwt.IGwtSerializableParameter;

/**
 * Persistence info handler interface. All the persistent POJO must implements this interface in stateless mode.
 *
 * @author bruno.marchesson
 */
public interface ILightEntity extends IGwtSerializableParameter {

    String INITIALISED = "initialized";

    /**
     * Add proxy information for the argument property.
     *
     * @param property the property name
     * @param proxyInformation map of proxy informations (string serialized form)
     */
    void addProxyInformation(String property, Object proxyInfo);

    /**
     * Remove a property proxy information
     */
    void removeProxyInformation(String property);

    /**
     * Get proxy information for the argument property
     *
     * @param property the property name
     * @return the proxy informations for the property _ string serialized form(can be null)
     */
    Object getProxyInformation(String property);

    /**
     * Set the initialization state of a property.
     *
     * @param property
     * @param initialised
     */
    void setInitialized(String property, boolean initialised);

    /**
     * @param property
     * @return if the property was initialised, false otherwise
     */
    boolean isInitialized(String property);

    /**
     * Debug method : write the declared proxy information
     *
     * @return a human readable description of proxy information
     */
    String getDebugString();
}
