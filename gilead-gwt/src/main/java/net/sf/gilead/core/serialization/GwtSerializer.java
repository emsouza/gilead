package net.sf.gilead.core.serialization;

import com.google.gwt.user.client.rpc.SerializationException;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.sf.gilead.pojo.gwt.IGwtSerializableParameter;
import net.sf.gilead.pojo.gwt.basic.BooleanParameter;
import net.sf.gilead.pojo.gwt.basic.ByteParameter;
import net.sf.gilead.pojo.gwt.basic.CharacterParameter;
import net.sf.gilead.pojo.gwt.basic.DateParameter;
import net.sf.gilead.pojo.gwt.basic.DoubleParameter;
import net.sf.gilead.pojo.gwt.basic.FloatParameter;
import net.sf.gilead.pojo.gwt.basic.IntegerParameter;
import net.sf.gilead.pojo.gwt.basic.LongParameter;
import net.sf.gilead.pojo.gwt.basic.ShortParameter;
import net.sf.gilead.pojo.gwt.basic.StringParameter;
import net.sf.gilead.pojo.gwt.collection.ListParameter;
import net.sf.gilead.pojo.gwt.collection.MapParameter;
import net.sf.gilead.pojo.gwt.collection.SetParameter;

/**
 * GWT compatible serialization. Since Object class is not allowed, we replace it with a marker interface and
 * encapsulates each basic supported types and collections in an implementation of the interface.
 *
 * @author bruno.marchesson
 */
public class GwtSerializer {

    private static final transient Logger LOGGER = LoggerFactory.getLogger(GwtSerializer.class);

    /**
     * Converts a serializable type to GWT supported encapsulation.
     *
     * @throws SerializationException if the serializable argument is not supported by GWT JRE.
     */
    public IGwtSerializableParameter convertToGwt(Serializable serializable) throws SerializationException {
        // Precondition checking
        if (serializable == null) {
            return null;
        }

        LOGGER.trace("Converting " + serializable + " to GWT supported type");

        // Type checking
        if (serializable instanceof List) {
            return convertListToGwt(serializable);
        } else if (serializable instanceof Set) {
            return convertSetToGwt(serializable);
        } else if (serializable instanceof Map) {
            return convertMapToGwt(serializable);
        } else {
            // Basic type ?
            return convertBasicToGwt(serializable);
        }
    }

    /**
     * @param serialized
     * @return
     */
    public Serializable convertFromGwt(IGwtSerializableParameter parameter) throws SerializationException {
        LOGGER.trace("Converting " + parameter + " from GWT back to serializable type");
        // Precondition checking
        if (parameter == null) {
            return null;
        }

        // Type checking
        if (parameter instanceof ListParameter) {
            return convertListFromGwt(parameter);
        } else if (parameter instanceof SetParameter) {
            return convertSetFromGwt(parameter);
        } else if (parameter instanceof MapParameter) {
            return convertMapFromGwt(parameter);
        } else {
            // Basic type ?
            return convertBasicFromGwt(parameter);
        }
    }

    /**
     * Convert the argument object to GWT serialzable IRequestParameter
     */
    protected IGwtSerializableParameter convertBasicToGwt(Serializable object) throws SerializationException {
        // Precondition checking
        if (object == null) {
            return null;
        }

        // Check basic parameters (most current first)
        if (object instanceof IGwtSerializableParameter) {
            return (IGwtSerializableParameter) object;
        }
        if (object instanceof Integer) {
            return new IntegerParameter((Integer) object);
        }
        if (object instanceof String) {
            return new StringParameter((String) object);
        }
        if (object instanceof Long) {
            return new LongParameter((Long) object);
        }
        if (object instanceof Boolean) {
            return new BooleanParameter((Boolean) object);
        }
        if (object instanceof Date) {
            return new DateParameter((Date) object);
        }
        if (object instanceof Short) {
            return new ShortParameter((Short) object);
        }
        if (object instanceof Character) {
            return new CharacterParameter((Character) object);
        }
        if (object instanceof Double) {
            return new DoubleParameter((Double) object);
        }
        if (object instanceof Float) {
            return new FloatParameter((Float) object);
        }
        if (object instanceof Byte) {
            return new ByteParameter((Byte) object);
        }
        // else : unsupported type
        throw new SerializationException("Unsupported type : " + object.getClass());
    }

    /**
     * Convert IRequestParameter back to Serializable
     *
     * @param parameter
     * @return
     */
    protected Serializable convertBasicFromGwt(IGwtSerializableParameter parameter) {
        // Just use the underlying value
        return (Serializable) parameter.getUnderlyingValue();
    }

    /**
     * Convert the argument list to GWT serializable IRequestParameter
     */
    @SuppressWarnings("unchecked")
    protected IGwtSerializableParameter convertListToGwt(Serializable object) throws SerializationException {
        // Precondition checking
        Collection<Serializable> objectList = (Collection<Serializable>) object;
        if ((objectList == null) || (objectList.isEmpty())) {
            return null;
        }

        // Create underlying list
        List<IGwtSerializableParameter> serializableCollection = null;
        if (object instanceof ArrayList) {
            serializableCollection = new ArrayList<>(objectList.size());
        } else if (object instanceof LinkedList) {
            serializableCollection = new LinkedList<>();
        } else {
            // else : unsupported GWT list
            throw new SerializationException("Unsupported collection type : " + object.getClass());
        }

        // Copy list contents
        for (Serializable item : objectList) {
            serializableCollection.add(convertToGwt(item));
        }

        return new ListParameter(serializableCollection);
    }

    /**
     * Convert the argument list from GWT IRequestParameter one
     *
     * @throws SerializationException
     */
    @SuppressWarnings("unchecked")
    protected Serializable convertListFromGwt(IGwtSerializableParameter object) throws SerializationException {
        // Precondition checking
        Collection<IGwtSerializableParameter> objectList = (Collection<IGwtSerializableParameter>) object.getUnderlyingValue();
        if ((objectList == null) || (objectList.isEmpty())) {
            return null;
        }

        // Create underlying list
        List<Serializable> serializableCollection = null;
        if (objectList instanceof ArrayList) {
            serializableCollection = new ArrayList<>(objectList.size());
        } else if (objectList instanceof LinkedList) {
            serializableCollection = new LinkedList<>();
        } else {
            // else : unsupported GWT list
            throw new SerializationException("Unsupported collection type : " + objectList.getClass());
        }

        // Copy list contents
        for (IGwtSerializableParameter item : objectList) {
            serializableCollection.add(convertFromGwt(item));
        }

        return (Serializable) serializableCollection;
    }

    /**
     * Convert the argument set to GWT serializable IRequestParameter
     */
    @SuppressWarnings("unchecked")
    protected IGwtSerializableParameter convertSetToGwt(Serializable object) throws SerializationException {
        // Precondition checking
        Collection<Serializable> objectList = (Collection<Serializable>) object;
        if ((objectList == null) || (objectList.isEmpty())) {
            return null;
        }

        // Create underlying list
        Set<IGwtSerializableParameter> serializableCollection = null;
        if (object instanceof HashSet) {
            serializableCollection = new HashSet<>(objectList.size());
        } else if (object instanceof LinkedHashSet) {
            serializableCollection = new LinkedHashSet<>(objectList.size());
        } else if (object instanceof TreeSet) {
            serializableCollection = new TreeSet<>();
        } else {
            // else : unsupported GWT set
            throw new SerializationException("Unsupported collection type : " + object.getClass());
        }

        // Copy list contents
        for (Serializable item : objectList) {
            serializableCollection.add(convertToGwt(item));
        }

        return new SetParameter(serializableCollection);
    }

    /**
     * Convert the argument set to GWT serializable IRequestParameter
     */
    @SuppressWarnings("unchecked")
    protected Serializable convertSetFromGwt(IGwtSerializableParameter object) throws SerializationException {
        // Precondition checking
        Collection<IGwtSerializableParameter> objectList = (Collection<IGwtSerializableParameter>) object.getUnderlyingValue();
        if ((objectList == null) || (objectList.isEmpty())) {
            return null;
        }

        // Create serializable set
        Set<Serializable> serializableCollection = null;
        if (objectList instanceof HashSet) {
            serializableCollection = new HashSet<>(objectList.size());
        } else if (objectList instanceof LinkedHashSet) {
            serializableCollection = new LinkedHashSet<>(objectList.size());
        } else if (objectList instanceof TreeSet) {
            serializableCollection = new TreeSet<>();
        } else {
            // else : unsupported GWT set
            throw new SerializationException("Unsupported collection type : " + objectList.getClass());
        }

        // Copy list contents
        for (IGwtSerializableParameter item : objectList) {
            serializableCollection.add(convertFromGwt(item));
        }

        return (Serializable) serializableCollection;
    }

    /**
     * Convert the argument map to GWT serializable IRequestParameter
     */
    @SuppressWarnings("unchecked")
    protected IGwtSerializableParameter convertMapToGwt(Serializable object) throws SerializationException {
        // Precondition checking
        Map<Serializable, Serializable> objectMap = (Map<Serializable, Serializable>) object;
        if ((objectMap == null) || (objectMap.isEmpty())) {
            return null;
        }

        // Create underlying list
        Map<IGwtSerializableParameter, IGwtSerializableParameter> serializableCollection = null;
        if (object instanceof HashMap) {
            serializableCollection = new HashMap<>(objectMap.size());
        } else if (object instanceof LinkedHashMap) {
            serializableCollection = new LinkedHashMap<>(objectMap.size());
        } else if (object instanceof TreeMap) {
            serializableCollection = new TreeMap<>();
        } else {
            // else : unsupported GWT map
            throw new SerializationException("Unsupported collection type : " + object.getClass());
        }

        // Copy map contents
        for (Map.Entry<Serializable, Serializable> item : objectMap.entrySet()) {
            serializableCollection.put(convertToGwt(item.getKey()), convertToGwt(item.getValue()));
        }

        return new MapParameter(serializableCollection);
    }

    /**
     * Convert the argument map from GWT serializable IRequestParameter
     */
    @SuppressWarnings("unchecked")
    protected Serializable convertMapFromGwt(IGwtSerializableParameter object) throws SerializationException {
        // Precondition checking
        Map<IGwtSerializableParameter, IGwtSerializableParameter> objectMap = (Map<IGwtSerializableParameter, IGwtSerializableParameter>) object
                .getUnderlyingValue();
        if ((objectMap == null) || (objectMap.isEmpty())) {
            return null;
        }

        // Create underlying list
        Map<Serializable, Serializable> serializableCollection = null;
        if (objectMap instanceof HashMap) {
            serializableCollection = new HashMap<>(objectMap.size());
        } else if (objectMap instanceof LinkedHashMap) {
            serializableCollection = new LinkedHashMap<>(objectMap.size());
        } else if (objectMap instanceof TreeMap) {
            serializableCollection = new TreeMap<>();
        } else {
            // else : unsupported GWT map
            throw new SerializationException("Unsupported collection type : " + object.getClass());
        }

        // Copy map contents
        for (Map.Entry<IGwtSerializableParameter, IGwtSerializableParameter> item : objectMap.entrySet()) {
            serializableCollection.put(convertFromGwt(item.getKey()), convertFromGwt(item.getValue()));
        }

        return (Serializable) serializableCollection;
    }
}
