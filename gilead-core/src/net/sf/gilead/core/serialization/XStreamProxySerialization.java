/**
 * 
 */
package net.sf.gilead.core.serialization;

import java.io.Serializable;

import org.apache.log4j.Logger;

import com.thoughtworks.xstream.XStream;

/**
 * XStream Serialization strategy. It serializes Serializable instances to String using XStream and deserializes them
 * when back. (needed for proxy informations, since GWT does not like Serializable type in Map<String, Serializable>)
 * 
 * @author bruno.marchesson
 */
public class XStreamProxySerialization implements IProxySerialization {
	// ----
	// Attributes
	// ----
	/**
	 * Logger channel.
	 */
	private static Logger _log = Logger.getLogger(XStreamProxySerialization.class);

	/**
	 * The XStream facade
	 */
	private XStream _xstream;

	// -------------------------------------------------------------------------
	//
	// Constructor
	//
	// -------------------------------------------------------------------------
	/**
	 * Constructor.
	 */
	public XStreamProxySerialization() {
		_xstream = new XStream();
		_xstream.registerConverter(new SerializableIdConverter(_xstream));
		// _xstream.registerConverter(new
		// JavaBeanConverter(_xstream.getMapper()));
	}

	// -------------------------------------------------------------------------
	//
	// Public interface
	//
	// -------------------------------------------------------------------------
	/**
	 * Convert Serializable to bytes.
	 */
	@Override
	public Object serialize(Serializable serializable) {
		if (_log.isDebugEnabled()) {
			_log.debug("Serialization of " + serializable);
		}
		// Precondition checking
		//
		if (serializable == null) {
			return null;
		}

		// Serialize to bytes and encapsulate into string
		//
		return _xstream.toXML(serializable);
	}

	/**
	 * Regenerate Serializable from String.
	 */
	@Override
	public Serializable unserialize(Object object) {
		// Precondition checking
		//
		if (object == null) {
			return null;
		}

		if (object instanceof String == false) {
			throw new RuntimeException("Cannot unserialize object : " + object + " (was expecting a String)");
		}

		String string = (String) object;
		if (_log.isDebugEnabled()) {
			_log.debug("Unserialization of " + string);
		}

		// String checking
		//
		if (string.length() == 0) {
			return null;
		}

		// Convert back to bytes and Serializable
		//
		return (Serializable) _xstream.fromXML(string);
	}
}
