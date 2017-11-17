package net.sf.gilead.annotations;

import net.sf.gilead.core.annotations.IAccessManager;

/**
 * Role based test access manager.
 * 
 * @author bruno.marchesson
 */
public class TestAccessManager implements IAccessManager {

    /**
     * The role enum
     * 
     * @author bruno.marchesson
     */
    public enum Role {
        user,
        admin
    }

    /**
     * The current role.
     */
    protected Role role;

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

    @Override
    public Class<?> getActiveAnnotation(Object entity, String propertyName) {
        if (role == Role.admin) {
            return null;
        } else {
            return ReadOnly.class;
        }
    }
}
