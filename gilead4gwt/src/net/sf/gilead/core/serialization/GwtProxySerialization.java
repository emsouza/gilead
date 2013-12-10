/**
 * 
 */
package net.sf.gilead.core.serialization;

import java.io.Serializable;
import java.util.logging.Level;
import java.util.logging.Logger;

import net.sf.gilead.pojo.gwt.GwtSerializableId;
import net.sf.gilead.pojo.gwt.IGwtSerializableParameter;
import net.sf.gilead.pojo.gwt.SerializedParameter;

import com.google.gwt.user.client.rpc.SerializationException;

/**
 * GWT compatible map serialization. Each basic type is encapsulated as IRequestParameter. Non basic type is serialised
 * as string and send as this
 * 
 * @author bruno.marchesson
 */
public class GwtProxySerialization extends GwtSerializer implements IProxySerialization {
    // ----
    // Attributes
    // ----
    /**
     * Logger channel
     */
    private static final Logger _log = Logger.getLogger(GwtProxySerialization.class.getSimpleName());

    /**
     * String serializer
     */
    protected IProxySerialization _stringSerializer;

    // ----
    // Properties
    // ----
    /**
     * @return the String Serializer
     */
    public IProxySerialization getStringSerializer() {
        return _stringSerializer;
    }

    /**
     * @param serializer the string serializer to set
     */
    public void setStringSerializer(IProxySerialization serializer) {
        _stringSerializer = serializer;
    }

    // ----
    // Constructor
    // ----
    /**
     * Constructor
     */
    public GwtProxySerialization() {
        _stringSerializer = new JBossProxySerialization();
    }

    // -------------------------------------------------------------------------
    //
    // IProxySerialization implementation
    //
    // -------------------------------------------------------------------------
    /*
     * (non-Javadoc)
     * @see net.sf.gilead.core.serialization.IProxySerialization#serialize(java.io .Serializable)
     */
    @Override
    public Object serialize(Serializable serializable) {
        // Precondition checking
        //
        if (serializable == null) {
            return null;
        }

        // Convert to GWT
        //
        try {
            return convertToGwt(serializable);
        } catch (SerializationException ex) {
            // should not happen
            throw new RuntimeException(ex);
        }
    }

    /*
     * (non-Javadoc)
     * @see net.sf.gilead.core.serialization.IProxySerialization#unserialize(java .lang.Object)
     */
    @Override
    public Serializable unserialize(Object serialized) {
        // Precondition checking
        //
        if (serialized == null) {
            return null;
        }

        // Convert from GWT
        //
        try {
            return convertFromGwt((IGwtSerializableParameter) serialized);
        } catch (SerializationException ex) {
            // should not happen
            throw new RuntimeException(ex);
        }
    }

    // ------------------------------------------------------------------------
    //
    // Overridden methods
    //
    // ------------------------------------------------------------------------
    /*
     * (non-Javadoc)
     * @see net.sf.gilead.core.serialization.GwtSerializer#convertBasicToGwt(java .io.Serializable)
     */
    @Override
    protected IGwtSerializableParameter convertBasicToGwt(Serializable object) throws SerializationException {
        // SerializableId handling
        //
        if (object instanceof SerializableId) {
            SerializableId serializableId = (SerializableId) object;
            GwtSerializableId gwtSerializableId = new GwtSerializableId();
            gwtSerializableId.setEntityName(serializableId.getEntityName());
            gwtSerializableId.setStringValue(serializableId.getValue());

            if (serializableId.getId() != null) {
                gwtSerializableId.setId(convertBasicToGwt(serializableId.getId()));
            }

            return gwtSerializableId;
        }

        // Basic case
        //
        try {
            return super.convertBasicToGwt(object);
        } catch (SerializationException ex) {
            _log.log(Level.FINE, object.getClass() + " not serializable => convert to string");
            return new SerializedParameter((String) _stringSerializer.serialize(object));
        }
    }

    /*
     * (non-Javadoc)
     * @see net.sf.gilead.core.serialization.GwtSerializer#convertBasicFromGwt(net
     * .sf.gilead.gwt.client.parameters.IRequestParameter)
     */
    @Override
    protected Serializable convertBasicFromGwt(IGwtSerializableParameter parameter) {
        // Serialized case
        //
        if (parameter instanceof SerializedParameter) {
            return _stringSerializer.unserialize(parameter.getUnderlyingValue());
        } else if (parameter instanceof GwtSerializableId) {
            // Re-create serializable Id
            //
            GwtSerializableId gwtSerializableId = (GwtSerializableId) parameter;
            SerializableId serializableId = new SerializableId();
            serializableId.setEntityName(gwtSerializableId.getEntityName());
            serializableId.setValue(gwtSerializableId.getStringValue());
            if (gwtSerializableId.getId() != null) {
                serializableId.setId(convertBasicFromGwt(gwtSerializableId.getId()));
            }

            return serializableId;
        } else {
            return super.convertBasicFromGwt(parameter);
        }
    }

    /*
     * (non-Javadoc)
     * @see net.sf.gilead.core.serialization.GwtSerializer#convertListToGwt(java. io.Serializable)
     */
    @Override
    protected IGwtSerializableParameter convertListToGwt(Serializable object) throws SerializationException {
        try {
            return super.convertListToGwt(object);
        } catch (SerializationException ex) {
            _log.log(Level.FINE, object.getClass() + " not serializable => convert to string");
            return new SerializedParameter((String) _stringSerializer.serialize(object));
        }
    }

    /*
     * (non-Javadoc)
     * @see net.sf.gilead.core.serialization.GwtSerializer#convertListFromGwt(net
     * .sf.gilead.gwt.client.parameters.IRequestParameter)
     */
    @Override
    protected Serializable convertListFromGwt(IGwtSerializableParameter parameter) throws SerializationException {
        if (parameter instanceof SerializedParameter) {
            return _stringSerializer.unserialize(parameter.getUnderlyingValue());
        } else {
            return super.convertListFromGwt(parameter);
        }
    }

    /*
     * (non-Javadoc)
     * @see net.sf.gilead.core.serialization.GwtSerializer#convertSetToGwt(java.io .Serializable)
     */
    @Override
    protected IGwtSerializableParameter convertSetToGwt(Serializable object) throws SerializationException {
        try {
            return super.convertSetToGwt(object);
        } catch (SerializationException ex) {
            _log.log(Level.FINE, object.getClass() + " not serializable => convert to string");
            return new SerializedParameter((String) _stringSerializer.serialize(object));
        }
    }

    /*
     * (non-Javadoc)
     * @see net.sf.gilead.core.serialization.GwtSerializer#convertSetFromGwt(net.
     * sf.gilead.gwt.client.parameters.IRequestParameter)
     */
    @Override
    protected Serializable convertSetFromGwt(IGwtSerializableParameter parameter) throws SerializationException {
        if (parameter instanceof SerializedParameter) {
            return _stringSerializer.unserialize(parameter.getUnderlyingValue());
        } else {
            return super.convertSetFromGwt(parameter);
        }
    }

    /*
     * (non-Javadoc)
     * @see net.sf.gilead.core.serialization.GwtSerializer#convertMapToGwt(java.io .Serializable)
     */
    @Override
    protected IGwtSerializableParameter convertMapToGwt(Serializable object) throws SerializationException {
        try {
            return super.convertMapToGwt(object);
        } catch (SerializationException ex) {
            _log.log(Level.FINE, object.getClass() + " not serializable => convert to string");
            return new SerializedParameter((String) _stringSerializer.serialize(object));
        }
    }

    @Override
    protected Serializable convertMapFromGwt(IGwtSerializableParameter parameter) throws SerializationException {
        if (parameter instanceof SerializedParameter) {
            return _stringSerializer.unserialize(parameter.getUnderlyingValue());
        } else {
            return super.convertMapFromGwt(parameter);
        }
    }
}
