package net.sf.gilead.test.domain.misc;

import net.sf.gilead.pojo.gwt.LightEntity;

public class Person extends LightEntity {
    /**
     * Serialization ID
     */
    private static final long serialVersionUID = 2459649482584350839L;

    private Long id;
    private Event parent;

    /**
     * @return the id
     */
    public Long getId() {
        return id;
    }

    /**
     * @param id the id to set
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * @return the parent
     */
    public Event getParent() {
        return parent;
    }

    /**
     * @param parent the parent to set
     */
    public void setParent(Event parent) {
        this.parent = parent;
    }
}
