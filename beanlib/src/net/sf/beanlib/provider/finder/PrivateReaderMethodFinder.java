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
package net.sf.beanlib.provider.finder;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

/**
 * Supports finding JavaBean reader method, either public, protected, or private.
 *   
 * @author Joe D. Velopar
 */
public class PrivateReaderMethodFinder extends ProtectedReaderMethodFinder {
    @Override protected boolean qualified(Method m, int mod) {
        return !Modifier.isStatic(mod) && m.getParameterTypes().length == 0;
    }
}
