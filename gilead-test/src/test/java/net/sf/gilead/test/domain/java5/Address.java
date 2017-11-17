package net.sf.gilead.test.domain.java5;

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
public class Address extends LightEntity implements IAddress {

    private static final long serialVersionUID = -6914495957511158133L;

    @Column(nullable = false)
    private String street;

    @Column(nullable = false)
    private String city;

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
}
