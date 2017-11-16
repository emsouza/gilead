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
 * Empty proxy store. The proxy informations is not stored, so this store can be used for cloning entities only (trying
 * to merge them throws an exception).
 *
 * @author bruno.marchesson
 */
public class NoProxyStore implements ProxyStore {

    @Override
    public void storeProxyInformations(Object cloneBean, Object persistentBean, String property, Map<String, Serializable> proxyInformations) {
        // Nothing to do
    }

    @Override
    public void removeProxyInformations(Object pojo, String property) {
        // Nothing to do
    }

    @Override
    public Map<String, Serializable> getProxyInformations(Object pojo, String property) {
        // Must no be called !
        throw new RuntimeException("Cannot merge entities with NoProxyStore !");
    }

    /**
     * Clean up the proxy store after a complete serialization process
     */
    @Override
    public void cleanUp() {}
}
