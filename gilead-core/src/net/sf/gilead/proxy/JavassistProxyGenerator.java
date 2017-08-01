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

package net.sf.gilead.proxy;

import org.apache.commons.lang3.ClassUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javassist.CannotCompileException;
import javassist.ClassClassPath;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtConstructor;
import javassist.CtField;
import javassist.CtMethod;
import javassist.CtNewConstructor;
import javassist.CtNewMethod;
import net.sf.gilead.exception.ProxyException;
import net.sf.gilead.proxy.xml.AdditionalCode;
import net.sf.gilead.proxy.xml.Attribute;
import net.sf.gilead.proxy.xml.Constructor;
import net.sf.gilead.proxy.xml.Method;

/**
 * Javassist proxy generator (for server side)
 *
 * @author bruno.marchesson
 */
public class JavassistProxyGenerator implements ServerProxyGenerator {

    private static final Logger LOGGER = LoggerFactory.getLogger(JavassistProxyGenerator.class);

    @Override
    public Class<?> generateProxyFor(Class<?> superClass, AdditionalCode additionalCode) {
        try {
            // Compute proxy class name
            String sourceClassName = superClass.getName();
            String proxyClassName = sourceClassName + additionalCode.getSuffix();
            LOGGER.trace("Generating server proxy " + proxyClassName + " for class " + sourceClassName);

            // Create proxy class
            ClassPool pool = ClassPool.getDefault();

            // TOMCAT and JBOSS classloader handling
            pool.insertClassPath(new ClassClassPath(superClass));

            CtClass proxyClass = pool.makeClass(proxyClassName);

            // Add proxy inheritance
            proxyClass.setSuperclass(pool.get(sourceClassName));

            // Add ILightEntity inheritance
            if (additionalCode.getImplementedInterface() != null) {
                proxyClass.addInterface(pool.get(additionalCode.getImplementedInterface()));
            }

            // generate Proxy
            generateProxy(proxyClass, additionalCode);

            // Generate class
            return proxyClass.toClass(superClass.getClassLoader(), superClass.getProtectionDomain());
        } catch (Exception ex) {
            throw new ProxyException("Proxy generation failure for " + superClass.getName(), ex);
        }
    }

    /**
     * Generates ILightEntity classes and methods
     *
     * @throws CannotCompileException
     */
    private void generateProxy(CtClass proxyClass, AdditionalCode additionalCode) throws CannotCompileException {
        // Generate attributes if needed
        if (additionalCode.getAttributes() != null) {
            for (Attribute attribute : additionalCode.getAttributes()) {
                generateAttribute(proxyClass, attribute);
            }
        }

        // Generate constructors if needed
        if (additionalCode.getConstructors() != null) {
            for (Constructor constructor : additionalCode.getConstructors()) {
                generateConstructor(proxyClass, constructor);
            }
        }

        // Generate methods if needed
        if (additionalCode.getMethods() != null) {
            for (Method method : additionalCode.getMethods()) {
                generateMethod(proxyClass, method);
            }
        }
    }

    /**
     * Generate an additional attribute
     *
     * @param proxyClass
     * @param attribute
     * @throws CannotCompileException
     */
    protected void generateAttribute(CtClass proxyClass, Attribute attribute) throws CannotCompileException {
        CtField field = CtField.make(attribute.toJava14String(), proxyClass);
        proxyClass.addField(field);
    }

    /**
     * Generate additional method to the instrumented class
     *
     * @param proxyClass
     * @param method
     * @throws CannotCompileException
     */
    private void generateMethod(CtClass proxyClass, Method method) throws CannotCompileException {
        // Source code
        StringBuffer sourceCode = new StringBuffer();
        sourceCode.append(method.computeJava14Signature());
        sourceCode.append(method.getCode());

        // Add method body
        CtMethod ctMethod = CtNewMethod.make(sourceCode.toString(), proxyClass);
        proxyClass.addMethod(ctMethod);
    }

    /**
     * Generate additional constructor to the instrumented class
     *
     * @param proxyClass
     * @param method
     * @throws CannotCompileException
     */
    private void generateConstructor(CtClass proxyClass, Constructor constructor) throws CannotCompileException {
        // Source code
        StringBuffer sourceCode = new StringBuffer();
        sourceCode.append(constructor.computeJava14Signature(ClassUtils.getShortClassName(proxyClass.getName())));
        sourceCode.append(constructor.getCode());

        // Add method body
        CtConstructor ctConstructor = CtNewConstructor.make(sourceCode.toString(), proxyClass);
        proxyClass.addConstructor(ctConstructor);
    }
}
