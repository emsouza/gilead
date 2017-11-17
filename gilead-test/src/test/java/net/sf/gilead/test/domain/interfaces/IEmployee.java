package net.sf.gilead.test.domain.interfaces;

/**
 * Interface of subclass Employee
 * 
 * @author bruno.marchesson
 */
public interface IEmployee extends IUser {

    /**
     * @return the email
     */
    String getEmail();

    /**
     * @param email the email to set
     */
    void setEmail(String email);
}