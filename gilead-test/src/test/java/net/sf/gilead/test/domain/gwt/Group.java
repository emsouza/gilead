package net.sf.gilead.test.domain.gwt;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

import net.sf.gilead.pojo.gwt.LightEntity;
import net.sf.gilead.test.domain.interfaces.IGroup;
import net.sf.gilead.test.domain.interfaces.IUser;

/**
 * GroupDTO class. A group contains many users, and user belongs to many groups (for many to many association test)
 * 
 * @author bruno.marchesson
 */
public class Group extends LightEntity implements Serializable, IGroup {
    // ----
    // Attributes
    // ----
    /**
     * Serialization id
     */
    private static final long serialVersionUID = -7851731827756230018L;

    // Fields
    private Integer id;
    private Integer version;

    private String name;
    private Set<IUser> members;

    // ----
    // Properties
    // ----
    /*
     * (non-Javadoc)
     * @see net.sf.gilead.test.domain.stateless.IGroup#getId()
     */
    @Override
    public Integer getId() {
        return id;
    }

    /*
     * (non-Javadoc)
     * @see net.sf.gilead.test.domain.stateless.IGroup#setId(java.lang.Integer)
     */
    @Override
    public void setId(Integer id) {
        this.id = id;
    }

    /*
     * (non-Javadoc)
     * @see net.sf.gilead.test.domain.stateless.IGroup#getVersion()
     */
    @Override
    public Integer getVersion() {
        return version;
    }

    /*
     * (non-Javadoc)
     * @see net.sf.gilead.test.domain.stateless.IGroup#setVersion(java.lang.Integer)
     */
    @Override
    public void setVersion(Integer version) {
        this.version = version;
    }

    /*
     * (non-Javadoc)
     * @see net.sf.gilead.test.domain.stateless.IGroup#getName()
     */
    @Override
    public String getName() {
        return name;
    }

    /*
     * (non-Javadoc)
     * @see net.sf.gilead.test.domain.stateless.IGroup#setName(java.lang.String)
     */
    @Override
    public void setName(String name) {
        this.name = name;
    }

    /*
     * (non-Javadoc)
     * @see net.sf.gilead.test.domain.stateless.IGroup#getMembers()
     */
    @Override
    public Set<IUser> getMembers() {
        return members;
    }

    /*
     * (non-Javadoc)
     * @see net.sf.gilead.test.domain.stateless.IGroup#setMembers(java.util.Set)
     */
    @Override
    public void setMembers(Set<IUser> members) {
        this.members = members;
    }

    /*
     * (non-Javadoc)
     * @see net.sf.gilead.test.domain.stateless.IGroup#addMember(net.sf.gilead.test.domain.IUser)
     */
    @Override
    public void addMember(IUser user) {
        if (members == null) {
            members = new HashSet<IUser>();
        }

        if (members.contains(user) == false) {
            members.add(user);
            user.addToGroup(this);
        }
    }

    /*
     * (non-Javadoc)
     * @see net.sf.gilead.testApplication.domain.IUser#removeMessage(net.sf.gilead.testApplication.domain.IMessage)
     */
    /*
     * (non-Javadoc)
     * @see net.sf.gilead.test.domain.stateless.IGroup#removeMember(net.sf.gilead.test.domain.IUser)
     */
    @Override
    public void removeMember(IUser user) {
        if ((members != null) && (members.contains(user))) {
            members.remove(user);
            user.removeUserFromGroup(this);
        }
    }

}
