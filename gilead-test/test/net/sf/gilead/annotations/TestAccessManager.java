/**
 * 
 */
package net.sf.gilead.annotations;

import net.sf.gilead.core.annotations.AccessManager;

/**
 * Role based test access manager.
 * 
 * @author bruno.marchesson
 */
public class TestAccessManager implements AccessManager {
    // ----
    // Enum
    // ----
    /**
     * The role enum
     * 
     * @author bruno.marchesson
     */
    public enum Role {
        user,
        admin
    }

    // ----
    // Attribute
    // ----
    /**
     * The current role.
     */
    protected Role role;

    // ----
    // Properties
    // ----
    /**
     * @return the role
     */
    public Role getRole() {
        return role;
    }

    /**
     * @param role the role to set
     */
    public void setRole(Role roletoSet) {
        role = roletoSet;
    }

    // -------------------------------------------------------------------------
    //
    // IAccessManager implementation
    //
    // -------------------------------------------------------------------------
    /*
     * (non-Javadoc)
     * @see net.sf.gilead.annotations.IAccessManager#isAnnotationActive(java.lang.Class, java.lang.Object,
     * java.lang.String)
     */
    @Override
    public Class<?> getActiveAnnotation(Object entity, String propertyName) {
        if (role == Role.admin) {
            return null;
        } else {
            return ReadOnly.class;
        }
    }
}
