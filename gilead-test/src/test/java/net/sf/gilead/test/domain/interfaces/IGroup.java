package net.sf.gilead.test.domain.interfaces;

import java.util.Set;

public interface IGroup {

    /**
     * @return the id
     */
    Integer getId();

    /**
     * @param id the id to set
     */
    void setId(Integer id);

    /**
     * @return the version
     */
    Integer getVersion();

    /**
     * @param version the version to set
     */
    void setVersion(Integer version);

    /**
     * @return the name
     */
    String getName();

    /**
     * @param name the name to set
     */
    void setName(String name);

    /**
     * @return the members
     */
    Set<IUser> getMembers();

    /**
     * @param members the members to set
     */
    void setMembers(Set<IUser> members);

    /**
     * Add user as a member
     * 
     * @param
     */
    void addMember(IUser user);

    void removeMember(IUser user);
}
