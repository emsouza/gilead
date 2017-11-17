package net.sf.gilead.test.domain.annotated;

import javax.persistence.Column;
import javax.persistence.Embeddable;

import net.sf.gilead.annotations.ReadOnly;
import net.sf.gilead.pojo.java5.LightEntity;
import net.sf.gilead.test.domain.interfaces.IAddress;

/**
 * Embedded test class for address (depends on User)
 *
 * @author bruno.marchesson
 */
@Embeddable
public class Address extends LightEntity implements IAddress {

    private static final long serialVersionUID = -6914495957511158133L;

    @Column(nullable = false)
    private String street;

    @Column(nullable = false)
    private String city;

    @Column(nullable = false)
    private String zipCode;

    @Override
    public String getStreet() {
        return street;
    }

    @Override
    public void setStreet(String street) {
        this.street = street;
    }

    @Override
    public String getCity() {
        return city;
    }

    @Override
    public void setCity(String city) {
        this.city = city;
    }

    /**
     * Private getter for @ReadOnly test
     *
     * @return the zipCode
     */
    @ReadOnly
    private String getZipCode() {
        return zipCode;
    }

    /**
     * @param zipCode the zipCode to set
     */
    public void setZipCode(String zipCode) {
        this.zipCode = zipCode;
    }
}
