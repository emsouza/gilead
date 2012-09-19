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
package net.sf.gilead.core.beanlib.mapper;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import net.sf.gilead.core.beanlib.IClassMapper;

import org.apache.log4j.Logger;

/**
 * Class mapper based on explicitly parameterized classes
 * 
 * @author Olaf Kock, Florian Siebert
 */
public class ExplicitClassMapper implements IClassMapper {
	/**
	 * Logger channel
	 */
	private static Logger _log = Logger.getLogger(ExplicitClassMapper.class);

	/**
	 * The maps of the domain class to their target class correspondence.
	 */
	private Map<Class<?>, Class<?>> _domainToTargetMap = new HashMap<Class<?>, Class<?>>();
	private Map<Class<?>, Class<?>> _targetToDomainMap = new HashMap<Class<?>, Class<?>>();

	// -------------------------------------------------------------------------
	//
	// Constructor
	//
	// -------------------------------------------------------------------------
	/**
	 * Constructor
	 */
	public ExplicitClassMapper() {}

	// -------------------------------------------------------------------------
	//
	// Public interface
	//
	// -------------------------------------------------------------------------
	/**
	 * Add an association between source- and Clone-Class<?>, where source is the domain class that shall be cloned to
	 * the given clone class, in order to be transferable via gwt to the client.
	 * 
	 * @param sourceclass your hibernate-domain-class
	 * @param targetclass the class to be transferred to the gwt client
	 */
	public void addAssociation(Class<?> sourceclass, Class<?> targetclass) {
		_domainToTargetMap.put(sourceclass, targetclass);
		_targetToDomainMap.put(targetclass, sourceclass);
	}

	/**
	 * Set the associations between source- and Clone-Class<?>. All previous association get deleted.
	 * 
	 * @author Norman Maurer
	 * @param mappings map with sourcClass as key and targetClass as value
	 */
	public void setAssociations(Map<Class<?>, Class<?>> mappings) {
		_domainToTargetMap.clear();
		_targetToDomainMap.clear();
		Iterator<Class<?>> iter = mappings.keySet().iterator();

		while (iter.hasNext()) {
			Class<?> srcClassName = iter.next();
			Class<?> targetClassName = mappings.get(srcClassName);

			addAssociation(srcClassName, targetClassName);
		}
	}

	// -------------------------------------------------------------------------
	//
	// IClassMapper implementation
	//
	// -------------------------------------------------------------------------
	/*
	 * (non-Javadoc)
	 * @see net.sf.gilead.core.beanlib.java5.ICloneMapper#getTargetClass(java.lang .Class<?>)
	 */
	@Override
	public Class<?> getTargetClass(Class<?> sourceClass) {
		Class<?> result = _domainToTargetMap.get(sourceClass);
		if (_log.isTraceEnabled()) {
			_log.trace("Target class for " + sourceClass.getCanonicalName() + ": " + (result == null ? "null" : result.getCanonicalName()));
		}
		return result;
	}

	/*
	 * (non-Javadoc)
	 * @see net.sf.gilead.core.beanlib.java5.ICloneMapper#getTargetClass(java.lang .Class<?>)
	 */
	@Override
	public Class<?> getSourceClass(Class<?> targetClass) {
		Class<?> result = _targetToDomainMap.get(targetClass);
		if (_log.isTraceEnabled()) {
			_log.trace("Source class for " + targetClass.getCanonicalName() + ": " + (result == null ? "null" : result.getCanonicalName()));
		}
		return result;
	}
}
