package net.sf.gilead.test.domain.interfaces;

import java.util.Date;

/**
 * Interface of the Java 1.4 domain message classes (stateful and stateless)
 *
 * @author bruno.marchesson
 */
public interface IMessage {

    /**
     * @return the id
     */
    int getId();

    /**
     * @param id the id to set
     */
    void setId(int id);

    /**
     * @return the version
     */
    Integer getVersion();

    /**
     * @param version the version to set
     */
    void setVersion(Integer version);

    /**
     * @return the message
     */
    String getMessage();

    /**
     * @param message the message to set
     */
    void setMessage(String message);

    /**
     * @return the timeStamp
     */
    Date getDate();

    /**
     * @param timeStamp the timeStamp to set
     */
    void setDate(Date timeStamp);

    /**
     * @return the author
     */
    IUser getAuthor();
}