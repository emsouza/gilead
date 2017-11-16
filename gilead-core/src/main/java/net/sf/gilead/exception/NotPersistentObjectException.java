/**
 *
 */
package net.sf.gilead.exception;

/**
 * Exception when trying to clone or merge an object not persisted with Persistence engine
 *
 * @author bruno.marchesson
 */
public class NotPersistentObjectException extends RuntimeException {

    private static final long serialVersionUID = 3274347637647294793L;

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
    public NotPersistentObjectException(Object object) {
        this.object = object;
    }

    /**
     * Get formatted message
     */
    @Override
    public String getMessage() {
        StringBuilder stringBuilder = new StringBuilder();

        stringBuilder.append("Not Persistent Object : ");
        if (object != null) {
            stringBuilder.append(" [Class is ");
            stringBuilder.append(object.getClass().getName());
            stringBuilder.append("]");
        }
        return stringBuilder.toString();
    }
}
