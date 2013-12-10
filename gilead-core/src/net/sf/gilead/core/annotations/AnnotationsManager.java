package net.sf.gilead.core.annotations;

import java.beans.BeanInfo;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import net.sf.gilead.annotations.LimitedAccess;
import net.sf.gilead.annotations.ReadOnly;
import net.sf.gilead.annotations.ServerOnly;
import net.sf.gilead.util.IntrospectionHelper;

/**
 * Helper class for annotation management
 * 
 * @author bruno.marchesson
 */
public class AnnotationsManager {
    // ----
    // Attributes
    // ----
    /**
     * Logger channel
     */
    private static Logger _log = Logger.getLogger(AnnotationsManager.class.getSimpleName());

    /**
     * Annotation map. It is filled with associated Gilead annotation for all classes and properties for performance
     * purpose (computing it each time is very expensive)
     */
    private static Map<Class<?>, Map<String, Class<?>>> _annotationMap = new HashMap<Class<?>, Map<String, Class<?>>>();

    /**
     * The associated access manager
     */
    private static IAccessManager _accessManager;

    // ----
    // Properties
    // ----
    /**
     * @param accessManager the access Manager to use for @LimitedAccess properties
     */
    public static void setAccessManager(IAccessManager accessManager) {
        _accessManager = accessManager;
    }

    /**
     * @return the access Manager
     */
    public static IAccessManager getAccessManager() {
        return _accessManager;
    }

    // -------------------------------------------------------------------------
    //
    // Static helpers
    //
    // -------------------------------------------------------------------------
    /**
     * Indicated if the argument has "ServerOnly" active annotation on the named field.
     */
    public static boolean isServerOnly(Object entity, String propertyName) {
        return isAnnotedProperty(entity, propertyName, ServerOnly.class);
    }

    /**
     * Indicated if the argument has "ReadOnly" active annotation on the named field.
     */
    public static boolean isReadOnly(Object entity, String propertyName) {
        return isAnnotedProperty(entity, propertyName, ReadOnly.class);
    }

    /**
     * Indicated if the argument has "ServerOnly" or "ReadOnly" active annotation on the named field.
     */
    public static boolean isServerOrReadOnly(Object entity, String propertyName) {
        return isAnnotedProperty(entity, propertyName, null);
    }

    /**
     * Indicated if the argument entity has "ServerOnly" active annotation on one of its field.
     */
    public static boolean hasServerOnlyAnnotations(Class<?> entityClass) {
        return isAnnotedClass(entityClass, ServerOnly.class);
    }

    /**
     * Indicated if the argument has "ServerOnly" annotation on one of its field.
     */
    public static boolean hasGileadAnnotations(Class<?> entityClass) {
        // Search all Gilead annotations
        //
        return isAnnotedClass(entityClass, null);
    }

    /**
     * Indicated if the argument has "ReadOnly" annotation on one of its field.
     */
    public static boolean hasReadOnlyAnnotations(Class<?> entityClass) {
        return isAnnotedClass(entityClass, ReadOnly.class);
    }

    // -------------------------------------------------------------------------
    //
    // Internal methods
    //
    // --------------------------------------------------------------------------
    /**
     * Indicated if the argument has "ReadOnly" annotation on one of its field.
     */
    private static Map<String, Class<?>> getGileadAnnotations(Class<?> clazz) {
        _log.log(Level.FINE, "Looking for Gilead annotations for " + clazz);

        Map<String, Class<?>> result = new HashMap<String, Class<?>>();

        // Search annotations on fields
        //
        try {
            Field[] fields = IntrospectionHelper.getRecursiveDeclaredFields(clazz);
            for (Field field : fields) {
                String propertyName = field.getName();

                // ReadOnly
                ReadOnly readOnly = field.getAnnotation(ReadOnly.class);
                if (readOnly != null) {
                    _log.log(Level.FINE, propertyName + " member has @ReadOnly");
                    result.put(propertyName, ReadOnly.class);
                    continue;
                }

                // ServerOnly
                ServerOnly serverOnly = field.getAnnotation(ServerOnly.class);
                if (serverOnly != null) {
                    _log.log(Level.FINE, propertyName + " member has @ServerOnly");
                    result.put(propertyName, ServerOnly.class);
                    continue;
                }

                // LimitedAccess
                LimitedAccess limitedAccess = field.getAnnotation(LimitedAccess.class);
                if (limitedAccess != null) {
                    _log.log(Level.FINE, propertyName + " member has @LimitedAccess");
                    result.put(propertyName, LimitedAccess.class);
                    continue;
                }

                // No Gilead annotation
                _log.log(Level.FINE, propertyName + " member has no Gilead annotation");

                result.put(propertyName, null);
            }

            // Search annotation on getters
            //
            BeanInfo info = Introspector.getBeanInfo(clazz);
            PropertyDescriptor[] descriptors = info.getPropertyDescriptors();
            for (PropertyDescriptor descriptor : descriptors) {
                if ((descriptor != null) && (descriptor.getReadMethod() != null)) {
                    String propertyName = descriptor.getName();

                    // If annotation has been detected on field, no need to
                    // search on getter
                    //
                    if (result.get(propertyName) != null) {
                        continue;
                    }

                    // ReadOnly
                    //
                    ReadOnly readOnly = descriptor.getReadMethod().getAnnotation(ReadOnly.class);
                    if (readOnly != null) {
                        _log.log(Level.FINE, propertyName + " getter has @ReadOnly");
                        result.put(propertyName, ReadOnly.class);
                        continue;
                    }

                    // ServerOnly
                    //
                    ServerOnly serverOnly = descriptor.getReadMethod().getAnnotation(ServerOnly.class);
                    if (serverOnly != null) {
                        _log.log(Level.FINE, propertyName + " getter has @ServerOnly");
                        result.put(propertyName, ServerOnly.class);
                    }

                    // LimitedAccess
                    //
                    LimitedAccess limitedAccess = descriptor.getReadMethod().getAnnotation(LimitedAccess.class);
                    if (limitedAccess != null) {
                        _log.log(Level.FINE, propertyName + " getter has @LimitedAccess");
                        result.put(propertyName, LimitedAccess.class);
                    }

                }
            }
        } catch (Exception e) {
            throw new RuntimeException("Error inspecting class " + clazz, e);
        }

        // Return annotation map
        //
        return result;
    }

    /**
     * Indicated if the argument has the target Gilead annotation on the named field.
     */
    private static boolean isAnnotedProperty(Object entity, String propertyName, Class<?> annotationClass) {
        // Map checking
        //
        Class<?> clazz = entity.getClass();
        Map<String, Class<?>> propertyAnnotations = _annotationMap.get(clazz);
        if (propertyAnnotations == null) {
            // Compute property annotations
            //
            propertyAnnotations = getGileadAnnotations(clazz);
            synchronized (_annotationMap) {
                _annotationMap.put(clazz, propertyAnnotations);
            }
        }

        // Does the map contains the target annotation for the argument property
        // ?
        //
        Class<?> annotation = propertyAnnotations.get(propertyName);

        // Limited access ?
        //
        if (LimitedAccess.class.equals(annotation)) {
            // Call access manager
            //
            if (_accessManager == null) {
                _log.log(Level.WARNING, "No Access Manager defined whereas @LimitedAccess annotation is used !");
            } else {
                annotation = _accessManager.getActiveAnnotation(entity, propertyName);
            }
        }

        // Annotation checking
        //
        if (annotation == null) {
            return false;
        }

        if ((annotationClass != null) && (annotationClass.equals(annotation) == false)) {
            return false;
        } else {
            return true;
        }
    }

    /**
     * Indicated if the argument entity has "ServerOnly" active annotation on one of its field.
     */
    private static boolean isAnnotedClass(Class<?> entityClass, Class<?> annotationClass) {
        // Map checking
        //
        Map<String, Class<?>> propertyAnnotations = _annotationMap.get(entityClass);
        if (propertyAnnotations == null) {
            // Compute property annotations
            //
            propertyAnnotations = getGileadAnnotations(entityClass);
            synchronized (_annotationMap) {
                _annotationMap.put(entityClass, propertyAnnotations);
            }
        }

        // Does the map contains @ServerOnly annotation ?
        //
        if (propertyAnnotations != null) {
            for (Map.Entry<String, Class<?>> entry : propertyAnnotations.entrySet()) {
                Class<?> annotation = entry.getValue();

                if (LimitedAccess.class.equals(annotation)) {
                    // Consider that it can be ServerOnly or ReadOnly...
                    //
                    return true;
                }

                if (annotation != null) {
                    if ((annotationClass == null) || (annotationClass.equals(annotation) == true)) {
                        // Found
                        //
                        return true;
                    }
                }
            }
        }
        return false;
    }
}