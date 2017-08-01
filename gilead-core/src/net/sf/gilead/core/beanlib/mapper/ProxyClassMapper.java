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
package net.sf.gilead.core.beanlib.mapper;

import net.sf.gilead.core.PersistenceUtil;
import net.sf.gilead.core.beanlib.ClassMapper;
import net.sf.gilead.proxy.AdditionalCodeManager;
import net.sf.gilead.proxy.ProxyManager;
import net.sf.gilead.proxy.xml.AdditionalCode;

/**
 * Class mapper for domain and proxy
 *
 * @author bruno.marchesson
 */
public class ProxyClassMapper implements ClassMapper {

    /**
     * The associated persistence util
     */
    protected PersistenceUtil persistenceUtil;

    /**
     * For newly created proxy, must we use Java5 or Java 1.4 generator
     */
    protected boolean java5 = true;

    /**
     * @return the associated Persistence Util
     */
    public PersistenceUtil getPersistenceUtil() {
        return persistenceUtil;
    }

    /**
     * @param persistenceUtil the persistenceUtil to set
     */
    public void setPersistenceUtil(PersistenceUtil persistenceUtil) {
        this.persistenceUtil = persistenceUtil;
    }

    /**
     * @return is Java5 used for proxy generation ?
     */
    public boolean isJava5() {
        return java5;
    }

    /**
     * @param java5 must Java5 be used for proxy generation ?
     */
    public void setJava5(boolean java5) {
        this.java5 = java5;
    }

    @Override
    public Class<?> getSourceClass(Class<?> targetClass) {
        return ProxyManager.getInstance().getSourceClass(targetClass);
    }

    @Override
    public Class<?> getTargetClass(Class<?> sourceClass) {
        Class<?> proxyClass = ProxyManager.getInstance().getProxyClass(sourceClass);

        if (proxyClass == null) {
            // Force proxy generation for persistent class
            if (persistenceUtil == null) {
                throw new RuntimeException("Missing PersistenceUtil in ProxyClassMapper : please fill this member...");
            }
            if (persistenceUtil.isPersistentClass(sourceClass)) {
                AdditionalCode additionalCode;
                if (java5 == true) {
                    additionalCode = AdditionalCodeManager.getInstance().getAdditionalCode(ProxyManager.JAVA_5_LAZY_POJO);
                } else {
                    additionalCode = AdditionalCodeManager.getInstance().getAdditionalCode(ProxyManager.JAVA_14_LAZY_POJO);
                }

                proxyClass = ProxyManager.getInstance().generateProxyClass(sourceClass, additionalCode);
            }
        }

        return proxyClass;
    }
}
