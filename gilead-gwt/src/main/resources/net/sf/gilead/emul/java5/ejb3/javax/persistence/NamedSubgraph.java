/*******************************************************************************
 * Copyright (c) 2011 - 2013 Oracle Corporation. All rights reserved.
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
 *
 ******************************************************************************/

package javax.persistence;

import java.lang.annotation.Target;
import java.lang.annotation.Retention;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * A <code>NamedSubgraph</code> is a member element of a
 * <code>NamedEntityGraph</code>.  The <code>NamedSubgraph</code> is
 * only referenced from within a NamedEntityGraph and can not be
 * referenced independently.  It is referenced by its <code>name</code>
 * from a <code>NamedAttributeNode</code> element of the 
 * <code>NamedEntityGraph</code>.
 *
 * @see NamedEntityGraph
 * @see NamedAttributeNode
 *
 * @since Java Persistence 2.1
 */
@Target({})
@Retention(RUNTIME)
public @interface NamedSubgraph {

    /**
     * (Required) The name of the subgraph as referenced from a
     * NamedAttributeNode element.
     */
    String name();

    /**
     * (Optional) The type represented by this subgraph.  The element
     * must be specified when this subgraph is extending a definition
     * on behalf of a subclass.
     */
    Class type() default void.class;

    /** 
     * (Required) The list of the attributes of the class that must
     * be included.  If the named subgraph corresponds to a subclass
     * of the class referenced by the corresponding attribute node,
     * then only subclass-specific attributes are listed.
     */
    NamedAttributeNode[] attributeNodes();
}


