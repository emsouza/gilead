/**
 *
 */
package net.sf.gilead.exception;

/**
 * Exception when trying to clone or merge an transient object
 *
 * @author bruno.marchesson
 */
public class TransientObjectException extends RuntimeException {

    private static final long serialVersionUID = -3916689195006928705L;

    /**
     * The exception object
     */
    private Object object;

    /**
     * @return the object
     */
    public final Object getObject() {
        return object;
    }

    /**
     * Base constructor
     */
    public TransientObjectException(Object object) {
        this.object = object;
    }
}
