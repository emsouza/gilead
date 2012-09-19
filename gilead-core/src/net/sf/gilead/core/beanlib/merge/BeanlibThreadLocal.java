/**
 * 
 */
package net.sf.gilead.core.beanlib.merge;

import java.io.Serializable;
import java.util.Map;
import java.util.Stack;

/**
 * Thread local to store BeanLib additional parameters
 * 
 * @author bruno.marchesson
 */
public class BeanlibThreadLocal {
	// ----
	// Attributes
	// ----
	/**
	 * Target merge persistent collection class
	 */
	private static ThreadLocal<Map<String, Serializable>> proxyInformations = new ThreadLocal<Map<String, Serializable>>();

	/**
	 * Current from bean stack. It is used to get embedded entities (component type) parent to determine unique ID in
	 * stateful mode.
	 */
	private static ThreadLocal<Stack<Object>> fromBeanStack = new ThreadLocal<Stack<Object>>();

	/**
	 * Current target bean stack. It is used for persistent collections, that need to know their parent entity.
	 */
	private static ThreadLocal<Stack<Object>> toBeanStack = new ThreadLocal<Stack<Object>>();

	// ----
	// Properties
	// ----
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
		BeanlibThreadLocal.proxyInformations.set(proxyInfo);
	}

	/**
	 * @return the from bean stack
	 */
	public static Stack<Object> getFromBeanStack() {
		Stack<Object> stack = fromBeanStack.get();
		if (stack == null) {
			stack = new Stack<Object>();
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
			stack = new Stack<Object>();
			toBeanStack.set(stack);
		}
		return stack;
	}
}
