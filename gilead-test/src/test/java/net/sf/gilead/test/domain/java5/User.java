package net.sf.gilead.test.domain.java5;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Version;

import net.sf.gilead.test.domain.interfaces.IAddress;
import net.sf.gilead.test.domain.interfaces.IGroup;
import net.sf.gilead.test.domain.interfaces.IMessage;
import net.sf.gilead.test.domain.interfaces.IUser;

/**
 * User Domain class for JAVA5 server
 */
@Entity
@Table(name = "user")
@Inheritance(strategy = InheritanceType.JOINED)
public class User implements Serializable, IUser {

    private static final long serialVersionUID = 1058354709157710766L;

    private Integer id;
    private Integer version;

    private String login;
    private String firstName;
    private String lastName;
    private String password;

    private Address address;

    private Set<IMessage> messageList;
    private Set<IGroup> groupList;

    @Id
    @Override
    @GeneratedValue
    @Column(name = "ID")
    public Integer getId() {
        return this.id;
    }

    @Override
    public void setId(Integer id) {
        this.id = id;
    }

    @Override
    @Version
    @Column(name = "VERSION")
    public Integer getVersion() {
        return version;
    }

    @Override
    public void setVersion(Integer version) {
        this.version = version;
    }

    @Override
    @Column(name = "LOGIN", nullable = false, length = 45)
    public String getLogin() {
        return this.login;
    }

    @Override
    public void setLogin(String surname) {
        this.login = surname;
    }

    @Override
    @Column(name = "FIRST_NAME", nullable = false, length = 45)
    public String getFirstName() {
        return this.firstName;
    }

    @Override
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    @Override
    @Column(name = "LAST_NAME", nullable = false, length = 45)
    public String getLastName() {
        return this.lastName;
    }

    @Override
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    @Override
    @Column(name = "PASSWORD", length = 45)
    public String getPassword() {
        return this.password;
    }

    @Override
    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * @return the message
     */
    @Override
    @JoinColumn(name = "USER_ID")
    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, targetEntity = Message.class, orphanRemoval = true)
    public Set<IMessage> getMessageList() {
        return messageList;
    }

    /**
     * @param message the message to set
     */
    @Override
    public void setMessageList(Set<IMessage> messageList) {
        this.messageList = messageList;
    }

    @Override
    public void addMessage(IMessage message) {
        ((Message) message).setAuthor(this);

        // Create message list if needed
        if (messageList == null) {
            messageList = new HashSet<>();
        }
        messageList.add(message);
    }

    @Override
    public void removeMessage(IMessage message) {
        messageList.remove(message);
    }

    /**
     * @return the groupList
     */
    @Override
    @ManyToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, mappedBy = "members", targetEntity = Group.class)
    public Set<IGroup> getGroupList() {
        return groupList;
    }

    /**
     * @param groupList the groupList to set
     */
    @Override
    public void setGroupList(Set<IGroup> groupList) {
        this.groupList = groupList;
    }

    /**
     * Add user to the argument group
     */
    @Override
    public void addToGroup(IGroup group) {
        if (groupList == null) {
            groupList = new HashSet<>();
        }

        if (groupList.contains(group) == false) {
            groupList.add(group);
            group.addMember(this);
        }
    }

    /**
     * Remove user from group
     */
    @Override
    public void removeUserFromGroup(IGroup group) {
        if ((groupList != null) && (groupList.contains(group))) {
            groupList.remove(group);
            group.removeMember(this);
        }
    }

    @Override
    @Embedded
    public Address getAddress() {
        return address;
    }

    @Override
    public void setAddress(IAddress address) {
        this.address = (Address) address;
    }
}
