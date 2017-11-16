/**
 *
 */
package net.sf.gilead.exception;

/**
 * Exception in proxy generation or handling
 *
 * @author bruno.marchesson
 */
public class ProxyException extends RuntimeException {

    private static final long serialVersionUID = -7472497039290844426L;

    /**
     * Message constructor
     */
    public ProxyException(String msg) {
        super(msg);
    }

    /**
     * Nested exception constructor
     */
    public ProxyException(String msg, Throwable e) {
        super(msg, e);
    }
}
