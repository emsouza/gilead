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
package net.sf.beanlib.hibernate;

import net.sf.cglib.proxy.Enhancer;

import org.apache.log4j.Logger;
import org.hibernate.HibernateException;
import org.hibernate.proxy.HibernateProxy;
import org.hibernate.proxy.LazyInitializer;

/**
 * @author Joe D. Velopar
 * @author Hanson Char
 */
public class UnEnhancer
{
    private static ThreadLocal<Boolean> perThreadCheckCGLib;
    
    private static class LazyHolder {
        private static final ThreadLocal<Boolean> perThreadCheckCGLib = new ThreadLocal<Boolean>();
    }
    
    private static volatile boolean defaultCheckCGLib = true;

    private UnEnhancer() {}
    
    private static final String JAVASSIST_STARTWITH = "org.javassist.tmp."; 
    private static final String JAVASSIST_INDEXOF = "_$$_javassist_";

    /** 
     * Returns true if the given class is found to be a javassist enhanced class; 
     * false otherwise. 
     */ 
    private static boolean isJavassistEnhanced(Class<?> c) {
        String className = c.getName();
        // pattern found in javassist 3.4 and 3.6's ProxyFactory 
        return className.startsWith(JAVASSIST_STARTWITH)
            || className.indexOf(JAVASSIST_INDEXOF) != -1
            ;
    }
    
    /**
     * Digs out the pre CGLIB/Javassist enhanced class, if any.
     */
    public static <T> Class<T> unenhanceClass(Class<?> c) 
    {
        boolean enhanced = true;
        
        while (c != null && enhanced)
        {
            enhanced =  isCheckCGLib() && Enhancer.isEnhanced(c)
                     || isJavassistEnhanced(c)
                     ;
            if (enhanced)
                c = c.getSuperclass();
        }
        
        @SuppressWarnings("unchecked") Class<T> ret = (Class<T>)c;
        return ret;
    }
    
    public static <T> Class<T> getActualClass(Object object) 
    {
        Class<?> c = object.getClass();
        boolean enhanced = true;
        
        while (c != null && enhanced)
        {
            enhanced =  isCheckCGLib() && Enhancer.isEnhanced(c)
                     || isJavassistEnhanced(c)
                     ;
            if (enhanced) 
            {
                if (object instanceof HibernateProxy) 
                {
                    HibernateProxy hibernateProxy = (HibernateProxy)object; 
                    LazyInitializer lazyInitializer = hibernateProxy.getHibernateLazyInitializer();
                    try {
                        // suggested by Chris (harris3@sourceforge.net)
                        Object impl = lazyInitializer.getImplementation();
                        
                        if (impl != null) {
                            @SuppressWarnings("unchecked") Class<T> ret = (Class<T>)impl.getClass();
                            return ret;
                        }
                    } catch(HibernateException ex) {
                        Logger.getLogger(UnEnhancer.class).warn("Unable to retrieve the underlying persistent object", ex);
                    }
                    @SuppressWarnings("unchecked") Class<T> ret = lazyInitializer.getPersistentClass();
                    return ret;
                }
                c = c.getSuperclass();
            }
        }
        @SuppressWarnings("unchecked") Class<T> ret = (Class<T>)c;
        return ret;
    }
    
    public static <T> T unenhanceObject(T object) {
        if (object instanceof HibernateProxy) 
        {
            HibernateProxy hibernateProxy = (HibernateProxy)object; 
            LazyInitializer lazyInitializer = hibernateProxy.getHibernateLazyInitializer();
            
            @SuppressWarnings("unchecked") 
            T ret = (T)lazyInitializer.getImplementation();
            return ret;
        }
        return object;
    }
    
    /**
     * Returns true if CGLib enhanced classes are to be checked by default; false otherwise.
     */
    public static boolean isDefaultCheckCGLib() { return defaultCheckCGLib; }
    
    /**
     * Returns true if CGLib enhanced classes are to be checked; false otherwise.
     * Note the per thread setting via {@link UnEnhancer#setCheckCGLibForThisThread(boolean)}
     * overrides the default setting, which can be changed via {@link #setDefaultCheckCGLib(boolean)}.
     */
    public static boolean isCheckCGLib() {
        if (perThreadCheckCGLib == null)
            return defaultCheckCGLib;
        final Boolean isCheckCGLib = perThreadCheckCGLib.get();
        return isCheckCGLib == null ? defaultCheckCGLib : isCheckCGLib.booleanValue();
    }

    /** Changes the default for whether CGLib enhanced classes are to be checked or not. */
    public static void setDefaultCheckCGLib(boolean defaultCheckCGLib) {
        UnEnhancer.defaultCheckCGLib = defaultCheckCGLib;
    }

    /** 
     * Changes whether CGLib enhanced classes are to be checked or not for the current thread.
     * Don't forget to invoke {@link #clearThreadLocal()} subsequently in a finally clause.
     * Otherwise, it is a memory leak.
     */
    public static void setCheckCGLibForThisThread(boolean isCheckCGLib) {
        perThreadCheckCGLib = LazyHolder.perThreadCheckCGLib;
        perThreadCheckCGLib.set(isCheckCGLib);
    }
    
    /**
     * Clears the thread local; necessary only if {@link UnEnhancer#setCheckCGLibForThisThread(boolean)}
     * was invoked.
     * @see #setCheckCGLibForThisThread(boolean)
     */
    public static void clearThreadLocal() {
        if (perThreadCheckCGLib != null)
            perThreadCheckCGLib.remove();
    }
}
