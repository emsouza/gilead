package net.sf.gilead.test.domain.interfaces;

import java.util.Set;

public interface IGroup {

    // ----
    // Properties
    // ----
    /**
     * @return the id
     */
    public abstract Integer getId();

    /**
     * @param id the id to set
     */
    public abstract void setId(Integer id);

    /**
     * @return the version
     */
    public abstract Integer getVersion();

    /**
     * @param version the version to set
     */
    public abstract void setVersion(Integer version);

    /**
     * @return the name
     */
    public abstract String getName();

    /**
     * @param name the name to set
     */
    public abstract void setName(String name);

    /**
     * @return the members
     */
    public abstract Set<IUser> getMembers();

    /**
     * @param members the members to set
     */
    public abstract void setMembers(Set<IUser> members);

    /**
     * Add user as a member
     * 
     * @param
     */
    public abstract void addMember(IUser user);

    /*
     * (non-Javadoc)
     * @see net.sf.gilead.testApplication.domain.IUser#removeMessage(net.sf.gilead.testApplication.domain.IMessage)
     */
    public abstract void removeMember(IUser user);

}