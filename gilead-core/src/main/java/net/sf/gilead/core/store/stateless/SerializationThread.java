package net.sf.gilead.core.store.stateless;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.sf.gilead.core.serialization.IProxySerialization;
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
    private IProxySerialization proxySerializer;

    /**
     * Serialization item list
     */
    private BlockingQueue<SerializationItem> itemList;

    /**
     * Running flag
     */
    private boolean running;

    /**
     * Constructor
     */
    public SerializationThread() {
        itemList = new LinkedBlockingQueue<>(10);
        running = true;
    }

    /**
     * @return the proxy serializer
     */
    public IProxySerialization getProxySerializer() {
        return proxySerializer;
    }

    /**
     * @param serializer the serializer to set
     */
    public void setProxySerializer(IProxySerialization proxySerializer) {
        this.proxySerializer = proxySerializer;
    }

    /**
     * @param running the running state
     */
    public void setRunning(boolean running) {
        this.running = false;
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
        while (running) {
            try {
                SerializationItem item = itemList.poll(10, TimeUnit.MILLISECONDS);
                if (item != null) {
                    if (item.proxyInfo == null) {
                        item.entity.addProxyInformation(item.propertyName, null);
                    } else {
                        Object serialized = item.proxyInfo;
                        if (proxySerializer != null) {
                            // Serialization needed
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
