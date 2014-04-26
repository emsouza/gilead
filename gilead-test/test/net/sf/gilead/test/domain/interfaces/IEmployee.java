package net.sf.gilead.test.domain.interfaces;

/**
 * Interface of subclass Employee
 * 
 * @author bruno.marchesson
 */
public interface IEmployee extends IUser {

    // Properties
    /**
     * @return the email
     */
    public abstract String getEmail();

    /**
     * @param email the email to set
     */
    public abstract void setEmail(String email);

}