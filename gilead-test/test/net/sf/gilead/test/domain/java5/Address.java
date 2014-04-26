package net.sf.gilead.test.domain.java5;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Embeddable;

import net.sf.gilead.pojo.java5.LightEntity;
import net.sf.gilead.test.domain.interfaces.IAddress;

/**
 * Embedded test class for address (depends on User)
 * 
 * @author bruno.marchesson
 */
@Embeddable
public class Address extends LightEntity implements Serializable, IAddress {
    /**
     * Serialization ID
     */
    private static final long serialVersionUID = -6914495957511158133L;

    // Attributes
    @Column(nullable = false)
    private String street;

    @Column(nullable = false)
    private String city;

    // Properties
    /*
     * (non-Javadoc)
     * @see net.sf.gilead.test.domain.stateless.IAddress#getStreet()
     */
    @Override
    public String getStreet() {
        return street;
    }

    /*
     * (non-Javadoc)
     * @see net.sf.gilead.test.domain.stateless.IAddress#setStreet(java.lang.String)
     */
    @Override
    public void setStreet(String street) {
        this.street = street;
    }

    /*
     * (non-Javadoc)
     * @see net.sf.gilead.test.domain.stateless.IAddress#getCity()
     */
    @Override
    public String getCity() {
        return city;
    }

    /*
     * (non-Javadoc)
     * @see net.sf.gilead.test.domain.stateless.IAddress#setCity(java.lang.String)
     */
    @Override
    public void setCity(String city) {
        this.city = city;
    }
}
