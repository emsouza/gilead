package net.sf.gilead.test.domain.annotated;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.Version;

import net.sf.gilead.pojo.java5.LightEntity;
import net.sf.gilead.test.domain.interfaces.IGroup;
import net.sf.gilead.test.domain.interfaces.IUser;

/**
 * Group class. A group contains many users, and user belongs to many groups (for many to many association test)
 * 
 * @author bruno.marchesson
 */
@Entity(name = "Groupe")
public class Group extends LightEntity implements IGroup {

    private static final long serialVersionUID = -7851731827756230018L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;
    @Version
    private Integer version;

    private String name;

    @ManyToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, targetEntity = User.class)
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
