package net.sf.gilead.core.loading.gwt.client;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

import net.sf.gilead.test.domain.stateful.Message;
import net.sf.gilead.test.domain.stateful.User;

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
    Message loadTestMessage();

    /**
     * Initialize the test environment and load a user message
     *
     * @return
     */
    User loadTestUser();
}
