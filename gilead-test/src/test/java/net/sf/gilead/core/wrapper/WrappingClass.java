package net.sf.gilead.core.wrapper;

import java.util.Collections;
import java.util.List;

import net.sf.gilead.test.domain.interfaces.IMessage;
import net.sf.gilead.test.domain.interfaces.IUser;

/**
 * Wrapping (non persistent) class containing persistent classes. Must be cloned and merged as a regular persistent
 * bean.
 *
 * @author bruno.marchesson
 */
public class WrappingClass {

    public enum ErrorCode {
        ok,
        warning,
        error,
    }

    /**
     * The associated user
     */
    protected IUser user;

    /**
     * The associated message list
     */
    protected List<IMessage> message;

    /**
     * The error code
     */
    protected ErrorCode errorCode;

    /**
     * @return the user
     */
    public IUser getUser() {
        return user;
    }

    /**
     * @param user the user to set
     */
    public void setUser(IUser user) {
        this.user = user;
    }

    /**
     * @return the message
     */
    public List<IMessage> getMessageList() {
        // test unmodifiable set
        return Collections.unmodifiableList(message);
    }

    /**
     * @param list the message to set
     */
    public void setMessageList(List<IMessage> message) {
        this.message = message;
    }

    /**
     * @return the error code
     */
    public ErrorCode getErrorCode() {
        return errorCode;
    }

    /**
     * @param error_code the error code to set
     */
    public void setErrorCode(ErrorCode errorCode) {
        this.errorCode = errorCode;
    }
}
