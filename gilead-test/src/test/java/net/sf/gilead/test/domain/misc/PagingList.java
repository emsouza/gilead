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
@SuppressWarnings("rawtypes")
public class PagingList extends ArrayList {

    private static final long serialVersionUID = 1084952802855800325L;

    /**
     * Total records info
     */
    private int totalRecords;

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
