package net.sf.gilead.test.domain.annotated;

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
public class Employee extends User implements IEmployee {

    private static final long serialVersionUID = -2294737766711898873L;

    private String email;

    @Override
    @Column(name = "EMAIL", length = 45)
    public String getEmail() {
        return email;
    }

    @Override
    public void setEmail(String email) {
        this.email = email;
    }
}