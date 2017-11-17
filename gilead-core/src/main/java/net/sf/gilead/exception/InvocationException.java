package net.sf.gilead.exception;

/**
 * Invocation exception
 * 
 * @author bruno.marchesson
 */
public class InvocationException extends RuntimeException {

    private static final long serialVersionUID = -6963949434725754587L;

    /**
     * Empty constructor
     */
    public InvocationException() {
        super();
    }

    public InvocationException(String arg0) {
        super(arg0);
    }

    public InvocationException(Throwable arg0) {
        super(arg0);
    }

    public InvocationException(String arg0, Throwable arg1) {
        super(arg0, arg1);
    }
}
