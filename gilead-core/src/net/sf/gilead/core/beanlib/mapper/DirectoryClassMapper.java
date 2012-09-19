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
import java.util.Map;

import net.sf.gilead.core.beanlib.IClassMapper;

import org.apache.log4j.Logger;

/**
 * Class mapper based on package hierarchy (Domain and DTO must have the same name and placed in identified packages)
 * 
 * @author bruno.marchesson
 */
public class DirectoryClassMapper implements IClassMapper {
	// ----
	// Attributes
	// ----
	/**
	 * Logger channel
	 */
	private static Logger _log = Logger.getLogger(DirectoryClassMapper.class);

	/**
	 * The root package of all Domain classes
	 */
	private String _rootDomainPackage;

	/**
	 * The root package of all clone classes
	 */
	private String _rootClonePackage;

	/**
	 * Suffix for clone classes (can be null)
	 */
	private String _cloneSuffix;

	/**
	 * The map of the source and target correspondance
	 */
	private Map<Class<?>, Class<?>> _sourceTargetMap;

	/**
	 * Inverse map of the source and target correspondance
	 */
	private Map<Class<?>, Class<?>> _targetSourceMap;

	// ----
	// Properties
	// ----
	/**
	 * @return the _rootClonePackage
	 */
	public String getRootClonePackage() {
		return _rootClonePackage;
	}

	/**
	 * @param clonePackage the _rootClonePackage to set
	 */
	public void setRootClonePackage(String clonePackage) {
		_rootClonePackage = clonePackage;
	}

	/**
	 * @return the _rootDomainPackage
	 */
	public String getRootDomainPackage() {
		return _rootDomainPackage;
	}

	/**
	 * @param domainPackage the _rootDomainPackage to set
	 */
	public void setRootDomainPackage(String domainPackage) {
		_rootDomainPackage = domainPackage;
	}

	/**
	 * @return the cloneSuffix
	 */
	public String getCloneSuffix() {
		return _cloneSuffix;
	}

	/**
	 * @param suffix the cloneSuffix to set
	 */
	public void setCloneSuffix(String suffix) {
		_cloneSuffix = suffix;
	}

	// -------------------------------------------------------------------------
	//
	// Constructor
	//
	// -------------------------------------------------------------------------
	/**
	 * Constructor
	 */
	public DirectoryClassMapper() {
		_sourceTargetMap = new HashMap<Class<?>, Class<?>>();
		_targetSourceMap = new HashMap<Class<?>, Class<?>>();
	}

	// -------------------------------------------------------------------------
	//
	// Public interface
	//
	// -------------------------------------------------------------------------
	/*
	 * (non-Javadoc)
	 * @see net.sf.gilead.core.beanlib.mapper.ICloneMapper#getTargetClass(java.lang .Class<?>)
	 */
	@Override
	public Class<?> getTargetClass(Class<?> sourceClass) {
		// Precondition checking
		//
		if (sourceClass == null) {
			return null;
		}

		String sourceClassName = sourceClass.getCanonicalName();
		if (sourceClassName.startsWith(_rootDomainPackage) == false) {
			// Not a souce Class<?>
			//
			return null;
		}

		synchronized (this) {
			// Is the correspondance already computed ?
			//
			Class<?> targetClass = _sourceTargetMap.get(sourceClass);
			if (targetClass != null) {
				return targetClass;
			}

			// Compute target Class<?> name
			//
			String targetClassName = null;
			String suffix = sourceClassName.substring(_rootDomainPackage.length());
			targetClassName = _rootClonePackage + suffix;
			if (_cloneSuffix != null) {
				// Add clone suffix
				//
				targetClassName += _cloneSuffix;
			}

			// Instantiate target Class<?>
			//
			_log.info("Source Class name is " + sourceClassName);
			_log.info("Computed target Class name is " + targetClassName);

			try {
				targetClass = Class.forName(targetClassName);
			} catch (ClassNotFoundException e) {
				_log.debug("Target Class does not exist : " + targetClassName, e);
				return null;
			}

			// Add both source and target Class<?> to the clone map
			//
			_sourceTargetMap.put(sourceClass, targetClass);
			_targetSourceMap.put(targetClass, sourceClass);

			return targetClass;
		}
	}

	/*
	 * (non-Javadoc)
	 * @see net.sf.gilead.core.beanlib.mapper.ICloneMapper#getTargetClass(java.lang .Class<?>)
	 */
	@Override
	public Class<?> getSourceClass(Class<?> targetClass) {
		// Precondition checking
		//
		if (targetClass == null) {
			return null;
		}

		// Compute source Class<?> name
		//
		String targetClassName = targetClass.getCanonicalName();
		if ((targetClassName == null) || (targetClassName.startsWith(_rootClonePackage) == false)) {
			// Not a target Class<?>
			//
			return null;
		}

		// Suffix verification
		//
		if ((_cloneSuffix != null) && (targetClassName.endsWith(_cloneSuffix) == false)) {
			// Not a target Class<?>
			//
			return null;
		}

		// Is the correspondance already computed ?
		//
		// work on copy to prevent syncrhnoisation issue
		String cloneSuffix = _cloneSuffix;
		synchronized (this) {
			Class<?> sourceClass = _targetSourceMap.get(targetClass);
			if (sourceClass != null) {
				return sourceClass;
			}

			// Compute source Class<?> name
			//
			String sourceClassName = null;
			String suffix = targetClassName.substring(_rootClonePackage.length());
			sourceClassName = _rootDomainPackage + suffix;

			if ((cloneSuffix != null) && (sourceClassName.endsWith(cloneSuffix))) {
				// Remove clone suffix
				//
				sourceClassName = sourceClassName.substring(0, sourceClassName.length() - cloneSuffix.length());
			}

			// Instantiate target Class<?>
			//
			_log.info("Target Class name is " + targetClassName);
			_log.info("Computed source Class name is " + sourceClassName);

			try {
				sourceClass = Class.forName(sourceClassName);
			} catch (ClassNotFoundException e) {
				_log.debug("Source Class does not exist : " + sourceClassName, e);
				return null;
			}

			// Add both source and target class to the clone map
			//
			_targetSourceMap.put(targetClass, sourceClass);
			_sourceTargetMap.put(sourceClass, targetClass);

			return sourceClass;
		}
	}
}
