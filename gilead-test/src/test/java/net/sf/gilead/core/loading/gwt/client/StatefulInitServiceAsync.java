package net.sf.gilead.core.loading.gwt.client;

import com.google.gwt.user.client.rpc.AsyncCallback;

import net.sf.gilead.test.domain.stateful.Message;
import net.sf.gilead.test.domain.stateful.User;

/**
 * Service for server side initialization (unit test purpose)
 * 
 * @author bruno.marchesson
 * @generated generated asynchronous callback interface to be used on the client side
 */

public interface StatefulInitServiceAsync {

    /**
     * Initialize the test environment and load a test message
     * 
     * @return
     * @param callback the callback that will be called to receive the return value
     * @generated generated method with asynchronous callback parameter to be used on the client side
     */
    void loadTestMessage(AsyncCallback<Message> callback);

    /**
     * Initialize the test environment and load a user message
     * 
     * @return
     * @param callback the callback that will be called to receive the return value
     * @generated generated method with asynchronous callback parameter to be used on the client side
     */
    void loadTestUser(AsyncCallback<User> callback);
}
