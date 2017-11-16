package net.sf.gilead.test.domain.misc;

import java.io.Serializable;
import java.util.Collection;
import java.util.LinkedHashSet;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;

import net.sf.gilead.pojo.java5.LightEntity;

import org.hibernate.annotations.Cascade;

@Entity
public class Client extends LightEntity implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    @ManyToMany
    @JoinTable(name = "LinkClientToProject", joinColumns = @JoinColumn(name = "clientId"), inverseJoinColumns = @JoinColumn(name = "projectId"))
    @Cascade(org.hibernate.annotations.CascadeType.SAVE_UPDATE)
    private Collection<Project> projects = new LinkedHashSet<Project>();

    // Some other fields and methods go here
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Collection<Project> getProjects() {
        return projects;
    }

    public void setProjects(Collection<Project> projects) {
        this.projects = projects;
    }

}