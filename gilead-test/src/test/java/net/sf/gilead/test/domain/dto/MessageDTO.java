package net.sf.gilead.test.domain.dto;

import java.util.Date;

import net.sf.gilead.pojo.java5.LightEntity;
import net.sf.gilead.test.domain.interfaces.IMessage;
import net.sf.gilead.test.domain.interfaces.IUser;

/**
 * DTO Message class for Java5 support This class just has to inherit from LazyGwtPojo It is also used as DTO for the
 * Java5 Message POJO
 * 
 * @author bruno.marchesson
 */
public class MessageDTO extends LightEntity implements IMessage {

    private static final long serialVersionUID = 3445339493203407152L;

    private int id;
    private Integer version;
    private String message;
    private Date date;

    private UserDTO author;

    /**
     * @return the id
     */
    @Override
    public final int getId() {
        return id;
    }

    /**
     * @param id the id to set
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
    public void setAuthor(UserDTO author) {
        this.author = author;
    }
}
