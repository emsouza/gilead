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
     * Constructor
     */
    public DirectoryClassMapper() {
        this.sourceTargetMap = new HashMap<>();
        this.targetSourceMap = new HashMap<>();
    }

    /**
     * @return the _rootClonePackage
     */
    public String getRootClonePackage() {
        return rootClonePackage;
    }

    /**
     * @param clonePackage the rootClonePackage to set
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
     * @param domainPackage the rootDomainPackage to set
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
                //
                targetClassName += cloneSuffix;
            }

            // Instantiate target Class<?>
            LOGGER.debug("Source Class name is " + sourceClassName);
            LOGGER.debug("Computed target Class name is " + targetClassName);

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
            //
            return null;
        }

        // Suffix verification
        if ((cloneSuffix != null) && (targetClassName.endsWith(cloneSuffix) == false)) {
            // Not a target Class<?>
            return null;
        }

        // Is the correspondance already computed ? work on copy to prevent syncrhnoisation issue
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
            LOGGER.debug("Target Class name is " + targetClassName);
            LOGGER.debug("Computed source Class name is " + sourceClassName);

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
