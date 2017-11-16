package net.sf.gilead.performance.gwt.client;

import net.sf.gilead.test.domain.interfaces.IUser;

import com.google.gwt.user.client.rpc.RemoteService;

/**
 * Performance remote service implementation
 * 
 * @author bruno.marchesson
 */
public interface PerformanceService extends RemoteService {
    /**
     * @return a persistent user and associated messages
     */
    public IUser loadUserAndMessages();
}
