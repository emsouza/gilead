package net.sf.gilead.test.domain.gwt;

import java.util.Date;

import net.sf.gilead.pojo.gwt.LightEntity;
import net.sf.gilead.test.domain.interfaces.IMessage;
import net.sf.gilead.test.domain.interfaces.IUser;

/**
 * Message Java domain class for stateless pojo store This class just has to inherit from LightEntity; The 'Keywords'
 * property has private getter and setter
 *
 * @author bruno.marchesson
 */
public class Message extends LightEntity implements IMessage {

    private static final long serialVersionUID = 3445339493203407152L;

    private int id;

    private Integer version;

    private String message;

    private Date date;

    private IUser author;

    /**
     * @return the id
     */
    @Override
    public int getId() {
        return id;
    }

    /**
     * @param id the id to set
     */
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

    /**
     * @return the message
     */
    @Override
    public String getMessage() {
        return message;
    }

    /**
     * @param message the message to set
     */
    @Override
    public void setMessage(String message) {
        this.message = message;
    }

    /**
     * @return the timeStamp
     */
    @Override
    public Date getDate() {
        return date;
    }

    /**
     * @param timeStamp the timeStamp to set
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
    public void setAuthor(IUser author) {
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
        IMessage other = (IMessage) obj;
        return (id == other.getId());
    }
}
