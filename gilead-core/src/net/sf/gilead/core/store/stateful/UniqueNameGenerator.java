/**
 * 
 */
package net.sf.gilead.core.store.stateful;

import java.io.Serializable;
import java.util.Stack;

import net.sf.gilead.core.IPersistenceUtil;
import net.sf.gilead.core.beanlib.merge.BeanlibThreadLocal;
import net.sf.gilead.exception.ComponentTypeException;
import net.sf.gilead.exception.TransientObjectException;

/**
 * Static class for unique name generation
 * 
 * @author BMARCHESSON
 */
public class UniqueNameGenerator {

	// -------------------------------------------------------------------------
	//
	// Public method
	//
	// -------------------------------------------------------------------------
	/**
	 * Generates a unique name for the argument Hibenrate pojo
	 */
	public static String generateUniqueName(IPersistenceUtil persistenceUtil, Object hibernatePojo) {
		return generateUniqueName(persistenceUtil, hibernatePojo, hibernatePojo.getClass());
	}

	/**
	 * Generates a unique name for the argument DTO associated with the hibernateClass
	 */
	public static String generateUniqueName(IPersistenceUtil persistenceUtil, Object pojo, Class<?> hibernateClass) {
		// Get ID
		//
		Serializable id = getUniqueId(persistenceUtil, pojo);

		// Format unique name
		//
		return generateUniqueName(id, hibernateClass);
	}

	/**
	 * Generates a unique name for the argument DTO associated with the hibernateClass
	 */
	public static String generateUniqueName(Serializable id, Class<?> hibernateClass) {
		// Format unique name
		//
		StringBuffer buffer = new StringBuffer();
		buffer.append(hibernateClass.getName());
		buffer.append('@');
		buffer.append(id.toString());

		return buffer.toString();
	}

	/**
	 * Gets a unique ID for the argument persistent bean. It simply returns the ID of persistent bean, or the ID of the
	 * parent persistent bean for component types.
	 * 
	 * @param persistenceUtil the persistence util
	 * @param persistentBean the persistent bean
	 * @return
	 */
	public static Serializable getUniqueId(IPersistenceUtil persistenceUtil, Object persistentBean) {
		try {
			return persistenceUtil.getId(persistentBean);
		} catch (ComponentTypeException ex) {
			// Component type : search parent in bean stack
			//
			Stack<Object> stack = BeanlibThreadLocal.getFromBeanStack();
			for (int index = stack.size() - 1; index >= 0; index--) {
				Object object = stack.get(index);
				if (object != persistentBean) {
					try {
						return persistenceUtil.getId(object);
					} catch (TransientObjectException e) {
						// Go on next level
					}
				}
			}

			// Cannot find parent ID : resend ComponentTypeException
			//
			throw ex;
		}
	}
}
