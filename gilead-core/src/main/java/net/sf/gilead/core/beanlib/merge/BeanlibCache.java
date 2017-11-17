package net.sf.gilead.core.beanlib.merge;

import java.io.Serializable;
import java.util.Map;
import java.util.Stack;

/**
 * Thread local to store BeanLib additional parameters
 * 
 * @author bruno.marchesson
 */
public class BeanlibCache {

    /**
     * Target merge persistent collection class
     */
    private static ThreadLocal<Map<String, Serializable>> proxyInformations = new ThreadLocal<>();

    /**
     * Current from bean stack. It is used to get embedded entities (component type) parent to determine unique ID in
     * stateful mode.
     */
    private static ThreadLocal<Stack<Object>> fromBeanStack = new ThreadLocal<>();

    /**
     * Current target bean stack. It is used for persistent collections, that need to know their parent entity.
     */
    private static ThreadLocal<Stack<Object>> toBeanStack = new ThreadLocal<>();

    /**
     * @return the proxy informations
     */
    public static Map<String, Serializable> getProxyInformations() {
        return proxyInformations.get();
    }

    /**
     * @param proxyInfo the proxy informations to set
     */
    public static void setProxyInformations(Map<String, Serializable> proxyInfo) {
        if (proxyInfo != null) {
            proxyInformations.set(proxyInfo);
        } else {
            proxyInformations.remove();
        }
    }

    /**
     * @return the from bean stack
     */
    public static Stack<Object> getFromBeanStack() {
        Stack<Object> stack = fromBeanStack.get();
        if (stack == null) {
            stack = new Stack<>();
            fromBeanStack.set(stack);
        }
        return stack;
    }

    /**
     * @return the to bean stack
     */
    public static Stack<Object> getToBeanStack() {
        Stack<Object> stack = toBeanStack.get();
        if (stack == null) {
            stack = new Stack<>();
            toBeanStack.set(stack);
        }
        return stack;
    }
}
