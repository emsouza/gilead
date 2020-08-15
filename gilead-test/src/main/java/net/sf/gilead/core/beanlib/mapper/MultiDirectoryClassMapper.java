package net.sf.gilead.core.beanlib.mapper;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.sf.gilead.core.beanlib.ClassMapper;

/**
 * Class mapper based on package hierarchy (Domain and DTO must have the same name and placed in identified packages).
 * This class mapper accepts multiple distinct domain and dto packages.
 *
 * @author Olaf Kock, Florian Siebert
 */
public class MultiDirectoryClassMapper implements ClassMapper {

    private static final Logger LOGGER = LoggerFactory.getLogger(MultiDirectoryClassMapper.class);

    /**
     * Mapping from domain packages to target class packages
     */
    private Map<String, String> sourcePackageMap = new HashMap<>();

    /**
     * Reverse mapping from target packages to domain packages
     */
    private Map<String, String> targetPackageMap = new HashMap<>();

    /**
     * The suffix for all target class names (e.g. "DTO")
     */
    private String targetSuffix = "";

    /**
     * The map of the clone and domain correspondance
     */
    private Map<Class<?>, Class<?>> associationMap;

    /**
     * Constructor
     */
    public MultiDirectoryClassMapper() {
        this.associationMap = new HashMap<>();
    }

    /**
     * @return the suffix of all target classes
     */
    public String getTargetSuffix() {
        return targetSuffix;
    }

    /**
     * @param suffix the suffix of all target classes (e.g. "DTO")
     */
    public void setTargetSuffix(String suffix) {
        targetSuffix = suffix;
    }

    /**
     * associate a source package (hibernated domain classes) with a target package (gwt serializeable classes)
     *
     * @param source the package containing the domain classes
     * @param target the package containing the gwt serializeable classes
     */

    public void addMapping(Package source, Package target) {
        sourcePackageMap.put(source.getName(), target.getName());
        targetPackageMap.put(target.getName(), source.getName());
    }

    @Override
    public Class<?> getTargetClass(Class<?> sourceClass) {
        if (sourceClass == null) {
            return null;
        }

        Class<?> targetClass = getCachedAssociation(sourceClass);
        if (targetClass != null) {
            return targetClass;
        }

        String targetClassName = computeTargetClassName(sourceClass);

        try {
            targetClass = Class.forName(targetClassName);
        } catch (ClassNotFoundException e) {
            LOGGER.error("Target class does not exist : " + targetClassName, e);
            return null;
        }

        cacheAssociation(sourceClass, targetClass);
        return targetClass;
    }

    @Override
    public Class<?> getSourceClass(Class<?> targetClass) {
        if (targetClass == null) {
            return null;
        }

        Class<?> sourceClass = getCachedAssociation(targetClass);
        if (sourceClass != null) {
            return sourceClass;
        }

        String sourceClassName = computeSourceClassName(targetClass);
        if (sourceClassName == null) {
            return null;
        }

        try {
            sourceClass = Class.forName(sourceClassName);
        } catch (ClassNotFoundException e) {
            LOGGER.error("Source class does not exist : " + sourceClassName, e);
            return null;
        }

        cacheAssociation(targetClass, sourceClass);
        return sourceClass;
    }

    void cacheAssociation(Class<?> targetClass, Class<?> sourceClass) {
        associationMap.put(targetClass, sourceClass);
        associationMap.put(sourceClass, targetClass);
    }

    Class<?> getCachedAssociation(Class<?> sourceClass) {
        return associationMap.get(sourceClass);
    }

    private String computeTargetClassName(Class<?> sourceClass) {
        String sourcePackage = sourceClass.getPackage().getName();
        String targetPackage = sourcePackageMap.get(sourcePackage);
        if (targetPackage == null) {
            return null;
        }

        String strippedClassName = sourceClass.getCanonicalName().substring(sourcePackage.length());
        assert sourceClass.getCanonicalName().startsWith(strippedClassName);
        String targetClassName = targetPackage + strippedClassName + targetSuffix;

        LOGGER.debug("Source class name is " + sourceClass.getCanonicalName() + ", target class is " + targetClassName);
        return targetClassName;
    }

    private String computeSourceClassName(Class<?> targetClass) {
        String targetClassName = targetClass.getCanonicalName();
        if (!targetClassName.endsWith(targetSuffix)) {
            LOGGER.error("target class " + targetClassName + " does not end with expected suffix '" + targetSuffix + "'");
            // might as well throw IllegalArgumentException
            return null;
        }
        String targetPackage = targetClass.getPackage().getName();
        String strippedTargetClassName = targetClassName.substring(targetPackage.length());

        String sourcePackageName = targetPackageMap.get(targetPackage);
        if (sourcePackageName == null) {
            return null;
        }
        String strippedSourceClassName = strippedTargetClassName.substring(0, strippedTargetClassName.length() - targetSuffix.length());
        String sourceClassName = sourcePackageName + strippedSourceClassName;

        LOGGER.debug("Source class for target " + targetClassName + " is " + sourceClassName);
        return sourceClassName;
    }
}
