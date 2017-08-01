/**
 *
 */
package net.sf.gilead.services;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.sf.gilead.core.PersistenceUtil;
import net.sf.gilead.core.PersistentBeanManager;

/**
 * Request service implementation.
 *
 * @author bruno.marchesson
 */
public class BaseRequestService {

    private static final Logger LOGGER = LoggerFactory.getLogger(BaseRequestService.class);

    /**
     * The associated bean manager. Default value is defined by the unique instance of the singleton.
     */
    private PersistentBeanManager beanManager = PersistentBeanManager.getInstance();

    /**
     * @return the beanManager
     */
    public PersistentBeanManager getBeanManager() {
        return beanManager;
    }

    /**
     * @param beanManager the beanManager to set
     */
    public void setBeanManager(PersistentBeanManager beanManager) {
        this.beanManager = beanManager;
    }

    @SuppressWarnings("unchecked")
    public List<Serializable> executeRequest(String query, List<Object> parameters) {
        // Precondition checking
        if (query == null) {
            throw new RuntimeException("Missing query !");
        }

        LOGGER.trace("Executing request " + query);

        if (beanManager == null) {
            throw new NullPointerException("Bean manager not set !");
        }

        // Get Persistence util
        PersistenceUtil persistenceUtil = beanManager.getPersistenceUtil();
        if (persistenceUtil == null) {
            throw new NullPointerException("Persistence util not set on beanManager field !");
        }

        // Execute query
        // Note : double case is mandatory due to Java 6 compiler issue 6548436
        List<Serializable> result = (List<Serializable>) (Object) persistenceUtil.executeQuery(query, parameters);
        return result;
    }

    @SuppressWarnings("unchecked")
    public List<Serializable> executeRequest(String query, Map<String, Object> parameters) {
        // Precondition checking
        if (query == null) {
            throw new RuntimeException("Missing query !");
        }

        LOGGER.trace("Executing request " + query);

        if (beanManager == null) {
            throw new NullPointerException("Bean manager not set !");
        }

        // Get Persistence util
        PersistenceUtil persistenceUtil = beanManager.getPersistenceUtil();
        if (persistenceUtil == null) {
            throw new NullPointerException("Persistence util not set on beanManager field !");
        }

        // Execute query
        // Note : double case is mandatory due to Java 6 compiler issue 6548436
        List<Serializable> result = (List<Serializable>) (Object) persistenceUtil.executeQuery(query, parameters);
        return result;
    }
}
