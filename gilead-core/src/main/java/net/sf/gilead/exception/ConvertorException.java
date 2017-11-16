/**
 *
 */
package net.sf.gilead.exception;

/**
 * Serializable Convertor Exception
 *
 * @author bruno.marchesson
 */
public class ConvertorException extends RuntimeException {

    private static final long serialVersionUID = -3726099973351020112L;

    /**
     * @param arg0
     */
    public ConvertorException(String arg0) {
        super(arg0);
    }

    /**
     * @param arg0
     */
    public ConvertorException(Throwable arg0) {
        super(arg0);
    }

    /**
     * @param arg0
     * @param arg1
     */
    public ConvertorException(String arg0, Throwable arg1) {
        super(arg0, arg1);
    }
}
