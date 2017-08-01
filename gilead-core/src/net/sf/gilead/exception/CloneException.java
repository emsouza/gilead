package net.sf.gilead.exception;

public class CloneException extends RuntimeException {

    private static final long serialVersionUID = 6572503996574346821L;

    /**
     * Base constructor
     */
    public CloneException(String message) {
        super(message);
    }
}
