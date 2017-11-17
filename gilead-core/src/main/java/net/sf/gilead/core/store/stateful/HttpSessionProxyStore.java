package net.sf.gilead.core.store.stateful;

import java.io.Serializable;
import java.util.Map;

import javax.servlet.http.HttpSession;

import net.sf.gilead.exception.ProxyStoreException;

/**
 * Proxy store for stateful web application
 *
 * @author bruno.marchesson
 */
public class HttpSessionProxyStore extends AbstractStatefulProxyStore {

    /**
     * The storage thread local
     */
    private static ThreadLocal<HttpSession> httpSession = new ThreadLocal<>();

    /**
     * Store the current HTTP session in the thread local
     */
    public static void setHttpSession(HttpSession session) {
        httpSession.set(session);
    }

    @Override
    public void delete(String key) {
        getSession().removeAttribute(key);
    }

    @Override
    @SuppressWarnings("unchecked")
    public Map<String, Serializable> get(String key) {
        return (Map<String, Serializable>) getSession().getAttribute(key);
    }

    @Override
    public void store(String key, Map<String, Serializable> proxyInformation) {
        getSession().setAttribute(key, proxyInformation);
    }

    /**
     * @return the HTTP session stored in thread local
     */
    private HttpSession getSession() {
        HttpSession session = httpSession.get();
        if (session == null) {
            throw new ProxyStoreException("No HTTP session stored", null);
        }
        return session;
    }
}
