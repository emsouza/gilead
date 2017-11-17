/**
 * 
 */
package net.sf.gilead.test.domain.misc;

/**
 * Exception handling a persistent POJO
 * 
 * @author bruno.marchesson
 */
public class PersistentException extends Exception {

    private static final long serialVersionUID = -365724127793925824L;

    /**
     * The persistent POJO
     */
    private Page page;

    /**
     * @return the page
     */
    public Page getPage() {
        return page;
    }

    /**
     * @param page the page to set
     */
    public void setPage(Page page) {
        this.page = page;
    }

    /**
     * Empty constructor
     */
    public PersistentException() {}

    /**
     * Constructor.
     */
    public PersistentException(Page page) {
        this.page = page;
    }
}
