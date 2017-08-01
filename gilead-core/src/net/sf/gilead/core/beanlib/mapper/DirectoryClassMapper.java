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

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.sf.gilead.core.beanlib.ClassMapper;

/**
 * Class mapper based on package hierarchy (Domain and DTO must have the same name and placed in identified packages)
 *
 * @author bruno.marchesson
 */
public class DirectoryClassMapper implements ClassMapper {

    private static final Logger LOGGER = LoggerFactory.getLogger(DirectoryClassMapper.class);

    /**
     * The root package of all Domain classes
     */
    private String rootDomainPackage;

    /**
     * The root package of all clone classes
     */
    private String rootClonePackage;

    /**
     * Suffix for clone classes (can be null)
     */
    private String cloneSuffix;

    /**
     * The map of the source and target correspondance
     */
    private Map<Class<?>, Class<?>> sourceTargetMap;

    /**
     * Inverse map of the source and target correspondance
     */
    private Map<Class<?>, Class<?>> targetSourceMap;

    /**
     * @return the _rootClonePackage
     */
    public String getRootClonePackage() {
        return rootClonePackage;
    }

    /**
     * @param clonePackage the _rootClonePackage to set
     */
    public void setRootClonePackage(String clonePackage) {
        rootClonePackage = clonePackage;
    }

    /**
     * @return the _rootDomainPackage
     */
    public String getRootDomainPackage() {
        return rootDomainPackage;
    }

    /**
     * @param domainPackage the _rootDomainPackage to set
     */
    public void setRootDomainPackage(String domainPackage) {
        rootDomainPackage = domainPackage;
    }

    /**
     * @return the cloneSuffix
     */
    public String getCloneSuffix() {
        return cloneSuffix;
    }

    /**
     * @param suffix the cloneSuffix to set
     */
    public void setCloneSuffix(String suffix) {
        cloneSuffix = suffix;
    }

    /**
     * Constructor
     */
    public DirectoryClassMapper() {
        sourceTargetMap = new HashMap<Class<?>, Class<?>>();
        targetSourceMap = new HashMap<Class<?>, Class<?>>();
    }

    @Override
    public Class<?> getTargetClass(Class<?> sourceClass) {
        // Precondition checking
        if (sourceClass == null) {
            return null;
        }

        String sourceClassName = sourceClass.getCanonicalName();
        if (sourceClassName.startsWith(rootDomainPackage) == false) {
            // Not a souce Class<?>
            return null;
        }

        synchronized (this) {
            // Is the correspondance already computed ?
            Class<?> targetClass = sourceTargetMap.get(sourceClass);
            if (targetClass != null) {
                return targetClass;
            }

            // Compute target Class<?> name
            String targetClassName = null;
            String suffix = sourceClassName.substring(rootDomainPackage.length());
            targetClassName = rootClonePackage + suffix;
            if (cloneSuffix != null) {
                // Add clone suffix
                targetClassName += cloneSuffix;
            }

            // Instantiate target Class<?>
            LOGGER.trace("Source Class name is " + sourceClassName);
            LOGGER.trace("Computed target Class name is " + targetClassName);

            try {
                targetClass = Class.forName(targetClassName);
            } catch (ClassNotFoundException e) {
                LOGGER.trace("Target Class does not exist : " + targetClassName, e);
                return null;
            }

            // Add both source and target Class<?> to the clone map
            sourceTargetMap.put(sourceClass, targetClass);
            targetSourceMap.put(targetClass, sourceClass);

            return targetClass;
        }
    }

    @Override
    public Class<?> getSourceClass(Class<?> targetClass) {
        // Precondition checking
        if (targetClass == null) {
            return null;
        }

        // Compute source Class<?> name
        String targetClassName = targetClass.getCanonicalName();
        if ((targetClassName == null) || (targetClassName.startsWith(rootClonePackage) == false)) {
            // Not a target Class<?>
            return null;
        }

        // Suffix verification
        if ((cloneSuffix != null) && (targetClassName.endsWith(cloneSuffix) == false)) {
            // Not a target Class<?>
            return null;
        }

        // Is the correspondance already computed ?
        // work on copy to prevent syncrhnoisation issue
        String cloneSuffix = this.cloneSuffix;
        synchronized (this) {
            Class<?> sourceClass = targetSourceMap.get(targetClass);
            if (sourceClass != null) {
                return sourceClass;
            }

            // Compute source Class<?> name
            String sourceClassName = null;
            String suffix = targetClassName.substring(rootClonePackage.length());
            sourceClassName = rootDomainPackage + suffix;

            if ((cloneSuffix != null) && (sourceClassName.endsWith(cloneSuffix))) {
                // Remove clone suffix
                sourceClassName = sourceClassName.substring(0, sourceClassName.length() - cloneSuffix.length());
            }

            // Instantiate target Class<?>
            LOGGER.trace("Target Class name is " + targetClassName);
            LOGGER.trace("Computed source Class name is " + sourceClassName);

            try {
                sourceClass = Class.forName(sourceClassName);
            } catch (ClassNotFoundException e) {
                LOGGER.trace("Source Class does not exist : " + sourceClassName, e);
                return null;
            }

            // Add both source and target class to the clone map
            targetSourceMap.put(targetClass, sourceClass);
            sourceTargetMap.put(sourceClass, targetClass);

            return sourceClass;
        }
    }
}
