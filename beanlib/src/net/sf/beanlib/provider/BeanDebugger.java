/*
 * Copyright 2005 The Apache Software Foundation.
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
package net.sf.beanlib.provider;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.apache.log4j.Logger;

/**
 * Bean Debugger.
 * 
 * @author Joe D. Velopar
 */
public class BeanDebugger {
	/** Singleton instance. */
//	private static final boolean debug = false;
//	private final Log log = LogFactory.getLog(this.getClass());

    /** Logs at debug level all readable properties of the given bean. */
	public void debugBeanProperties(Object bean, Logger log) {
        if (log.isDebugEnabled())
            log.debug("Reading bean properties for class " + bean.getClass());
		try {
			BeanInfo bi = Introspector.getBeanInfo(bean.getClass());
			PropertyDescriptor[] pda = bi.getPropertyDescriptors();

			for (int i = pda.length - 1; i > -1; i--) {
				PropertyDescriptor pd = pda[i];
				Method m = pd.getReadMethod();

				if (m != null) {
                    if (log.isDebugEnabled())
                        log.debug(pd.getName() + "={" + m.invoke(bean) + "}");
				}
			}
		} catch (IntrospectionException e) {
			log.error(e);
		} catch (IllegalAccessException e) {
			log.error(e);
		} catch (InvocationTargetException e) {
			log.error(e.getTargetException());
		}
	}
}