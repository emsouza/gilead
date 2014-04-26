/**
 * 
 */
package net.sf.gilead.core.wrapper;

import net.sf.gilead.test.domain.interfaces.IMessage;

/**
 * Wrapping (non persistent) class containing an array persistent classes. Must be cloned and merged as a regular
 * persistent bean.
 * 
 * @author bruno.marchesson
 */
public class WrappingArrayClass {
    // ----
    // Attributes
    // ----
    /**
     * The associated message array
     */
    protected IMessage[] _messages;

    // ----
    // Properties
    // ----
    /**
     * @return the messages
     */
    public IMessage[] getMessages() {
        return _messages;
    }

    /**
     * @param list the messageList to set
     */
    public void setMessages(IMessage[] array) {
        _messages = array;
    }
}