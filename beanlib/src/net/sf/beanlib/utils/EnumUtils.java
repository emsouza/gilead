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
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * Enum Utilities.
 * 
 * @author Joe D. Velopar
 */
public class EnumUtils 
{
    /** 
     * Contains a lazily initialized internal map used for implementing fromExternal().
     * 
     * @author Joe D. Velopar
     */
    private static class LazyExternal {
        private static final ConcurrentMap<Class<?>,Map<?,?>> enumExternalDirectory = 
            new ConcurrentHashMap<Class<?>,Map<?,?>>();
    }

    /** 
     * Contains a lazily initialized internal map used for implementing fromString().
     * 
     * @author Joe D. Velopar
     */
    private static class LazyToString {
        private static final ConcurrentMap<Class<?>,Map<?,?>> enumToStringDirectory = 
            new ConcurrentHashMap<Class<?>,Map<?,?>>();
    }

    /** 
     * Contains a lazily initialized internal map used for implementing fromOrdinal().
     * 
     * @author Joe D. Velopar
     */
    private static class LazyOrdinal {
        private static final ConcurrentMap<Class<?>,Map<?,?>> enumOrdinalDirectory = 
            new ConcurrentHashMap<Class<?>,Map<?,?>>();
    }

    /**
     * Returns the enum constant of the specified Enum and External type with the
     * specified externalized object; or null if no such enum constant exists.
     * Note this method may break unless E is immutable.
     */
    public static <T extends Enum<T> & External<E>, E> 
        T fromExternal(Class<T> enumType,  E externalized) 
    {
        return enumConstantDirectory(enumType).get(externalized);
    }

    /**
     * Returns the enum constant of the specified Enum with the
     * specified toString() return value of the enum constant; 
     * or null if no such enum constant exists.
     */
    public static <T extends Enum<T>> 
        T fromString(Class<T> enumType,  String s) 
    {
        return enumStringConstantDirectory(enumType).get(s);
    }

    /**
     * Returns the enum constant of the specified Enum with the
     * specified ordinal() return value of the enum constant; 
     * or null if no such enum constant exists.
     */
    public static <T extends Enum<T>> 
        T fromOrdinal(Class<T> enumType,  int ordinal) 
    {
        return enumOrdinalConstantDirectory(enumType).get(ordinal);
    }
    
    /**
     * Returns a map from the externalized object of type E to enum constant for the specified T, 
     * which has a type of both Enum and External.
     * This private method is used internally to implement 
     * <pre>  
     *     public static &lt;T extends Enum&lt;T&gt; & External&lt;E&gt;&gt; T fromExternal(Class&lt;T&gt;, E)
     * </pre>  
     * efficiently.
     * Note that the map returned by this method is
     * created lazily on first use.  Typically it won't ever get created.
     */
    private static <T extends Enum<T> & External<E>, E> 
        Map<E,T> enumConstantDirectory(Class<T> enumType) 
    {
        @SuppressWarnings("unchecked")
        Map<E,T> constantMap = (Map<E,T>)LazyExternal.enumExternalDirectory.get(enumType);
        
        if (constantMap == null) {
            T[] constants = enumType.getEnumConstants();  // Does unnecessary clone
            constantMap = new HashMap<E,T>(2 * constants.length);
            
            for (T constant : constants)
                constantMap.put(constant.externalize(), constant);
            @SuppressWarnings("unchecked")
            Map<E,T> prev = 
                (Map<E,T>)LazyExternal.enumExternalDirectory
                    .putIfAbsent(enumType, Collections.unmodifiableMap(constantMap));
            
            if (prev != null)
                constantMap = prev; // race condition
        }
        return constantMap;
    }
    
    /**
     * Returns a map from the toString() return value to the enum constant for the specified T, 
     * which is an Enum.
     * This private method is used internally to implement 
     * <pre>  
     *     public static &lt;T extends Enum&lt;T&gt;&gt; T fromString(Class&lt;T&gt;, String)
     * </pre>  
     * efficiently.
     * Note that the map returned by this method is
     * created lazily on first use.  Typically it won't ever get created.
     */
    private static <T extends Enum<T>>
        Map<String,T> enumStringConstantDirectory(Class<T> enumType) 
    {
        @SuppressWarnings("unchecked")
        Map<String,T> constantMap = (Map<String,T>)LazyToString.enumToStringDirectory.get(enumType);
        
        if (constantMap == null) {
            T[] constants = enumType.getEnumConstants();  // Does unnecessary clone
            constantMap = new HashMap<String,T>(2 * constants.length);
            
            for (T constant : constants)
                constantMap.put(constant.toString(), constant);
            @SuppressWarnings("unchecked")
            Map<String,T> prev = 
                (Map<String,T>)LazyToString.enumToStringDirectory
                    .putIfAbsent(enumType, Collections.unmodifiableMap(constantMap));
            
            if (prev != null)
                constantMap = prev; // race condition
        }
        return constantMap;
    }
    
    /**
     * Returns a map from the ordinal() return value to the enum constant for the specified T, 
     * which is an Enum.
     * This private method is used internally to implement 
     * <pre>  
     *     public static &lt;T extends Enum&lt;T&gt;&gt; T fromOrdinal(Class&lt;T&gt;, int)
     * </pre>
     * efficiently.
     * Note that the map returned by this method is
     * created lazily on first use.  Typically it won't ever get created.
     */
    private static <T extends Enum<T>>
        Map<Integer,T> enumOrdinalConstantDirectory(Class<T> enumType) 
    {
        @SuppressWarnings("unchecked")
        Map<Integer,T> constantMap = (Map<Integer,T>)LazyOrdinal.enumOrdinalDirectory.get(enumType);
        
        if (constantMap == null) {
            T[] constants = enumType.getEnumConstants();  // Does unnecessary clone
            constantMap = new HashMap<Integer,T>(2 * constants.length);
            
            for (T constant : constants)
                constantMap.put(constant.ordinal(), constant);
            @SuppressWarnings("unchecked")
            Map<Integer,T> prev = 
                (Map<Integer,T>)LazyOrdinal.enumOrdinalDirectory
                    .putIfAbsent(enumType, Collections.unmodifiableMap(constantMap));
            
            if (prev != null)
                constantMap = prev; // race condition
        }
        return constantMap;
    }
    private EnumUtils() {}
}
