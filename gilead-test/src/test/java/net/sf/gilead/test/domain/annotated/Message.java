package net.sf.gilead.test.domain.annotated;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Version;

import net.sf.gilead.annotations.LimitedAccess;
import net.sf.gilead.annotations.ServerOnly;
import net.sf.gilead.pojo.java5.LightEntity;
import net.sf.gilead.test.domain.interfaces.IMessage;

/**
 * Message domain class for Java5 Hibernate POJO This class has no inheritance on hibernate4gwt, and will be converted
 * as a DTO in HibernateRemoteService
 * 
 * @author bruno.marchesson
 */
@Entity
@Table(name = "message")
public class Message extends LightEntity implements IMessage {
    /**
     * Serialisation ID
     */
    private static final long serialVersionUID = -1067096371173906324L;

    // Fields
    private int id;

    @ServerOnly
    private Integer version;

    private String message;

    private Date date;

    private User author;

    @LimitedAccess
    private String comment;

    // Properties
    /*
     * (non-Javadoc)
     * @see net.sf.gilead.testApplication.domain.IMessage#getId()
     */
    @Override
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "ID")
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
    @Version
    @Column(name = "VERSION")
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
    @Column(name = "MESSAGE", nullable = false, length = 255)
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
    @Column(name = "DATE", nullable = false)
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
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "USER_ID")
    public User getAuthor() {
        return author;
    }

    /**
     * @param author the author to set
     */
    public void setAuthor(User author) {
        this.author = author;
    }

    /**
     * @return the comment
     */
    public String getComment() {
        return comment;
    }

    /**
     * @param comment the comment to set
     */
    public void setComment(String comment) {
        this.comment = comment;
    }
}
