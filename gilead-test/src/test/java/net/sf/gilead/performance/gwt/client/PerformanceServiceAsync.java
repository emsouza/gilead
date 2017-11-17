package net.sf.gilead.performance.gwt.client;

import com.google.gwt.user.client.rpc.AsyncCallback;

import net.sf.gilead.test.domain.interfaces.IUser;

/**
 * Performance remote service implementation
 * 
 * @author bruno.marchesson
 * @generated generated asynchronous callback interface to be used on the client side
 */

public interface PerformanceServiceAsync {

    /**
     * @gwt.callbackReturn a persistent user and associated messages
     * @param callback the callback that will be called to receive the return value (see
     *            <code>@gwt.callbackReturn</code> tag)
     * @generated generated method with asynchronous callback parameter to be used on the client side
     */
    void loadUserAndMessages(AsyncCallback<IUser> callback);
}
