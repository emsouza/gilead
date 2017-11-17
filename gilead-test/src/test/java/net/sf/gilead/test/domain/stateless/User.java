package net.sf.gilead.test.domain.stateless;

import java.util.HashSet;
import java.util.Set;

import net.sf.gilead.pojo.java5.LightEntity;
import net.sf.gilead.test.domain.interfaces.IAddress;
import net.sf.gilead.test.domain.interfaces.IGroup;
import net.sf.gilead.test.domain.interfaces.IMessage;
import net.sf.gilead.test.domain.interfaces.IUser;

/**
 * User Domain class for stateless server
 */
public class User extends LightEntity implements IUser {

    private static final long serialVersionUID = 1058354709157710766L;

    private Integer id;
    private Integer version;

    private String login;
    private String firstName;
    private String lastName;
    private String password;

    private IAddress address;

    private Set<IMessage> messageList;
    private Set<IGroup> groupList;

    @Override
    public Integer getId() {
        return this.id;
    }

    @Override
    public void setId(Integer id) {
        this.id = id;
    }

    @Override
    public Integer getVersion() {
        return version;
    }

    @Override
    public void setVersion(Integer version) {
        this.version = version;
    }

    @Override
    public String getLogin() {
        return this.login;
    }

    @Override
    public void setLogin(String surname) {
        this.login = surname;
    }

    @Override
    public String getFirstName() {
        return this.firstName;
    }

    @Override
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    @Override
    public String getLastName() {
        return this.lastName;
    }

    @Override
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    @Override
    public String getPassword() {
        return this.password;
    }

    @Override
    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * @return the address
     */
    @Override
    public IAddress getAddress() {
        return address;
    }

    /**
     * @param address the address to set
     */
    @Override
    public void setAddress(IAddress address) {
        this.address = address;
    }

    /**
     * @return the message List
     */
    @Override
    public Set<IMessage> getMessageList() {
        return messageList;
    }

    /**
     * @param message the message List to set
     */
    @Override
    public void setMessageList(Set<IMessage> messageList) {
        this.messageList = messageList;
    }

    @Override
    public void addMessage(IMessage message) {
        ((Message) message).setAuthor(this);
        if (messageList == null) {
            messageList = new HashSet<>();
        }
        messageList.add(message);
    }

    @Override
    public void removeMessage(IMessage message) {
        messageList.remove(message);
        ((Message) message).setAuthor(this);
    }

    /**
     * @return the groupList
     */
    @Override
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
        }
    }

    /**
     * Remove user from group
     */
    @Override
    public void removeUserFromGroup(IGroup group) {
        if ((groupList != null) && (groupList.contains(group))) {
            groupList.remove(group);
        }
    }
}
