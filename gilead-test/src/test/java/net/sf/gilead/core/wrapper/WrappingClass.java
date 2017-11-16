/**
 *
 */
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
    // ----
    // Enum (for test)
    // ----
    public enum ErrorCode {
        ok,
        warning,
        error,
    }

    // ----
    // Attributes
    // ----
    /**
     * The associated user
     */
    protected IUser _user;

    /**
     * The associated message list
     */
    protected List<IMessage> _messageList;

    /**
     * The error code
     */
    protected ErrorCode _errorCode;

    // ----
    // Properties
    // ----
    /**
     * @return the user
     */
    public IUser getUser() {
        return _user;
    }

    /**
     * @param user the user to set
     */
    public void setUser(IUser user) {
        _user = user;
    }

    /**
     * @return the messageList
     */
    public List<IMessage> getMessageList() {
        // test unmodifiable set
        return Collections.unmodifiableList(_messageList);
    }

    /**
     * @param list the messageList to set
     */
    public void setMessageList(List<IMessage> list) {
        _messageList = list;
    }

    /**
     * @return the error code
     */
    public ErrorCode getErrorCode() {
        return _errorCode;
    }

    /**
     * @param error_code the error code to set
     */
    public void setErrorCode(ErrorCode error_code) {
        _errorCode = error_code;
    }
}