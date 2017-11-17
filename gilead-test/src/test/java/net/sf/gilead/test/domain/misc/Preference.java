package net.sf.gilead.test.domain.misc;

import javax.persistence.Embeddable;

import org.hibernate.annotations.Parent;

import net.sf.gilead.pojo.java5.LightEntity;

@Embeddable
public class Preference extends LightEntity {

    private static final long serialVersionUID = -731857320269444208L;

    @Parent
    private Utente user;

    private int intValue;

    /**
     * @return the user
     */
    public Utente getUser() {
        return user;
    }

    /**
     * @param user the user to set
     */
    public void setUser(Utente user) {
        this.user = user;
    }

    /**
     * @return the value
     */
    public int getIntValue() {
        return intValue;
    }

    /**
     * @param value the value to set
     */
    public void setIntValue(int value) {
        this.intValue = value;
    }

    @Override
    public String toString() {
        return "Preference " + intValue;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + intValue;
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        Preference other = (Preference) obj;
        if (intValue != other.intValue) {
            return false;
        }
        return true;
    }
}
