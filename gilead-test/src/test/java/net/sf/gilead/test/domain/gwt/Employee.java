package net.sf.gilead.test.domain.gwt;

import net.sf.gilead.test.domain.interfaces.IEmployee;

/**
 * Employee class. Subclass of User
 * 
 * @author bruno.marchesson
 */
public class Employee extends User implements IEmployee {

    private static final long serialVersionUID = -2294737766711898873L;

    private String email;

    @Override
    public String getEmail() {
        return email;
    }

    @Override
    public void setEmail(String email) {
        this.email = email;
    }
}