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
package net.sf.beanlib.spi;

import java.lang.reflect.Method;

/**
 * Used to control if a JavaBean property should be propagated.
 * 
 * @author Joe D. Velopar
 */
public interface PropertyFilter {
    /**
     * Returns true if the given JavaBean property should be propagated;
     * false otherwise.
     * @param propertyName JavaBean property name.
     * @param readerMethod reader method of the JavaBean property name.
     * @return true if the given JavaBean property should be propagated.
     */
    public boolean propagate(String propertyName, Method readerMethod);
}
