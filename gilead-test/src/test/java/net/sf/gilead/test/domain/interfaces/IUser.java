package net.sf.gilead.test.domain.interfaces;

import java.util.Set;

/**
 * Common interface for all user implementations. Not needed in production, it is just used for unified testing between
 * different configurations (stateful, stateless, proxy and Java5)
 * 
 * @author bruno.marchesson
 */
public interface IUser {

    Integer getId();

    void setId(Integer id);

    Integer getVersion();

    void setVersion(Integer version);

    String getLogin();

    void setLogin(String surname);

    String getFirstName();

    void setFirstName(String firstName);

    String getLastName();

    void setLastName(String lastName);

    String getPassword();

    void setPassword(String password);

    Set<IMessage> getMessageList();

    void setMessageList(Set<IMessage> messageList);

    void addMessage(IMessage message);

    void removeMessage(IMessage message);

    /**
     * @return the groupList
     */
    Set<IGroup> getGroupList();

    /**
     * @param groupList the groupList to set
     */
    void setGroupList(Set<IGroup> groupList);

    /**
     * Add user to the argument group
     */
    void addToGroup(IGroup group);

    /**
     * Remove user from group
     */
    void removeUserFromGroup(IGroup group);

    /**
     * @return the address
     */
    IAddress getAddress();

    /**
     * @param address the address to set
     */
    void setAddress(IAddress address);
}
