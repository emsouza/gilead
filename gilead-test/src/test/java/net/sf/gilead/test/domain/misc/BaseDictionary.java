package net.sf.gilead.test.domain.misc;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class BaseDictionary implements Serializable {
    private static final long serialVersionUID = -4793946537941656195L;

    private int id;
    private String name;

    public BaseDictionary() {}

    public BaseDictionary(final int id) {
        this.id = id;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (!(obj instanceof BaseDictionary)) {
            return false;
        }
        final BaseDictionary other = (BaseDictionary) obj;
        if (id != other.id) {
            return false;
        }
        return true;
    }

    @Id
    @Column(name = "ID", nullable = false, precision = 2)
    public int getId() {
        return id;
    }

    @Column(name = "NAME", nullable = false, length = 25)
    public String getName() {
        return name;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + id;
        return result;
    }

    public void setId(final int id) {
        this.id = id;
    }

    public void setName(final String name) {
        this.name = name;
    }
}