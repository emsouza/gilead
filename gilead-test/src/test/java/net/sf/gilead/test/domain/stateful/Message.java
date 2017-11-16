package net.sf.gilead.test.domain.stateful;

import java.io.Serializable;
import java.util.Date;

import net.sf.gilead.test.domain.interfaces.IMessage;
import net.sf.gilead.test.domain.interfaces.IUser;

/**
 * Message Java 1.4 domain class for stateful pojo store This class has no inheritance on hibernate4gwt, but must be
 * Serializable for GWT RPC serialization
 *
 * @author bruno.marchesson
 */
public class Message implements Serializable, IMessage {
    /**
     * Serialization ID
     */
    private static final long serialVersionUID = 3421537443957416948L;

    // Fields
    private int id;
    private Integer version;
    private String message;
    private Date date;

    private User author;

    // Properties
    /*
     * (non-Javadoc)
     * @see net.sf.gilead.testApplication.domain.IMessage#getId()
     */
    @Override
    public final int getId() {
        return id;
    }

    /*
     * (non-Javadoc)
     * @see net.sf.gilead.testApplication.domain.IMessage#setId(java.lang.Integer)
     */
    @Override
    public final void setId(int id) {
        this.id = id;
    }

    /**
     * @return the version
     */
    @Override
    public Integer getVersion() {
        return version;
    }

    /**
     * @param version the version to set
     */
    @Override
    public void setVersion(Integer version) {
        this.version = version;
    }

    /*
     * (non-Javadoc)
     * @see net.sf.gilead.testApplication.domain.IMessage#getMessage()
     */
    @Override
    public String getMessage() {
        return message;
    }

    /*
     * (non-Javadoc)
     * @see net.sf.gilead.testApplication.domain.IMessage#setMessage(java.lang.String)
     */
    @Override
    public void setMessage(String message) {
        this.message = message;
    }

    /*
     * (non-Javadoc)
     * @see net.sf.gilead.testApplication.domain.IMessage#getDate()
     */
    @Override
    public Date getDate() {
        return date;
    }

    /*
     * (non-Javadoc)
     * @see net.sf.gilead.testApplication.domain.IMessage#setDate(java.util.Date)
     */
    @Override
    public void setDate(Date timeStamp) {
        this.date = timeStamp;
    }

    /**
     * @return the author
     */
    @Override
    public IUser getAuthor() {
        return author;
    }

    /**
     * @param author the author to set
     */
    public void setAuthor(User author) {
        this.author = author;
    }

    /**
     * Equality function
     */
    @Override
    public boolean equals(Object obj) {
        if ((obj == null) || (obj instanceof Message == false)) {
            return false;
        } else if (this == obj) {
            return true;
        }

        // ID comparison
        Message other = (Message) obj;
        return (id == other.getId());
    }
}
