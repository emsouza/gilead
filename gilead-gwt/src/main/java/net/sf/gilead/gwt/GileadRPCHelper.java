package net.sf.gilead.gwt;

import com.google.gwt.user.server.rpc.RPCRequest;

import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.sf.gilead.core.PersistentBeanManager;
import net.sf.gilead.core.beanlib.mapper.ProxyClassMapper;
import net.sf.gilead.core.store.stateful.HttpSessionProxyStore;
import net.sf.gilead.exception.NotAssignableException;
import net.sf.gilead.exception.TransientObjectException;
import net.sf.gilead.proxy.AdditionalCodeManager;
import net.sf.gilead.proxy.ProxyClassLoader;

/**
 * Static helper class for PersistentRemoteService and HibernateRPCServiceExporter (GWT-SL)
 *
 * @author bruno.marchesson
 */
public class GileadRPCHelper {

    private static final Logger LOGGER = LoggerFactory.getLogger(GileadRPCHelper.class);

    /**
     * Proxy class loader initialisation
     */
    @SuppressWarnings("resource")
    public static void initClassLoader() {
        // Set Proxy class loader (privileged code needed)
        //
        ClassLoader contextClassLoader = Thread.currentThread().getContextClassLoader();
        if (contextClassLoader instanceof ProxyClassLoader == false) {
            LOGGER.info("Setting proxy class loader for thread " + Thread.currentThread());

            // initialize AdditionalCodeManager before changing class loader to prevent stack overflow
            AdditionalCodeManager.getInstance();

            Thread.currentThread().setContextClassLoader(new ProxyClassLoader(contextClassLoader));
        }
    }

    /**
     * Parse RPC input parameters. Must be called before GWT service invocation.
     *
     * @param rpcRequest the input GWT RPC request
     * @param beanManager the Hibernate bean manager
     * @param session the HTTP session (for HTTP Pojo store)
     */
    public static void parseInputParameters(Object[] parameters, PersistentBeanManager beanManager, HttpSession session) {
        // Init classloader for proxy mode
        if (beanManager.getClassMapper() instanceof ProxyClassMapper) {
            initClassLoader();
        }

        // Set HTTP session of Pojo store in thread local
        HttpSessionProxyStore.setHttpSession(session);

        // Merge parameters if needed
        if (parameters != null) {
            long start = System.currentTimeMillis();
            for (int index = 0; index < parameters.length; index++) {
                if (parameters[index] != null) {
                    try {
                        parameters[index] = beanManager.merge(parameters[index], true);
                    } catch (NotAssignableException ex) {
                        LOGGER.error(parameters[index] + " not assignable");
                    } catch (TransientObjectException ex) {
                        LOGGER.error(parameters[index] + " is transient : cannot merge...");
                    }
                }
            }
            LOGGER.trace("Merge took " + (System.currentTimeMillis() - start) + " ms.");
        }
    }

    /**
     * Parse RPC input parameters. Must be called before GWT service invocation.
     *
     * @param rpcRequest the input GWT RPC request
     * @param beanManager the Hibernate bean manager
     * @param session the HTTP session (for HTTP Pojo store)
     */
    public static void parseInputParameters(RPCRequest rpcRequest, PersistentBeanManager beanManager, HttpSession session) {
        // Extract object parameters
        Object[] parameters = null;
        if ((rpcRequest != null) && (rpcRequest.getParameters() != null)) {
            parameters = rpcRequest.getParameters();
        }

        // Parse input parameters
        parseInputParameters(parameters, beanManager, session);
    }

    /**
     * Clone the service result. Must be called after successful service invocation
     *
     * @param returnValue the service return value
     * @param beanManager the Hibernate bean manager
     * @return the cloned service value
     */
    public static final Object parseReturnValue(Object returnValue, PersistentBeanManager beanManager) {
        // Clone if needed
        if (returnValue != null) {
            long start = System.currentTimeMillis();
            try {
                returnValue = beanManager.clone(returnValue, true);
            } catch (NotAssignableException ex) {
                LOGGER.error(returnValue + " not assignable");
            } catch (TransientObjectException ex) {
                LOGGER.error(returnValue + " is transient : cannot clone...");
            } finally {
                beanManager.getProxyStore().cleanUp();
            }

            LOGGER.trace("Clone took " + (System.currentTimeMillis() - start) + " ms.");
        }

        // Remove HTTP session of Pojo store thread local
        HttpSessionProxyStore.setHttpSession(null);

        return returnValue;
    }
}
