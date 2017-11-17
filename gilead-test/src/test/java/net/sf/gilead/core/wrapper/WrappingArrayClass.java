package net.sf.gilead.core.wrapper;

import net.sf.gilead.test.domain.interfaces.IMessage;

/**
 * Wrapping (non persistent) class containing an array persistent classes. Must be cloned and merged as a regular
 * persistent bean.
 * 
 * @author bruno.marchesson
 */
public class WrappingArrayClass {

    /**
     * The associated message array
     */
    protected IMessage[] messages;

    /**
     * @return the messages
     */
    public IMessage[] getMessages() {
        return messages;
    }

    /**
     * @param list the messages to set
     */
    public void setMessages(IMessage[] messages) {
        this.messages = messages;
    }
}