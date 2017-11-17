package net.sf.gilead.performance.gwt.client;

import com.google.gwt.user.client.rpc.RemoteService;

import net.sf.gilead.test.domain.interfaces.IUser;

/**
 * Performance remote service implementation
 * 
 * @author bruno.marchesson
 */
public interface PerformanceService extends RemoteService {

    /**
     * @return a persistent user and associated messages
     */
    IUser loadUserAndMessages();
}
