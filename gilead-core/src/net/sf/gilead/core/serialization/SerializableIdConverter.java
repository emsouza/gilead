package net.sf.gilead.core.serialization;

import java.io.Serializable;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;

/**
 * SerializableId XStream converter (performance improvement)
 * 
 * @author bruno.marchesson
 */
public class SerializableIdConverter implements Converter {
	// ----
	// Attributes
	// ----
	/**
	 * The XStream facade
	 */
	private XStream _xstream;

	/**
	 * Constructor
	 */
	public SerializableIdConverter(XStream xstream) {
		_xstream = xstream;
	}

	// -------------------------------------------------------------------------
	//
	// Converter implementation
	//
	// -------------------------------------------------------------------------
	/**
	 * (non-Javadoc)
	 * 
	 * @see com.thoughtworks.xstream.converters.ConverterMatcher#canConvert(java.lang.Class)
	 */
	@Override
	public boolean canConvert(Class clazz) {
		return clazz.equals(SerializableId.class);
	}

	/**
	 * (non-Javadoc)
	 * 
	 * @see com.thoughtworks.xstream.converters.Converter#marshal(java.lang.Object,
	 *      com.thoughtworks.xstream.io.HierarchicalStreamWriter,
	 *      com.thoughtworks.xstream.converters.MarshallingContext)
	 */
	@Override
	public void marshal(Object value, HierarchicalStreamWriter writer, MarshallingContext context) {
		SerializableId sId = (SerializableId) value;

		writer.startNode("id");
		writer.setValue(_xstream.toXML(sId.getId()));
		writer.endNode();

		writer.startNode("cn");
		writer.setValue(sId.getEntityName());
		writer.endNode();
	}

	/**
	 * (non-Javadoc)
	 * 
	 * @see com.thoughtworks.xstream.converters.Converter#unmarshal(com.thoughtworks.xstream.io.HierarchicalStreamReader,
	 *      com.thoughtworks.xstream.converters.UnmarshallingContext)
	 */
	@Override
	public Object unmarshal(HierarchicalStreamReader reader, UnmarshallingContext context) {
		SerializableId sId = new SerializableId();
		while (reader.hasMoreChildren()) {
			reader.moveDown();
			if ("id".equals(reader.getNodeName())) {
				Serializable id = (Serializable) _xstream.fromXML(reader.getValue());
				sId.setId(id);
			} else if ("cn".equals(reader.getNodeName())) {
				sId.setEntityName(reader.getValue());
			}
			reader.moveUp();
		}
		return sId;
	}
}
