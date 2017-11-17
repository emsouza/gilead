package net.sf.gilead.test.domain.stateful;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

import net.sf.gilead.test.domain.interfaces.IGroup;
import net.sf.gilead.test.domain.interfaces.IUser;

/**
 * GroupDTO class. A group contains many users, and user belongs to many groups (for many to many association test)
 * 
 * @author bruno.marchesson
 */
public class Group implements Serializable, IGroup {

    private static final long serialVersionUID = -7851731827756230018L;

    private Integer id;
    private Integer version;

    private String name;
    private Set<IUser> members;

    @Override
    public Integer getId() {
        return id;
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
    public String getName() {
        return name;
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }

    @Override
    public Set<IUser> getMembers() {
        return members;
    }

    @Override
    public void setMembers(Set<IUser> members) {
        this.members = members;
    }

    @Override
    public void addMember(IUser user) {
        if (members == null) {
            members = new HashSet<>();
        }

        if (members.contains(user) == false) {
            members.add(user);
            user.addToGroup(this);
        }
    }

    @Override
    public void removeMember(IUser user) {
        if ((members != null) && (members.contains(user))) {
            members.remove(user);
            user.removeUserFromGroup(this);
        }
    }
}
