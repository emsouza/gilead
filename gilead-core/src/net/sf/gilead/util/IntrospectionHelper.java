package net.sf.gilead.util;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Singleton for instrospection search
 *
 * @author bruno.marchesson
 */
public class IntrospectionHelper {

    private static Map<Class<?>, Map<String, Method>> _declaredMethodMap = Collections
            .synchronizedMap(new HashMap<Class<?>, Map<String, Method>>());

    /**
     * Recursively get declared fields
     */
    public static Field[] getRecursiveDeclaredFields(Class<?> clazz) {
        // Create field list
        List<Field> fieldList = Collections.synchronizedList(new ArrayList<Field>());

        // Recursive get superclass declared fields
        while (clazz != null) {
            fieldList.addAll(Arrays.asList(clazz.getDeclaredFields()));
            clazz = clazz.getSuperclass();
        }

        // Convert field list to array
        return fieldList.toArray(new Field[fieldList.size()]);
    }

    /**
     * Recursively find declared field with the argument name
     */
    public static Field getRecursiveDeclaredField(Class<?> clazz, String fieldName) {
        // Recursive get superclass declared fields
        while (clazz != null) {
            try {
                return clazz.getDeclaredField(fieldName);
            } catch (NoSuchFieldException e) {
                // Search in superclass
                clazz = clazz.getSuperclass();
            } catch (SecurityException e) {
                throw new RuntimeException(e.getMessage(), e);
            }

        }

        // not found
        return null;
    }

    /**
     * Recursively find declared method with the argument name Important notice : does not check parameters types since
     * it is used for searching getter and setter !
     */
    public static Method getRecursiveDeclaredMethod(Class<?> clazz, String methodName, Class<?>... parameterTypes) throws NoSuchMethodException {
        // Map checking
        Map<String, Method> methodMap;
        Map<String, Method> temp = _declaredMethodMap.get(clazz);

        if (temp == null) {
            methodMap = Collections.synchronizedMap(new HashMap<String, Method>());
            _declaredMethodMap.put(clazz, methodMap);
        } else {
            methodMap = Collections.synchronizedMap(temp);
        }

        Method method = methodMap.get(methodName);
        if (method != null) {
            // Already computed
            return method;
        }

        // Need to search
        method = searchRecursiveDeclaredMethod(clazz, methodName, parameterTypes);
        methodMap.put(methodName, method);

        return method;
    }

    /**
     * Return the getter for the named property
     *
     * @param clazz
     * @param propertyName
     * @return
     * @throws NoSuchMethodException
     */
    public static Method getReaderMethodForProperty(Class<?> clazz, String propertyName) throws NoSuchMethodException {
        // Compute method name
        String s = propertyName;

        if (Character.isLowerCase(propertyName.charAt(0))) {
            s = propertyName.substring(0, 1).toUpperCase();

            if (propertyName.length() > 1) {
                s += propertyName.substring(1);
            }
        }

        return getRecursiveDeclaredMethod(clazz, "get" + s, (Class[]) null);
    }

    /**
     * Search nested class from the attributes of the argument
     *
     * @param clazz the searched class
     * @param root the root object
     * @return the object if found, null otherwise
     */
    public static Object searchMember(Class<?> clazz, Object root) {
        return searchMember(clazz, root, Collections.synchronizedList(new ArrayList<Object>()));
    }

    /**
     * Search nested class from the attributes of the argument
     *
     * @param clazz the searched class
     * @param root the root object
     * @return the object if found, null otherwise
     */
    private static Object searchMember(Class<?> clazz, Object root, List<Object> alreadyChecked) {
        // Precondition checking
        if ((root == null) || (root.getClass().getName().startsWith("java."))) {
            return null;
        }

        // Already checked ?
        if (alreadyChecked.contains(root) == true) {
            return null;
        }
        alreadyChecked.add(root);

        if (clazz.isInstance(root)) {
            return root;
        }

        // Iterate over fields
        Field[] fields = getRecursiveDeclaredFields(root.getClass());
        for (Field field : fields) {
            // Recursive search
            field.setAccessible(true);
            try {
                Object member = searchMember(clazz, field.get(root), alreadyChecked);
                if (member != null) {
                    return member;
                }
            } catch (Exception e) {
                // Should not happen
                throw new RuntimeException(e);
            }
        }

        // Final verification : is the current class is a proxy ?
        if (root.getClass().getName().endsWith("Proxy")) {
            // Is there a 'getProxy' method ?
            try {
                Method getProxyMethod = getRecursiveDeclaredMethod(root.getClass(), "getProxy", (Class<?>[]) null);
                if (getProxyMethod != null) {
                    try {
                        Object result = getProxyMethod.invoke(root, (Object[]) null);
                        result = searchMember(clazz, result, alreadyChecked);
                        if (result != null) {
                            return result;
                        }
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }

                }
            } catch (NoSuchMethodException e) {
                // No getProxy method, probably not a Spring proxy...
            }
        }

        // Member not found
        return null;
    }

    /**
     * Recursively find declared method with the argument name
     */
    private static Method searchRecursiveDeclaredMethod(Class<?> clazz, String methodName, Class<?>... parameterTypes)
            throws NoSuchMethodException {
        // Recursive get superclass declared fields
        while (clazz != null) {
            try {
                return clazz.getDeclaredMethod(methodName, parameterTypes);
            } catch (NoSuchMethodException ex) {
                // Search in superclass
                clazz = clazz.getSuperclass();
            }
        }

        // Method not found
        throw new NoSuchMethodException(methodName);
    }
}
