package net.sf.gilead.util;

import java.lang.reflect.Field;
import java.util.Collection;

/**
 * Generic collection helper class
 * 
 * @author bruno.marchesson
 */
public class CollectionHelper {
    /**
     * Indicates if the collection has been wrapped by Collections.unmodifiableCollection(...)
     * 
     * @param collection
     * @return
     */
    public static boolean isUnmodifiableCollection(Object collection) {
        // Precondition checking
        //
        if (collection == null) {
            return false;
        }

        // Get class
        //
        Class<?> collectionClass = collection.getClass();
        return (collectionClass.getName().startsWith("java.util.Collections$Unmodifiable"));
    }

    /**
     * Gets the underlying collection wrapped by Collections.unmodifiableCollection(...)
     * 
     * @param collection
     * @return
     */
    public static Collection<?> getUnmodifiableCollection(Object collection) {
        // Precondition checking
        //
        if (collection == null) {
            return null;
        }

        // Search the underlying field (named 'c')
        //
        Field field = IntrospectionHelper.getRecursiveDeclaredField(collection.getClass(), "c");
        if (field == null) {
            throw new RuntimeException("Unable to find the underlying collection of unmodifiable collection !");
        }

        try {
            field.setAccessible(true);
            return (Collection<?>) field.get(collection);
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }
}
