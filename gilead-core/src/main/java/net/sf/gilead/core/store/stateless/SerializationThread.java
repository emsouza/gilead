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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.sf.gilead.core.serialization.ProxySerialization;
import net.sf.gilead.pojo.base.ILightEntity;

/**
 * Thread for handling proxy information serialization. (Performance purpose)
 *
 * @author
 */
public class SerializationThread implements Runnable {

    private static final Logger LOGGER = LoggerFactory.getLogger(SerializationThread.class);

    /**
     * Serializer for proxy informations
     */
    private ProxySerialization proxySerializer;

    /**
     * Serialization item list
     */
    private BlockingQueue<SerializationItem> itemList;

    /**
     * Running flag
     */
    private boolean _running;

    /**
     * @return the proxy serializer
     */
    public ProxySerialization getProxySerializer() {
        return proxySerializer;
    }

    /**
     * @param serializer the serializer to set
     */
    public void setProxySerializer(ProxySerialization serializer) {
        proxySerializer = serializer;
    }

    /**
     * @param running the running state
     */
    public void setRunning(boolean running) {
        _running = false;
    }

    /**
     * Constructor
     */
    public SerializationThread() {
        itemList = new LinkedBlockingQueue<SerializationItem>(10);
        _running = true;
    }

    /**
     * Add serialization item
     */
    public void serialize(ILightEntity entity, String propertyName, Map<String, Serializable> proxyInfo) {
        SerializationItem item = new SerializationItem();

        item.entity = entity;
        item.propertyName = propertyName;
        item.proxyInfo = proxyInfo;

        try {
            itemList.put(item);
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
        return itemList.isEmpty();
    }

    @Override
    public void run() {
        while (_running) {
            try {
                SerializationItem item = itemList.poll(10, TimeUnit.MILLISECONDS);
                if (item != null) {
                    if (item.proxyInfo == null) {
                        item.entity.addProxyInformation(item.propertyName, null);
                    } else {
                        Object serialized = item.proxyInfo;
                        if (proxySerializer != null) {
                            // Serialization needed
                            //
                            proxySerializer.serialize((HashMap<String, Serializable>) item.proxyInfo);
                        }
                        item.entity.addProxyInformation(item.propertyName, serialized);
                    }
                }
            } catch (InterruptedException e) {
                // Not matter
            } catch (Throwable ex) {
                LOGGER.error(ex.getMessage(), ex);
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
