package net.sf.gilead.core.beanlib.transformer;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.List;

import net.sf.beanlib.spi.BeanTransformerSpi;
import net.sf.beanlib.spi.CustomBeanTransformerSpi;

import org.apache.log4j.Logger;

/**
 * Factory for gilead custom transformers
 * 
 * @author Alexandre Pretyman, Bruno Marchesson
 */
public class CustomTransformersFactory {
	// ----
	// Attributes
	// ----
	/**
	 * Logger channel
	 */
	private static Logger _log = Logger.getLogger(CustomTransformersFactory.class);

	/**
	 * Singleton instance
	 */
	private static CustomTransformersFactory _instance = null;

	/**
	 * List of custom bean transformers constructors for clone operation
	 */
	private List<Constructor<CustomBeanTransformerSpi>> _cloneTransformersList;

	/**
	 * List of custom bean transformers constructors for merge operation
	 */
	private List<Constructor<CustomBeanTransformerSpi>> _mergeTransformersList;

	// ----
	// Singleton
	// ----
	/**
	 * @return the unique instance of the factory
	 */
	public static synchronized CustomTransformersFactory getInstance() {
		if (_instance == null) {
			_instance = new CustomTransformersFactory();
		}
		return _instance;
	}

	// -------------------------------------------------------------------------
	//
	// Constructor
	//
	// -------------------------------------------------------------------------
	/**
	 * Private constructor
	 */
	private CustomTransformersFactory() {
		_cloneTransformersList = new ArrayList<Constructor<CustomBeanTransformerSpi>>();
		_mergeTransformersList = new ArrayList<Constructor<CustomBeanTransformerSpi>>();

		// Transformers needed for Gilead
		//
		addCustomBeanTransformer(TimestampCustomTransformer.class);
		addCustomBeanTransformer(StackTraceElementCustomTransformer.class);
	}

	// -------------------------------------------------------------------------
	//
	// Public interface
	//
	// -------------------------------------------------------------------------
	/**
	 * Add a custom bean transformer for both clone and merge
	 */
	@SuppressWarnings("unchecked")
	public void addCustomBeanTransformer(Class transformerClass) {
		Constructor<CustomBeanTransformerSpi> constructor = getConstructorFor(transformerClass);
		_cloneTransformersList.add(constructor);
		_mergeTransformersList.add(constructor);
	}

	/**
	 * Add a custom bean transformer for clone operation
	 */
	@SuppressWarnings("unchecked")
	public void addCloneCustomBeanTransformer(Class transformerClass) {
		Constructor<CustomBeanTransformerSpi> constructor = getConstructorFor(transformerClass);
		_cloneTransformersList.add(constructor);
	}

	/**
	 * Add a custom bean transformer for merge operation
	 */
	@SuppressWarnings("unchecked")
	public void addMergeCustomBeanTransformer(Class transformerClass) {
		Constructor<CustomBeanTransformerSpi> constructor = getConstructorFor(transformerClass);
		_mergeTransformersList.add(constructor);
	}

	/**
	 * Create a union transformer for clone if needed
	 * 
	 * @param beanTransformer the input bean transformer
	 * @return the beanlib CustomBeanTransformer.
	 */
	public CustomBeanTransformerSpi createUnionCustomBeanTransformerForClone(BeanTransformerSpi beanTransformer) {
		int transformerCount = _cloneTransformersList.size();
		if (transformerCount == 1) {
			// No additional custom transformer defined : just use the one
			//
			return instantiate(_cloneTransformersList.get(0), beanTransformer);
		} else {
			// Create each transformer
			//
			CustomBeanTransformerSpi[] customBeanTransformers = new CustomBeanTransformerSpi[transformerCount];
			for (int index = 0; index < transformerCount; index++) {
				customBeanTransformers[index] = instantiate(_cloneTransformersList.get(index), beanTransformer);
			}

			return new UnionCustomBeanTransformer(customBeanTransformers);
		}
	}

	/**
	 * Create a union transformer for merge if needed
	 * 
	 * @param beanTransformer the input bean transformer
	 * @return the beanlib CustomBeanTransformer.
	 */
	public CustomBeanTransformerSpi createUnionCustomBeanTransformerForMerge(BeanTransformerSpi beanTransformer) {
		int transformerCount = _mergeTransformersList.size();
		if (transformerCount == 1) {
			// No additional custom transformer defined : just use the one
			//
			return instantiate(_mergeTransformersList.get(0), beanTransformer);
		} else {
			// Create each transformer
			//
			CustomBeanTransformerSpi[] customBeanTransformers = new CustomBeanTransformerSpi[transformerCount];
			for (int index = 0; index < transformerCount; index++) {
				customBeanTransformers[index] = instantiate(_mergeTransformersList.get(index), beanTransformer);
			}

			return new UnionCustomBeanTransformer(customBeanTransformers);
		}
	}

	// -------------------------------------------------------------------------
	//
	// Internal methods
	//
	// -------------------------------------------------------------------------
	/**
	 * Get constructor for the argument class
	 * 
	 * @param clazz the custom bean transformer class
	 * @return the constructor that take a BeanTransformerSpi as argument from the class
	 */
	protected Constructor<CustomBeanTransformerSpi> getConstructorFor(Class<CustomBeanTransformerSpi> clazz) {
		// Constructor has a transformer argument
		//
		Class<?>[] ctArg = { BeanTransformerSpi.class };

		try {
			// Search for constructors with BeanTransformerSpi argument
			return clazz.getConstructor(ctArg);
		} catch (Exception e) {
			_log.error("Cannot find constructor with BeanTransformerSpi argument for class " + clazz, e);
			throw new RuntimeException("Error retrieving constructor for " + clazz, e);
		}
	}

	/**
	 * Create a new transformer instance
	 * 
	 * @param constructor the constructor
	 * @param beanTransformer the bean transformer needed argument
	 * @return the CustomBeanTransformer
	 */
	protected CustomBeanTransformerSpi instantiate(Constructor<CustomBeanTransformerSpi> constructor, final BeanTransformerSpi beanTransformer) {
		// Constructor argument
		Object[] initArgs = { beanTransformer };

		// New instance of custom transformer
		try {
			return constructor.newInstance(initArgs);
		} catch (Exception e) {
			// Throw unrecoverable error on exception
			//
			_log.error("Transformer initialization error", e);
			throw new RuntimeException("Transformer initialization error", e);
		}
	}
}
