package net.sf.gilead.pojo.gwt;

import java.util.HashMap;
import java.util.Map;

import net.sf.gilead.pojo.base.ILightEntity;

/**
 * Abstract POJO with minimal proxy informations handling
 * 
 * @author bruno.marchesson
 */
public abstract class LightEntity implements ILightEntity {

    private static final long serialVersionUID = 1061336746068017740L;

    /**
     * Map of persistence proxy informations. The key is the property name, the value is a map with persistence
     * informations filled by the persistence util implementation
     */
    protected Map<String, IGwtSerializableParameter> _proxyInformations;

    /**
     * Map of persistence initialisation state. The key is the property name, the value is a map with persistence
     * informations filled by the persistence util implementation
     */
    protected Map<String, Boolean> _initializationMap;

    /**
     * Constructor
     */
    public LightEntity() {
        super();
    }

    /**
     * Add proxy information
     */
    @Override
    public void addProxyInformation(String property, Object proxyInfo) {
        if (_proxyInformations == null) {
            _proxyInformations = new HashMap<>();
        }
        _proxyInformations.put(property, (IGwtSerializableParameter) proxyInfo);
    }

    @Override
    public void removeProxyInformation(String property) {
        if (_proxyInformations != null) {
            _proxyInformations.remove(property);
        }
    }

    @Override
    public Object getProxyInformation(String property) {
        if (_proxyInformations != null) {
            return _proxyInformations.get(property);
        } else {
            return null;
        }
    }

    @Override
    public String getDebugString() {
        if (_proxyInformations != null) {
            return _proxyInformations.toString();
        } else {
            return null;
        }
    }

    @Override
    public boolean isInitialized(String property) {
        if (_initializationMap == null) {
            return true;
        }

        Boolean initialized = _initializationMap.get(property);
        if (initialized == null) {
            return true;
        }
        return initialized.booleanValue();
    }

    @Override
    public void setInitialized(String property, boolean initialized) {
        if (_initializationMap == null) {
            _initializationMap = new HashMap<>();
        }
        _initializationMap.put(property, initialized);
    }

    @Override
    public Object getUnderlyingValue() {
        return this;
    }
}
