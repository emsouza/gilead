package net.sf.gilead.gwt;

import com.google.gwt.user.client.rpc.IncompatibleRemoteServiceException;
import com.google.gwt.user.client.rpc.SerializationException;
import com.google.gwt.user.server.rpc.RPC;
import com.google.gwt.user.server.rpc.RPCRequest;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import com.google.gwt.user.server.rpc.SerializationPolicy;

import java.lang.reflect.InvocationTargetException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;

import net.sf.gilead.core.PersistentBeanManager;
import net.sf.gilead.core.beanlib.mapper.ProxyClassMapper;

/**
 * Abstract class for GWT remote service using persistent POJO
 *
 * @author bruno.marchesson
 */
public abstract class PersistentRemoteService extends RemoteServiceServlet {

    private static final long serialVersionUID = 7432874379586734765L;

    /**
     * The Hibernate lazy manager
     */
    protected PersistentBeanManager beanManager;

    /**
     * Empty constructor
     */
    public PersistentRemoteService() {
        // Default Hibernate Lazy Manager
        beanManager = PersistentBeanManager.getInstance();
    }

    /**
     * Base constructor
     */
    public PersistentRemoteService(PersistentBeanManager beanManager) {
        this.beanManager = beanManager;
    }

    /**
     * @return the Persistent Bean Manager
     */
    public PersistentBeanManager getBeanManager() {
        return beanManager;
    }

    /**
     * @param manager the Hibernate Bean Manager to set
     */
    public void setBeanManager(PersistentBeanManager beanManager) {
        this.beanManager = beanManager;
    }

    /**
     * Clone and store (if needed) the hibernate POJO
     */
    public Object clone(Object hibernatePojo) {
        return beanManager.clone(hibernatePojo);
    }

    /**
     * Retrieve and populate Hibernate pojo with gwt pojo values
     */
    public Object merge(Object gwtPojo) {
        return beanManager.merge(gwtPojo);
    }

    @Override
    protected SerializationPolicy doGetSerializationPolicy(HttpServletRequest request, String moduleBaseURL, String strongName) {
        // Init proxy class loader if in proxy mode
        if ((beanManager != null) && (beanManager.getClassMapper() instanceof ProxyClassMapper)) {
            GileadRPCHelper.initClassLoader();
        }
        return super.doGetSerializationPolicy(request, moduleBaseURL, strongName);
    }

    /**
     * Servlet initialisation
     */
    @Override
    public void init() throws ServletException {
        super.init();

        // Init proxy class loader if in proxy mode
        if ((beanManager != null) && (beanManager.getClassMapper() instanceof ProxyClassMapper)) {
            GileadRPCHelper.initClassLoader();
        }
    }

    /**
     * Override of the RemoteServletService main method
     */
    @Override
    public String processCall(String payload) throws SerializationException {
        // Normal processing
        RPCRequest rpcRequest = null;
        try {
            // Decode request
            rpcRequest = RPC.decodeRequest(payload, this.getClass(), this);

            if (rpcRequest == null) {
                throw new NullPointerException("No rpc request");
            }

            // Invoke method
            GileadRPCHelper.parseInputParameters(rpcRequest, beanManager, getThreadLocalRequest().getSession());
            Object returnValue = rpcRequest.getMethod().invoke(this, rpcRequest.getParameters());

            returnValue = GileadRPCHelper.parseReturnValue(returnValue, beanManager);

            // Encode response
            return RPC.encodeResponseForSuccess(rpcRequest.getMethod(), returnValue, rpcRequest.getSerializationPolicy());
        } catch (IllegalArgumentException e) {
            SecurityException securityException = new SecurityException("Blocked attempt to invoke method " + rpcRequest.getMethod());
            securityException.initCause(e);
            throw securityException;
        } catch (IllegalAccessException e) {
            SecurityException securityException = new SecurityException(
                    "Blocked attempt to access inaccessible method " + rpcRequest.getMethod() + " on target " + this);
            securityException.initCause(e);
            throw securityException;
        } catch (InvocationTargetException e) {
            // Clone exception if needed
            Exception exception = (Exception) GileadRPCHelper.parseReturnValue(e.getCause(), beanManager);

            return RPC.encodeResponseForFailure(rpcRequest.getMethod(), exception, rpcRequest.getSerializationPolicy());
        } catch (IncompatibleRemoteServiceException ex) {
            // Clone exception if needed
            Exception exception = (Exception) GileadRPCHelper.parseReturnValue(ex, beanManager);
            return RPC.encodeResponseForFailure(null, exception, rpcRequest.getSerializationPolicy());
        }
    }
}
