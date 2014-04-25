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
package net.sf.beanlib.provider.handler;

import java.lang.reflect.Method;

import net.sf.beanlib.spi.BeanPopulationExceptionHandler;

/** 
 * Can be used as a convenient adaptor class for implementing 
 * the {@link BeanPopulationExceptionHandler} interface.
 * 
 * @author Joe D. Velopar
 */
public abstract class AbstractBeanPopulationExceptionHandler implements BeanPopulationExceptionHandler 
{
	protected String propertyName;
	protected Object fromBean, toBean;
	protected Method readerMethod, setterMethod;
	
	public BeanPopulationExceptionHandler initPropertyName(String propertyName) {
		this.propertyName = propertyName;
		return this;
	}

	public BeanPopulationExceptionHandler initFromBean(Object fromBean) {
		this.fromBean = fromBean;
		return this;
	}

	public BeanPopulationExceptionHandler initReaderMethod(Method readerMethod) {
		this.readerMethod = readerMethod;
		return this;
	}

	public BeanPopulationExceptionHandler initToBean(Object toBean) {
		this.toBean = toBean;
		return this;
	}

	public BeanPopulationExceptionHandler initSetterMethod(Method setterMethod) {
		this.setterMethod = setterMethod;
		return this;
	}
}
