/*******************************************************************************
 * Copyright (c) 2008 - 2013 Oracle Corporation. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
 * which accompanies this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     Linda DeMichiel - Java Persistence 2.1
 *     Linda DeMichiel - Java Persistence 2.0
 *
 ******************************************************************************/ 
package javax.persistence.metamodel;

/**
 * Instances of the type <code>Type</code> represent persistent object 
 * or attribute types.
 *
 * @param <X>  The type of the represented object or attribute
 *
 * @since Java Persistence 2.0
 */
public interface Type<X> {

       public static enum PersistenceType {

	   /** Entity */
           ENTITY, 

	   /** Embeddable class */
	   EMBEDDABLE, 

	   /** Mapped superclass */
	   MAPPED_SUPERCLASS, 

	   /** Basic type */
	   BASIC
       }

    /**
     *  Return the persistence type.
     *  @return persistence type
     */	
    PersistenceType getPersistenceType();

    /**
     *  Return the represented Java type.
     *  @return Java type
     */
    Class<X> getJavaType();
}
