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

    private static final long serialVersionUID = 3421537443957416948L;

    private int id;
    private Integer version;
    private String message;
    private Date date;

    private User author;

    @Override
    public int getId() {
        return id;
    }

    @Override
    public void setId(int id) {
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

    @Override
    public String getMessage() {
        return message;
    }

    @Override
    public void setMessage(String message) {
        this.message = message;
    }

    @Override
    public Date getDate() {
        return date;
    }

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
