package net.sf.gilead.test.domain.misc;

import java.io.Serializable;
import java.util.List;

/**
 * List of result encapsulation, inspired from GXT library
 * 
 * @author bruno.marchesson
 * @param <Data>
 */
public class BaseListLoadResult<Data> implements Serializable {
    /**
     * Serialisation ID
     */
    private static final long serialVersionUID = -6519188971278457470L;

    /**
     * List of data
     */
    protected List<Data> list;

    /**
     * Constructor (protected)
     */
    BaseListLoadResult() {}

    /**
     * Complete constructor
     * 
     * @param list
     */
    public BaseListLoadResult(List<Data> list) {
        this.list = list;
    }

    /**
     * @return underlying data
     */
    public List<Data> getData() {
        return list;
    }

    /**
     * Change underlying data
     * 
     * @param list
     */
    public void setData(List<Data> list) {
        this.list = list;
    }
}
