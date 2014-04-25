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
package net.sf.beanlib.utils;

import java.util.Arrays;

// Copied from http://cvs.apache.org/viewcvs.cgi/jakarta-turbine-jcs/sandbox/yajcache/src/org/apache/jcs/yajcache/util/EqualsUtils.java?only_with_tag=HEAD&view=markup
 
/**
 * Returns true if the two input arguments are equal, handling cases
 * when they are arrays.  
 * Returns false otherwise.
 */
public class EqualsUtils {
//	private static final boolean debug = false;
	/**
     * Returns true if the two input arguments are equal, handling cases
     * when they are arrays.  
     * Returns false otherwise.
     */
    public boolean equals(Object lhs, Object rhs) {
        if (lhs == rhs)
            return true;
        if (lhs == null || rhs == null)
            return false;
        Class<?> lClass = lhs.getClass();
        Class<?> rClass = rhs.getClass();
        
        if (lClass.isArray()
        &&  rClass.isArray())
        {
            Class<?> lCompType = lClass.getComponentType();
            Class<?> rCompType = rClass.getComponentType();
            
            if (lCompType.isPrimitive()) {
                if (rCompType.isPrimitive()) {
                    if (lCompType != rCompType)
                        return false;
                    if (lCompType == int.class)
                        return Arrays.equals((int[])lhs, (int[])rhs);
                    if (lCompType == boolean.class)
                        return Arrays.equals((boolean[])lhs, (boolean[])rhs);
                    if (lCompType == byte.class)
                        return Arrays.equals((byte[])lhs, (byte[])rhs);
                    if (lCompType == char.class)
                        return Arrays.equals((char[])lhs, (char[])rhs);
                    if (lCompType == double.class)
                        return Arrays.equals((double[])lhs, (double[])rhs);
                    if (lCompType == float.class)
                        return Arrays.equals((float[])lhs, (float[])rhs);
                    if (lCompType == long.class)
                        return Arrays.equals((long[])lhs, (long[])rhs);
                    if (lCompType == short.class)
                        return Arrays.equals((short[])lhs, (short[])rhs);
                }
                return false;
            }
            if (rCompType.isPrimitive())
                return false;
            return Arrays.equals((Object[])lhs, (Object[])rhs);
        }
        return lhs.equals(rhs);
    }
}
