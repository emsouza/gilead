/**
 * 
 */
package net.sf.gilead.services;

import java.io.Serializable;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import net.sf.gilead.core.IPersistenceUtil;
import net.sf.gilead.core.PersistentBeanManager;

/**
 * Request service implementation.
 * 
 * @author bruno.marchesson
 */
public class BaseRequestService {
    // ----
    // Attributes
    // ----
    /**
     * Serialization ID
     */
    private static final long serialVersionUID = 814725549964949202L;

    /**
     * Logger channel
     */
    private static Logger _log = Logger.getLogger(BaseRequestService.class.getSimpleName());

    /**
     * The associated bean manager. Default value is defined by the unique instance of the singleton.
     */
    private PersistentBeanManager beanManager = PersistentBeanManager.getInstance();

    // ----
    // Properties
    // ---
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

    // -------------------------------------------------------------------------
    //
    // Request service implementation
    //
    // -------------------------------------------------------------------------
    /**
     * @see net.sf.gilead.gwt.client.RequestService#executeRequest(java.lang.String, java.util.List)
     */
    @SuppressWarnings("unchecked")
    public List<Serializable> executeRequest(String query, List<Object> parameters) {
        // Precondition checking
        //
        if (query == null) {
            throw new RuntimeException("Missing query !");
        }

        _log.log(Level.FINE, "Executing request " + query);

        if (beanManager == null) {
            throw new NullPointerException("Bean manager not set !");
        }

        // Get Persistence util
        //
        IPersistenceUtil persistenceUtil = beanManager.getPersistenceUtil();
        if (persistenceUtil == null) {
            throw new NullPointerException("Persistence util not set on beanManager field !");
        }

        // Execute query
        // Note : double case is mandatory due to Java 6 compiler issue 6548436
        //
        List<Serializable> result = (List<Serializable>) (Object) persistenceUtil.executeQuery(query, parameters);
        return result;
    }

    /**
     * @see net.sf.gilead.gwt.client.RequestService#executeRequest(java.lang.String, java.util.Map)
     */
    @SuppressWarnings("unchecked")
    public List<Serializable> executeRequest(String query, Map<String, Object> parameters) {
        // Precondition checking
        //
        if (query == null) {
            throw new RuntimeException("Missing query !");
        }

        _log.log(Level.FINE, "Executing request " + query);

        if (beanManager == null) {
            throw new NullPointerException("Bean manager not set !");
        }

        // Get Persistence util
        //
        IPersistenceUtil persistenceUtil = beanManager.getPersistenceUtil();
        if (persistenceUtil == null) {
            throw new NullPointerException("Persistence util not set on beanManager field !");
        }

        // Execute query
        // Note : double case is mandatory due to Java 6 compiler issue 6548436
        //
        List<Serializable> result = (List<Serializable>) (Object) persistenceUtil.executeQuery(query, parameters);
        return result;
    }
}
