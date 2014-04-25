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

import java.util.Currency;

/**
 * Class Utilities.
 * 
 * @author Joe D. Velopar
 */
public enum ClassUtils {
    ;
    /** copied from the private static Class.ENUM constant. */
    private static final int ENUM      = 0x00004000;
    /** Returns the unqalified class name. */
    public static String unqualify(Class<?> c) {
        if (c == null)
            return null;
        String fqcn = c.getName();
        int idx = fqcn.lastIndexOf('.');
        return idx == -1 ? fqcn : fqcn.substring(idx+1);
    }
    
    /** 
     * Returns true if the given class is known to be immutable; false otherwise. 
     */
    public static boolean immutable(Class<?> c) {
        if (c == null)
            return false;
        return c == String.class
            || c.isPrimitive()
            || (c.getModifiers() & ENUM) != 0
            || Number.class.isAssignableFrom(c) && isJavaPackage(c)
            || Boolean.class == c
            || Character.class == c
            || Byte.class == c
            || Currency.class == c
            ;
    }
    
    /**
     * Returns true if the given class is under a package that starts with "java.". 
     */
    public static boolean isJavaPackage(Class<?> c) {
        return c != null && fqcn(c).startsWith("java.");
    }

    /**
     * Returns true if the given class is under a package that starts with "org.hibernate.". 
     */
    public static boolean isHibernatePackage(Class<?> c) {
        return c != null && fqcn(c).startsWith("org.hibernate.");
    }
    
    /** 
     * Returns the fully qualified class name of the given class
     * but without any array prefix such as "[L";
     * or null if the given class is null;
     * or an empty string if the given class is a primitive array 
     * which may be single or multiple dimensional.
     */
    public static String fqcn(Class<?> c) {
        if (c == null)
            return null;
        String cn = c.getName();
        
        if (c.isArray()) {
            int idx = cn.lastIndexOf("[");
            return idx + 2 < cn.length()
                 ? cn.substring(idx + 2)
                 : ""
                 ;
        }
        return cn;
    }
}
