/**
 * 
 */
package net.sf.gilead.pojo.gwt;

/**
 * GWT serializabe version of the SerializableId class.
 * 
 * @author bruno.marchesson
 */
public class GwtSerializableId implements IGwtSerializableParameter {
	// ----
	// Attributes
	// ----
	/**
	 * Serialization ID
	 */
	private static final long serialVersionUID = 6651960007634836456L;

	/**
	 * The underlying id
	 */
	protected IGwtSerializableParameter id;

	/**
	 * Hash code or value for non persistent and transient values
	 */
	protected String stringValue;

	/**
	 * The associated entity name
	 */
	protected String entityName;

	// ----
	// Properties
	// ----
	/**
	 * @return the id
	 */
	public IGwtSerializableParameter getId() {
		return id;
	}

	/**
	 * @param id the id to set
	 */
	public void setId(IGwtSerializableParameter id) {
		this.id = id;
	}

	/**
	 * @return the string value
	 */
	public String getStringValue() {
		return stringValue;
	}

	/**
	 * @param value the value to set
	 */
	public void setStringValue(String value) {
		this.stringValue = value;
	}

	/**
	 * @return the entityName
	 */
	public String getEntityName() {
		return entityName;
	}

	/**
	 * @param entityName the entityName to set
	 */
	public void setEntityName(String entityName) {
		this.entityName = entityName;
	}

	// -------------------------------------------------------------------------
	//
	// IRequestParameter fake implementation
	//
	// -------------------------------------------------------------------------

	@Override
	public Object getUnderlyingValue() {
		return null;
	}

}
