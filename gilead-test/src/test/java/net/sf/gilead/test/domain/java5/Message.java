package net.sf.gilead.test.domain.java5;

import java.io.Serializable;
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

import net.sf.gilead.test.domain.interfaces.IMessage;

/**
 * Message domain class for Java5 Hibernate POJO This class has no inheritance on hibernate4gwt, and will be converted
 * as a DTO in HibernateRemoteService
 *
 * @author bruno.marchesson
 */
@Entity
@Table(name = "message")
public class Message implements Serializable, IMessage {

    private static final long serialVersionUID = -1067096371173906324L;

    private int id;
    private Integer version;
    private String message;
    private Date date;

    private User author;

    @Id
    @Override
    @Column(name = "ID")
    @GeneratedValue(strategy = GenerationType.AUTO)
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

    @Override
    @Column(name = "MESSAGE", nullable = false, length = 255)
    public String getMessage() {
        return message;
    }

    @Override
    public void setMessage(String message) {
        this.message = message;
    }

    @Override
    @Column(name = "DATE", nullable = false)
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
}
