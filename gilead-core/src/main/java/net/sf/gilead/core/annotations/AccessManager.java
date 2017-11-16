package net.sf.gilead.core.annotations;

/**
 * Interface of transport annotation access manager
 *
 * @author bruno.marchesson
 */
public interface AccessManager {

    /**
     * Returns the active annotation on the property of the target entity. Can be @ReadOnly, @ServerOnly or null (write
     * access)
     *
     * @param entity
     * @param propertyName
     * @return
     */
    Class<?> getActiveAnnotation(Object entity, String propertyName);
}
