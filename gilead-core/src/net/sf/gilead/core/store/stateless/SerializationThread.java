/**
 *
 */
package net.sf.gilead.core.store.stateless;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import net.sf.gilead.core.serialization.IProxySerialization;
import net.sf.gilead.pojo.base.ILightEntity;

import org.apache.log4j.Logger;

/**
 * Thread for handling proxy information serialization. (Performance purpose)
 * 
 * @author
 */
public class SerializationThread implements Runnable {
	// ----
	// Attribute
	// -----
	/**
	 * Logger channel
	 */
	private static final Logger _log = Logger.getLogger(SerializationThread.class);

	/**
	 * Serializer for proxy informations
	 */
	private IProxySerialization _proxySerializer;

	/**
	 * Serialization item list
	 */
	private BlockingQueue<SerializationItem> _itemList;

	/**
	 * Running flag
	 */
	private boolean _running;

	/**
	 * @return the proxy serializer
	 */
	public IProxySerialization getProxySerializer() {
		return _proxySerializer;
	}

	/**
	 * @param serializer the serializer to set
	 */
	public void setProxySerializer(IProxySerialization serializer) {
		_proxySerializer = serializer;
	}

	/**
	 * @param running the running state
	 */
	public void setRunning(boolean running) {
		_running = false;
	}

	// -------------------------------------------------------------------------
	//
	// Constructor
	//
	// -------------------------------------------------------------------------
	/**
	 * Constructor
	 */
	public SerializationThread() {
		_itemList = new LinkedBlockingQueue<SerializationItem>(10);
		_running = true;
	}

	// ------------------------------------------------------------------------
	//
	// Public interface
	//
	// ------------------------------------------------------------------------
	/**
	 * Add serialization item
	 */
	public void serialize(ILightEntity entity, String propertyName, Map<String, Serializable> proxyInfo) {
		SerializationItem item = new SerializationItem();

		item.entity = entity;
		item.propertyName = propertyName;
		item.proxyInfo = proxyInfo;

		try {
			_itemList.put(item);
		} catch (InterruptedException e) {
			// No matter
		}
	}

	/**
	 * Inidcates if serialization is finished or not
	 * 
	 * @return
	 */
	public boolean isSerializationFinished() {
		return _itemList.isEmpty();
	}

	/*
	 * (non-Javadoc)
	 * @see java.lang.Runnable#run()
	 */
	@Override
	public void run() {
		while (_running) {
			try {
				SerializationItem item = _itemList.poll(10, TimeUnit.MILLISECONDS);
				if (item != null) {
					if (item.proxyInfo == null) {
						item.entity.addProxyInformation(item.propertyName, null);
					} else {
						Object serialized = item.proxyInfo;
						if (_proxySerializer != null) {
							// Serialization needed
							//
							_proxySerializer.serialize((HashMap<String, Serializable>) item.proxyInfo);
						}
						item.entity.addProxyInformation(item.propertyName, serialized);
					}
				}
			} catch (InterruptedException e) {
				// Not matter
			} catch (Throwable ex) {
				_log.error(ex.getMessage(), ex);
			}
		}
	}

}

/**
 * Serialization item.
 * 
 * @author bruno.marchesson
 */
class SerializationItem {
	public ILightEntity entity;
	public String propertyName;
	public Map<String, Serializable> proxyInfo;
}
