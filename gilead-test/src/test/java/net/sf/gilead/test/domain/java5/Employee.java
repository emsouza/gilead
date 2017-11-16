package net.sf.gilead.test.domain.java5;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import net.sf.gilead.test.domain.interfaces.IEmployee;

/**
 * Employee class. Subclass of User
 * 
 * @author bruno.marchesson
 */
@Entity
@Table(name = "employee")
public class Employee extends User implements IEmployee, Serializable {
    /**
     * Serialization ID
     */
    private static final long serialVersionUID = -2294737766711898873L;

    // Fields
    private String email;

    // Properties
    /*
     * (non-Javadoc)
     * @see net.sf.gilead.test.domain.proxy.IEmployee#getEmail()
     */
    @Override
    @Column(name = "EMAIL", length = 45)
    public String getEmail() {
        return email;
    }

    /*
     * (non-Javadoc)
     * @see net.sf.gilead.test.domain.proxy.IEmployee#setEmail(java.lang.String)
     */
    @Override
    public void setEmail(String email) {
        this.email = email;
    }
}