/**
 * 
 */
package net.sf.gilead.pojo.gwt;

import java.io.Serializable;

/**
 * Interface for valid GWT serializable parameters. It is needed because GWT does not support a list of Object instance,
 * even if each element in the list is serializable
 * 
 * @author bruno.marchesson
 */
public interface IGwtSerializableParameter extends Serializable {
	/**
	 * @return the underlying value.
	 */
	public Object getUnderlyingValue();
}
