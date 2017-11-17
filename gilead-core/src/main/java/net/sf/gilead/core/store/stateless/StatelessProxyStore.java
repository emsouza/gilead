package net.sf.gilead.core.store.stateless;

import java.io.Serializable;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.sf.gilead.core.serialization.IProxySerialization;
import net.sf.gilead.core.store.ProxyStore;
import net.sf.gilead.exception.ProxyStoreException;
import net.sf.gilead.pojo.base.ILightEntity;

/**
 * Stateless proxy store. The proxy informations is stored on the pojo, by implementing the ILightEntity interface.
 *
 * @see ILightEntity
 * @author bruno.marchesson
 */
public class StatelessProxyStore implements ProxyStore {

    private static final Logger LOGGER = LoggerFactory.getLogger(StatelessProxyStore.class);

    /**
     * Serializer for proxy informations
     */
    private IProxySerialization _proxySerializer;

    /**
     * Use separate serialization thread
     */
    private boolean _useSerializationThread;

    /**
     * Separate serialization thread
     */
    private ThreadLocal<SerializationThread> _serializationThread;

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
     * @return the _useSerializationThread
     */
    public boolean getUseSerializationThread() {
        return _useSerializationThread;
    }

    /**
     * @param serializationThread the _useSerializationThread to set
     */
    public void setUseSerializationThread(boolean serializationThread) {
        _useSerializationThread = serializationThread;
    }

    // -------------------------------------------------------------------------
    //
    // Constructor
    //
    // -------------------------------------------------------------------------
    /**
     * Constructor
     */
    public StatelessProxyStore() {
        // default value
        _serializationThread = new ThreadLocal<>();
        _useSerializationThread = false;
    }

    // -------------------------------------------------------------------------
    //
    // ProxyStore implementation
    //
    // -------------------------------------------------------------------------
    /*
     * (non-Javadoc)
     * @see net.sf.gilead.core.store.ProxyStore#storeProxyInformations(java.lang .Object, java.lang.String,
     * java.util.Map)
     */
    @Override
    public void storeProxyInformations(Object cloneBean, Object persistentBean, String property, Map<String, Serializable> proxyInformations) {

        // ILightEntity checking
        if (!(cloneBean instanceof ILightEntity)) {
            throw new ProxyStoreException("Class " + cloneBean.getClass() + " must implements ILightEntity interface !", cloneBean);
        }

        // Extract initialization info
        if (proxyInformations != null) {
            Boolean initialized = (Boolean) proxyInformations.get(ILightEntity.INITIALISED);
            if (initialized != null) {
                ((ILightEntity) cloneBean).setInitialized(property, initialized.booleanValue());

                // remove from map
                proxyInformations.remove(ILightEntity.INITIALISED);
            }
        }

        // Store information in the POJO
        if (!_useSerializationThread) {
            ((ILightEntity) cloneBean).addProxyInformation(property, convertMap(proxyInformations));
        } else {
            getSerializationThread().serialize((ILightEntity) cloneBean, property, proxyInformations);
        }
    }

    /*
     * (non-Javadoc)
     * @see net.sf.gilead.core.store.ProxyStore#removeProxyInformations(java.lang .Object, java.lang.String)
     */
    @Override
    public void removeProxyInformations(Object pojo, String property) {
        // ILightEntity checking
        //
        if (pojo instanceof ILightEntity) {
            // Remove information from the POJO
            //
            ((ILightEntity) pojo).removeProxyInformation(property);
        }
    }

    /*
     * (non-Javadoc)
     * @see net.sf.gilead.core.store.ProxyStore#getProxyInformations(java.lang.Object , java.lang.String)
     */
    @Override
    public Map<String, Serializable> getProxyInformations(Object pojo, String property) {
        // ILightEntity checking
        if (!(pojo instanceof ILightEntity)) {
            return null;
        }

        Map<String, Serializable> proxyInformations = convertToSerializable(((ILightEntity) pojo).getProxyInformation(property));

        // Add initialization information
        boolean initialized = ((ILightEntity) pojo).isInitialized(property);
        if (!initialized) {
            proxyInformations.put(ILightEntity.INITIALISED, Boolean.valueOf(false));
        }

        return proxyInformations;
    }

    /**
     * Clean up the proxy store after a complete serialization process
     */
    @Override
    public void cleanUp() {
        if ((_useSerializationThread == true) && (_serializationThread.get() != null)) {
            LOGGER.trace("Cleaning up serialization thread");
            SerializationThread thread = getSerializationThread();

            // Wait for end of serialization
            while (!thread.isSerializationFinished()) {
                try {
                    Thread.sleep(50);
                } catch (InterruptedException e) {
                    // does not matter
                }
            }
            thread.setRunning(false);
            _serializationThread.set(null);
        }
    }

    // -------------------------------------------------------------------------
    //
    // Internal methods
    //
    // -------------------------------------------------------------------------
    /**
     * Convert Map<String,Serializable> to Map<String, Object>
     */
    protected Object convertMap(Map<String, Serializable> map) {
        // Precondition checking
        //
        if (map == null) {
            return null;
        }

        // Convert map
        //
        if (_proxySerializer == null) {
            // No serialization needed
            //
            return map;
        } else {
            return _proxySerializer.serialize((Serializable) map);
        }
    }

    /**
     * Convert Map<String,bytes> to Map<String, Serializable>
     */
    @SuppressWarnings("unchecked")
    protected Map<String, Serializable> convertToSerializable(Object serialized) {
        // Precondition checking
        //
        if (serialized == null) {
            return null;
        }

        // Convert map
        //
        if (_proxySerializer == null) {
            // No serialization
            //
            return (Map<String, Serializable>) serialized;
        } else {
            return (Map<String, Serializable>) _proxySerializer.unserialize(serialized);
        }
    }

    /**
     * @return the serialization thread.
     */
    protected SerializationThread getSerializationThread() {
        SerializationThread thread = _serializationThread.get();
        if (thread == null) {
            thread = new SerializationThread();
            thread.setProxySerializer(_proxySerializer);
            new Thread(thread).start();
            _serializationThread.set(thread);
        }

        return thread;
    }
}
