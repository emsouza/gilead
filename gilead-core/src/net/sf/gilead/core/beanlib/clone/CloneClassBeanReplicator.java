/*
 * Copyright 2007 The Apache Software Foundation.
 *
 * Licensed under the Apache License, Version 2.0 (the "License")
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package net.sf.gilead.core.beanlib.clone;

import net.sf.beanlib.hibernate3.Hibernate3JavaBeanReplicator;
import net.sf.beanlib.spi.BeanTransformerSpi;
import net.sf.beanlib.spi.replicator.BeanReplicatorSpi;
import net.sf.gilead.core.IPersistenceUtil;
import net.sf.gilead.core.beanlib.IClassMapper;
import net.sf.gilead.core.beanlib.merge.BeanlibThreadLocal;
import net.sf.gilead.core.beanlib.merge.MergeClassBeanReplicator;

import org.apache.log4j.Logger;

/**
 * Bean replicator with different from and to classes for clone operation
 * 
 * @author bruno.marchesson
 */
public class CloneClassBeanReplicator extends Hibernate3JavaBeanReplicator {
	// ---
	// Attributes
	// ---
	/**
	 * Logger channel
	 */
	private static Logger _log = Logger.getLogger(CloneClassBeanReplicator.class);

	/**
	 * The class mapper (can be null)
	 */
	private IClassMapper _classMapper;

	/**
	 * Persistence util class
	 */
	private IPersistenceUtil _persistenceUtil;

	// ----
	// Factory
	// ----
	public static final Factory factory = new Factory();

	/**
	 * Factory for {@link MergeClassBeanReplicator}
	 * 
	 * @author bruno.marchesson
	 */
	public static class Factory implements BeanReplicatorSpi.Factory {
		private Factory() {}

		@Override
		public CloneClassBeanReplicator newBeanReplicatable(BeanTransformerSpi beanTransformer) {
			return new CloneClassBeanReplicator(beanTransformer);
		}
	}

	public static CloneClassBeanReplicator newBeanReplicatable(BeanTransformerSpi beanTransformer) {
		return factory.newBeanReplicatable(beanTransformer);
	}

	// ----
	// Constructor
	// ----
	protected CloneClassBeanReplicator(BeanTransformerSpi beanTransformer) {
		super(beanTransformer);
	}

	// ----
	// Properties
	// ----
	/**
	 * @return the Class Mapper
	 */
	public IClassMapper getClassMapper() {
		return _classMapper;
	}

	/**
	 * @param mapper the classMapper to set
	 */
	public void setClassMapper(IClassMapper mapper) {
		_classMapper = mapper;
	}

	/**
	 * @return the persistence Util implementation to use
	 */
	public IPersistenceUtil getPersistenceUtil() {
		return _persistenceUtil;
	}

	/**
	 * @param util the persistenceUtil to set
	 */
	public void setPersistenceUtil(IPersistenceUtil util) {
		_persistenceUtil = util;
	}

	// ----
	// Override
	// ----
	@Override
	public <V extends Object, T extends Object> T replicateBean(V from, java.lang.Class<T> toClass) {
		// Force persistence map computation (useful for subclass)
		_persistenceUtil.isPersistentPojo(from);

		BeanlibThreadLocal.getFromBeanStack().push(from);
		T result = super.replicateBean(from, toClass);
		BeanlibThreadLocal.getFromBeanStack().pop();
		return result;
	}

	@Override
	@SuppressWarnings("unchecked")
	protected <T extends Object> T createToInstance(Object from, java.lang.Class<T> toClass) throws InstantiationException, IllegalAccessException,
			SecurityException, NoSuchMethodException {
		// Class mapper indirection
		//
		if (_classMapper != null) {
			// Get target class
			//
			Class<T> targetClass = (Class<T>) _classMapper.getTargetClass(from.getClass());

			// Keep target class only if not null
			//
			if (targetClass != null) {
				if (_log.isDebugEnabled()) {
					_log.debug("Class mapper : from " + from.getClass() + " to " + targetClass);
				}
				toClass = targetClass;
			} else {
				if (_log.isDebugEnabled()) {
					_log.debug("Class mapper : no target class for " + from.getClass());
				}
			}

		}
		return super.createToInstance(from, toClass);
	}
}
