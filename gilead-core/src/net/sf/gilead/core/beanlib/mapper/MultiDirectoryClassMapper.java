package net.sf.gilead.core.beanlib.mapper;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import net.sf.gilead.core.beanlib.IClassMapper;

/**
 * Class mapper based on package hierarchy (Domain and DTO must have the same name and placed in identified packages).
 * This class mapper accepts multiple distinct domain and dto packages.
 * 
 * @author Olaf Kock, Florian Siebert
 */
public class MultiDirectoryClassMapper implements IClassMapper {

    private static Logger _log = Logger.getLogger(MultiDirectoryClassMapper.class.getSimpleName());

    /**
     * Mapping from domain packages to target class packages
     */
    private Map<String, String> _sourcePackageMap = new HashMap<String, String>();

    /**
     * Reverse mapping from target packages to domain packages
     */
    private Map<String, String> _targetPackageMap = new HashMap<String, String>();

    /**
     * The suffix for all target class names (e.g. "DTO")
     */
    private String _targetSuffix = "";

    /**
     * The map of the clone and domain correspondance
     */
    private Map<Class<?>, Class<?>> _associationMap;

    /**
     * Constructor
     */
    public MultiDirectoryClassMapper() {
        _associationMap = new HashMap<Class<?>, Class<?>>();
    }

    /**
     * @return the suffix of all target classes
     */
    public String getTargetSuffix() {
        return _targetSuffix;
    }

    /**
     * @param suffix the suffix of all target classes (e.g. "DTO")
     */
    public void setTargetSuffix(String suffix) {
        _targetSuffix = suffix;
    }

    /**
     * associate a source package (hibernated domain classes) with a target package (gwt serializeable classes)
     * 
     * @param source the package containing the domain classes
     * @param target the package containing the gwt serializeable classes
     */

    public void addMapping(Package source, Package target) {
        _sourcePackageMap.put(source.getName(), target.getName());
        _targetPackageMap.put(target.getName(), source.getName());
    }

    /*
     * (non-Javadoc)
     * @see net.sf.gilead.core.beanlib.java5.ICloneMapper#getTargetClass(java.lang .Class<?>)
     */
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
            _log.log(Level.SEVERE, "Target class does not exist : " + targetClassName, e);
            return null;
        }

        cacheAssociation(sourceClass, targetClass);
        return targetClass;
    }

    /*
     * (non-Javadoc)
     * @see net.sf.gilead.core.beanlib.java5.ICloneMapper#getTargetClass(java.lang .Class<?>)
     */
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
            _log.log(Level.SEVERE, "Source class does not exist : " + sourceClassName, e);
            return null;
        }

        cacheAssociation(targetClass, sourceClass);
        return sourceClass;
    }

    // ================================================================
    // Implementation details - private and package(test)visible stuff

    void cacheAssociation(Class<?> targetClass, Class<?> sourceClass) {
        _associationMap.put(targetClass, sourceClass);
        _associationMap.put(sourceClass, targetClass);
    }

    Class<?> getCachedAssociation(Class<?> sourceClass) {
        return _associationMap.get(sourceClass);
    }

    private String computeTargetClassName(Class<?> sourceClass) {
        String sourcePackage = sourceClass.getPackage().getName();
        String targetPackage = _sourcePackageMap.get(sourcePackage);
        if (targetPackage == null) {
            return null;
        }

        String strippedClassName = sourceClass.getCanonicalName().substring(sourcePackage.length());
        assert sourceClass.getCanonicalName().startsWith(strippedClassName);
        String targetClassName = targetPackage + strippedClassName + _targetSuffix;

        _log.log(Level.INFO, "Source class name is " + sourceClass.getCanonicalName() + ", target class is " + targetClassName);
        return targetClassName;
    }

    private String computeSourceClassName(Class<?> targetClass) {
        String targetClassName = targetClass.getCanonicalName();
        if (!targetClassName.endsWith(_targetSuffix)) {
            _log.log(Level.SEVERE, "target class " + targetClassName + " does not end with expected suffix '" + _targetSuffix + "'");
            // might as well throw IllegalArgumentException
            return null;
        }
        String targetPackage = targetClass.getPackage().getName();
        String strippedTargetClassName = targetClassName.substring(targetPackage.length());

        String sourcePackageName = _targetPackageMap.get(targetPackage);
        if (sourcePackageName == null) {
            return null;
        }
        String strippedSourceClassName = strippedTargetClassName.substring(0, strippedTargetClassName.length() - _targetSuffix.length());
        String sourceClassName = sourcePackageName + strippedSourceClassName;

        _log.log(Level.INFO, "Source class for target " + targetClassName + " is " + sourceClassName);
        return sourceClassName;
    }

}
