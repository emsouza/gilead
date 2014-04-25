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
package net.sf.beanlib.provider;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Map;

import net.sf.beanlib.BeanGetter;
import net.sf.beanlib.BeanlibException;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.log4j.Logger;

/**
 * Bean Checker.
 * 
 * @author Joe D. Velopar
 */
public class BeanChecker {
    /** Singleton instance. */
//    private static final boolean debug = false;
    private final Logger log = Logger.getLogger(this.getClass());
    private final BeanGetter beanGetter = new BeanGetter();

    /** 
     * Returns true if the fromBean is empty; or false otherwise.
     * It is considered empty if every publicly declared getter methods of the specified class 
     * either return null or blank string.
     * 
     * @param fromBean java bean to check if empty or not.
     * @param k specified class name from which the getter methods are derived. 
     */
    public boolean empty(Object fromBean, Class<?> k) {
        Class<?> fromClass = fromBean.getClass();
        Method[] ma = k.getDeclaredMethods();
        try {
            // invoking all declaring getter methods of fromBean
            for (int i = 0; i < ma.length; i++) {
                Method m = ma[i];
                String name = m.getName();
                try {
                    if (name.startsWith("get")
                        && Modifier.isPublic(m.getModifiers())) {
                        Method getter = fromClass.getMethod(name);
                        Object attrValue = getter.invoke(fromBean);

                        if (attrValue == null) {
                            continue;
                        }
                        if (attrValue instanceof String) {
                            String s = (String) attrValue;
                            s = s.trim();

                            if (s.length() == 0) {
                                continue;
                            }
                        }
                        return false;
                    }
                } catch (NoSuchMethodException ignore) {
                    // ignore and proceed to the next method.
                }
            }
            return true;
        } catch (IllegalAccessException e) {
            log.error("", e);
            throw new BeanlibException(e);
        } catch (SecurityException e) {
            log.error("", e);
            throw new BeanlibException(e);
        } catch (InvocationTargetException e) {
            log.error("", e.getTargetException());
            throw new BeanlibException(e.getTargetException());
        }
    }
    /** 
     * @param fBean from bean
     * @param tBean to bean
     * @return true if the two beans are equal in a JavaBean sense.
     * ie. if they have the same number of properties, 
     * and the properties that can be read contain the same values.
     * 
     * TODO: unit test me
     */
    public boolean beanEquals(Object fBean, Object tBean) {
        if (fBean == tBean)
            return true;
        if (fBean == null || tBean == null)
            return false;
        try {
            BeanInfo bi_f = Introspector.getBeanInfo(fBean.getClass());
            PropertyDescriptor[] pda_f = bi_f.getPropertyDescriptors();

            Map<?,?> tMap = beanGetter.getPropertyName2DescriptorMap(tBean.getClass());

            if (pda_f.length != tMap.size())
                return false;

            for (int i = pda_f.length - 1; i > -1; i--) {
                PropertyDescriptor pd_f = pda_f[i];
                PropertyDescriptor pd_t =
                    (PropertyDescriptor) tMap.get(pd_f.getName());
                Method m_f = pd_f.getReadMethod();
                Method m_t = pd_t.getReadMethod();

                if (m_f == null) {
                    if (m_t == null)
                        continue;
                    return false;
                }
                if (m_t == null)
                    return false;
                Object v_f = m_f.invoke(fBean);
                Object v_t = m_t.invoke(tBean);

                if (!new EqualsBuilder().append(v_f, v_t).isEquals())
                    return false;
            }
            return true;
        } catch (IntrospectionException e) {
            log.error("", e);
            throw new BeanlibException(e);
        } catch (IllegalAccessException e) {
            log.error("", e);
            throw new BeanlibException(e);
        } catch (InvocationTargetException e) {
            log.error("", e.getTargetException());
            throw new BeanlibException(e.getTargetException());
        }
    }
}