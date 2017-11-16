package net.sf.gilead.test.domain.misc;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinColumns;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Transient;

@Entity
public class SomeDictionary implements Serializable {
    @Embeddable
    public static class PrimaryKey implements Serializable {
        private static final long serialVersionUID = -1786248350425646775L;

        private int baseDictionaryId;

        private int id;

        public PrimaryKey() {}

        public PrimaryKey(final int baseDictionaryId, final int id) {
            this.baseDictionaryId = baseDictionaryId;
            this.id = id;
        }

        @Override
        public boolean equals(final Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj == null) {
                return false;
            }
            if (!(obj instanceof PrimaryKey)) {
                return false;
            }
            final PrimaryKey other = (PrimaryKey) obj;
            if (baseDictionaryId != other.baseDictionaryId) {
                return false;
            }
            if (id != other.id) {
                return false;
            }
            return true;
        }

        @Column(name = "BASE_DICTIONARY_ID", nullable = false, updatable = false, precision = 2)
        public int getBaseDictionaryId() {
            return baseDictionaryId;
        }

        @Column(name = "ID", nullable = false, updatable = false, precision = 5)
        public int getId() {
            return id;
        }

        @Override
        public int hashCode() {
            final int prime = 31;
            int result = 1;
            result = prime * result + baseDictionaryId;
            result = prime * result + id;
            return result;
        }

        public void setBaseDictionaryId(final int baseDictionaryId) {
            this.baseDictionaryId = baseDictionaryId;
        }

        public void setId(final int id) {
            this.id = id;
        }
    }

    private static final long serialVersionUID = -344723901746214193L;

    private BaseDictionary baseDictionary;

    private List<SomeDictionary> children = new ArrayList<SomeDictionary>();

    private SomeDictionary parent;

    private PrimaryKey key;

    public SomeDictionary() {}

    public SomeDictionary(final PrimaryKey key) {
        this.key = key;
    }

    public void addChild(final SomeDictionary child) {
        if (!children.contains(child)) {
            child.setParent(this);
            children.add(child);
        }
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (!(obj instanceof SomeDictionary)) {
            return false;
        }
        final SomeDictionary other = (SomeDictionary) obj;
        if (key == null) {
            if (other.key != null) {
                return false;
            }
        } else if (!key.equals(other.key)) {
            return false;
        }
        return true;
    }

    // @MappedById("baseDictionaryId")
    @ManyToOne(optional = false)
    @JoinColumn(name = "BASE_DICTIONARY_ID", nullable = false, insertable = false, updatable = false)
    public BaseDictionary getBaseDictionary() {
        return baseDictionary;
    }

    public void setBaseDictionary(final BaseDictionary BaseDictionary) {
        this.baseDictionary = BaseDictionary;
    }

    @OneToMany(fetch = FetchType.EAGER, mappedBy = "parent", cascade = { CascadeType.REMOVE })
    public List<SomeDictionary> getChildren() {
        return children;
    }

    @Transient
    public int getChildrenCount() {
        return children.size();
    }

    @EmbeddedId
    public PrimaryKey getKey() {
        return key;
    }

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumns({ @JoinColumn(name = "PARENT_ID", referencedColumnName = "ID", updatable = false),
            @JoinColumn(name = "PARENT_BASE_DICTIONARY_ID", referencedColumnName = "BASE_DICTIONARY_ID", updatable = false) })
    public SomeDictionary getParent() {
        return parent;
    }

    public boolean hasChildren() {
        return !children.isEmpty();
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((key == null) ? 0 : key.hashCode());
        return result;
    }

    @Transient
    public boolean isTopLevel() {
        return (parent == null);
    }

    public void removeChild(final SomeDictionary child) {
        if (hasChildren() && children.contains(child)) {
            children.remove(child);
            child.setParent(null);
        }
    }

    public void setChildren(final List<SomeDictionary> children) {
        this.children = children;
    }

    public void setKey(final PrimaryKey key) {
        this.key = key;
    }

    public void setParent(final SomeDictionary parent) {
        this.parent = parent;
    }
}