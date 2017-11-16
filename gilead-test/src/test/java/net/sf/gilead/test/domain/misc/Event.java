package net.sf.gilead.test.domain.misc;

import java.util.Date;
import java.util.SortedSet;
import java.util.TreeSet;

import net.sf.gilead.pojo.gwt.LightEntity;

public class Event extends LightEntity {
    /**
     * Serialization ID
     */
    private static final long serialVersionUID = 7396466186076632943L;
    private Long id;
    private String name;
    private Date date;
    private SortedSet<Person> attendees = new TreeSet<Person>();

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
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * @param name the name to set
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return the date
     */
    public Date getDate() {
        return date;
    }

    /**
     * @param date the date to set
     */
    public void setDate(Date date) {
        this.date = date;
    }

    /**
     * @return the attendees
     */
    public SortedSet<Person> getAttendees() {
        return attendees;
    }

    /**
     * @param attendees the attendees to set
     */
    public void setAttendees(SortedSet<Person> attendees) {
        this.attendees = attendees;
    }
}
