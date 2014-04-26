package net.sf.gilead.core.loading.gwt.client;

import net.sf.gilead.test.domain.stateful.Message;
import net.sf.gilead.test.domain.stateful.User;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

/**
 * Service for server side initialization (unit test purpose)
 * 
 * @author bruno.marchesson
 */
@RemoteServiceRelativePath("StatefulInitService")
public interface StatefulInitService extends RemoteService {
    /**
     * Initialize the test environment and load a test message
     * 
     * @return
     */
    public Message loadTestMessage();

    /**
     * Initialize the test environment and load a user message
     * 
     * @return
     */
    public User loadTestUser();
}
