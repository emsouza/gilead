/**
 *
 */
package net.sf.gilead.test;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

/**
 * Hibernate context utility class
 *
 * @author bruno.marchesson
 */
public class HibernateContext {
    // ----
    // Enumeration
    // ----
    public enum Context {
        stateful,
        stateless,
        legacy,
        gwt,
        java5,
        proxy,
        annotated
    }

    // ----
    // Constants
    // ----
    /**
     * The stateful configuration file
     */
    private static final String STATEFUL_CONFIGURATION_FILE = "stateful/hibernate.cfg.xml";

    /**
     * The stateless configuration file
     */
    private static final String STATELESS_CONFIGURATION_FILE = "stateless/hibernate.cfg.xml";

    /**
     * The stateless GWT configuration file
     */
    private static final String GWT_CONFIGURATION_FILE = "gwt/hibernate.cfg.xml";

    /**
     * The stateless legacy configuration file (Gilead 1.2)
     */
    private static final String LEGACY_CONFIGURATION_FILE = "legacy/hibernate.cfg.xml";

    /**
     * The Java5 configuration file
     */
    private static final String JAVA5_CONFIGURATION_FILE = "java5/hibernate.cfg.xml";

    /**
     * The Proxy configuration file
     */
    private static final String PROXY_CONFIGURATION_FILE = "proxy/hibernate.cfg.xml";

    /**
     * The Java5 annotated (ServerOnly, ReadOnly) configuration file
     */
    private static final String ANNOTATED_CONFIGURATION_FILE = "annotated/hibernate.cfg.xml";

    // ----
    // Attributes
    // ----
    /**
     * Logger channel
     */
    private static Logger _log = Logger.getLogger(HibernateContext.class.getSimpleName());

    /**
     * Current configuration
     */
    private static Context _context;

    /**
     * The current session factory
     */
    private static SessionFactory _sessionFactory;

    // ----
    // Properties
    // ----
    /**
     * @return the application context
     */
    public static Context getContext() {
        return _context;
    }

    /**
     * @param context the context to set
     */
    public static void setContext(Context context) {
        if (_context != context) {
            _context = context;

            if (_sessionFactory != null) {
                _sessionFactory.close();
            }
            _sessionFactory = null;
        }
    }

    // -------------------------------------------------------------------------
    //
    // Public interface
    //
    // -------------------------------------------------------------------------
    /**
     * @return the created session factory
     */
    public static SessionFactory getSessionFactory() {
        if (_sessionFactory == null) {
            try {
                // Create the SessionFactory from hibernate.cfg.xml
                _sessionFactory = new Configuration().configure(getContextFile()).buildSessionFactory();
            } catch (Throwable ex) {
                // Make sure you log the exception, as it might be swallowed
                _log.log(Level.SEVERE, "Initial SessionFactory creation failed.", ex);
                throw new ExceptionInInitializerError(ex);
            }
        }
        return _sessionFactory;
    }

    // -------------------------------------------------------------------------
    //
    // Internal methods
    //
    // -------------------------------------------------------------------------
    /**
     * @return the appropriate context file associated with the current application configuration
     */
    private static String getContextFile() {
        if (_context == Context.stateful) {
            return STATEFUL_CONFIGURATION_FILE;
        } else if (_context == Context.stateless) {
            return STATELESS_CONFIGURATION_FILE;
        } else if (_context == Context.gwt) {
            return GWT_CONFIGURATION_FILE;
        } else if (_context == Context.legacy) {
            return LEGACY_CONFIGURATION_FILE;
        } else if (_context == Context.proxy) {
            return PROXY_CONFIGURATION_FILE;
        } else if (_context == Context.java5) {
            return JAVA5_CONFIGURATION_FILE;
        } else // if (_context == Context.annotated)
        {
            return ANNOTATED_CONFIGURATION_FILE;
        }
    }
}
