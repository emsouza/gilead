package net.sf.gilead.test.dao;

import java.util.List;

import net.sf.gilead.test.domain.interfaces.IMessage;

public interface IMessageDAO {

    /**
     * The IoC name
     */
    public static final String NAME = "messageDAO";

    /**
     * Load the last posted message
     */
    IMessage loadLastMessage();

    /**
     * Load all the posted messages
     * 
     * @param startIndex first index of the message to load
     * @param maxResult max number of message to load
     * @return a list of IMessage
     */
    List<IMessage> loadAllMessage();

    /**
     * Load the complete message
     * 
     * @param id the ID of the message to load
     * @return the message if found, null otherwise
     */
    IMessage loadDetailedMessage(Integer id);

    /**
     * Save the argument message
     */
    void saveMessage(IMessage message);

    /**
     * Delete the argument message
     */
    void deleteMessage(IMessage message);

    /**
     * Lock the argument message
     */
    void lockMessage(IMessage message);

    /**
     * Count all messages
     * 
     * @return number of posted messages
     */
    int countAllMessages();
}
