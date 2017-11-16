package net.sf.gilead.test.domain.interfaces;

public interface IAddress {

    // Properties
    /**
     * @return the street
     */
    public abstract String getStreet();

    /**
     * @param street the street to set
     */
    public abstract void setStreet(String street);

    /**
     * @return the city
     */
    public abstract String getCity();

    /**
     * @param city the city to set
     */
    public abstract void setCity(String city);

}