package net.sf.gilead.core.serialization;

import com.google.gwt.user.client.rpc.SerializationException;

import java.io.Serializable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.sf.gilead.pojo.gwt.GwtSerializableId;
import net.sf.gilead.pojo.gwt.IGwtSerializableParameter;
import net.sf.gilead.pojo.gwt.SerializedParameter;

/**
 * GWT compatible map serialization. Each basic type is encapsulated as IRequestParameter. Non basic type is serialised
 * as string and send as this
 *
 * @author bruno.marchesson
 */
public class GwtProxySerialization extends GwtSerializer implements IProxySerialization {

    private static final transient Logger LOGGER = LoggerFactory.getLogger(GwtProxySerialization.class);

    /**
     * String serializer
     */
    protected IProxySerialization stringSerializer;

    /**
     * @return the String Serializer
     */
    public IProxySerialization getStringSerializer() {
        return stringSerializer;
    }

    /**
     * @param serializer the string serializer to set
     */
    public void setStringSerializer(IProxySerialization stringSerializer) {
        this.stringSerializer = stringSerializer;
    }

    /**
     * Constructor
     */
    public GwtProxySerialization() {
        stringSerializer = new JBossProxySerialization();
    }

    @Override
    public Object serialize(Serializable serializable) {
        // Precondition checking
        if (serializable == null) {
            return null;
        }

        // Convert to GWT
        try {
            return convertToGwt(serializable);
        } catch (SerializationException ex) {
            // should not happen
            throw new RuntimeException(ex);
        }
    }

    @Override
    public Serializable unserialize(Object serialized) {
        // Precondition checking
        if (serialized == null) {
            return null;
        }

        // Convert from GWT
        try {
            return convertFromGwt((IGwtSerializableParameter) serialized);
        } catch (SerializationException ex) {
            // should not happen
            throw new RuntimeException(ex);
        }
    }

    @Override
    protected IGwtSerializableParameter convertBasicToGwt(Serializable object) throws SerializationException {
        // SerializableId handling
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
        try {
            return super.convertBasicToGwt(object);
        } catch (SerializationException ex) {
            LOGGER.trace(object.getClass() + " not serializable => convert to string");
            return new SerializedParameter((String) stringSerializer.serialize(object));
        }
    }

    @Override
    protected Serializable convertBasicFromGwt(IGwtSerializableParameter parameter) {
        // Serialized case
        if (parameter instanceof SerializedParameter) {
            return stringSerializer.unserialize(parameter.getUnderlyingValue());
        } else if (parameter instanceof GwtSerializableId) {
            // Re-create serializable Id
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

    @Override
    protected IGwtSerializableParameter convertListToGwt(Serializable object) throws SerializationException {
        try {
            return super.convertListToGwt(object);
        } catch (SerializationException ex) {
            LOGGER.trace(object.getClass() + " not serializable => convert to string");
            return new SerializedParameter((String) stringSerializer.serialize(object));
        }
    }

    @Override
    protected Serializable convertListFromGwt(IGwtSerializableParameter parameter) throws SerializationException {
        if (parameter instanceof SerializedParameter) {
            return stringSerializer.unserialize(parameter.getUnderlyingValue());
        } else {
            return super.convertListFromGwt(parameter);
        }
    }

    @Override
    protected IGwtSerializableParameter convertSetToGwt(Serializable object) throws SerializationException {
        try {
            return super.convertSetToGwt(object);
        } catch (SerializationException ex) {
            LOGGER.trace(object.getClass() + " not serializable => convert to string");
            return new SerializedParameter((String) stringSerializer.serialize(object));
        }
    }

    @Override
    protected Serializable convertSetFromGwt(IGwtSerializableParameter parameter) throws SerializationException {
        if (parameter instanceof SerializedParameter) {
            return stringSerializer.unserialize(parameter.getUnderlyingValue());
        } else {
            return super.convertSetFromGwt(parameter);
        }
    }

    @Override
    protected IGwtSerializableParameter convertMapToGwt(Serializable object) throws SerializationException {
        try {
            return super.convertMapToGwt(object);
        } catch (SerializationException ex) {
            LOGGER.trace(object.getClass() + " not serializable => convert to string");
            return new SerializedParameter((String) stringSerializer.serialize(object));
        }
    }

    @Override
    protected Serializable convertMapFromGwt(IGwtSerializableParameter parameter) throws SerializationException {
        if (parameter instanceof SerializedParameter) {
            return stringSerializer.unserialize(parameter.getUnderlyingValue());
        } else {
            return super.convertMapFromGwt(parameter);
        }
    }
}
