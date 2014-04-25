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
package net.sf.beanlib.utils;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.apache.log4j.Logger;

/**
 * Method Interceptor used to time the method invokation. 
 * 
 * @author Joe D. Velopar
 */
public class TimingMethodInterceptor implements MethodInterceptor {
	public Object invoke(MethodInvocation methodInvocation) throws Throwable {
		Object thisObject = methodInvocation.getThis();
		Logger log = Logger.getLogger(thisObject.getClass());
		String method = methodInvocation.getMethod().getName(); 
		log.info(method + " begins");
		long startTime = System.currentTimeMillis();
        boolean thrown=true;
		try {
			Object retVal = methodInvocation.proceed();
            thrown = false;
			return retVal;
		} finally {
			log.info(new StringBuilder(method).append(" ends")
                        .append(thrown ? " abnormally" : " normally").append(".  ~")
						.append((System.currentTimeMillis() - startTime))
						.append(" msecs.")
					);
		}
	}
}
