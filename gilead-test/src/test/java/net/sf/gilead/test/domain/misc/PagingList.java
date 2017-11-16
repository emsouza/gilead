/**
 * 
 */
package net.sf.gilead.test.domain.misc;

import java.util.ArrayList;

/**
 * PagingList class, inspired from GWT-Plus library (the code is not the one from the library, but was rewrriten based
 * on it, thanks to Sanjiv)
 * 
 * @author bruno.marchesson
 */
public class PagingList extends ArrayList {
    // ----
    // Attributes
    // ----
    /**
     * Serialization ID
     */
    private static final long serialVersionUID = 1084952802855800325L;

    /**
     * Total records info
     */
    private int totalRecords;

    // ----
    // Properties
    // ----
    /**
     * @return the totalRecords
     */
    public int getTotalRecords() {
        return totalRecords;
    }

    /**
     * @param totalRecords the totalRecords to set
     */
    public void setTotalRecords(int totalRecords) {
        this.totalRecords = totalRecords;
    }
}
