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
 * A <code>NamedAttributeNode</code> is a member element of a
 * <code>NamedEntityGraph</code>.
 *
 * @see NamedEntityGraph
 * @see NamedSubgraph
 *
 * @since Java Persistence 2.1
 */
@Target({})
@Retention(RUNTIME)
public @interface NamedAttributeNode {

    /**
     * (Required) The name of the attribute that must be included in the graph.
     */
    String value();

    /**
     * (Optional) If the attribute references a managed type that has
     * its own AttributeNodes, this element is used to refer to that
     * NamedSubgraph definition.  
     * If the target type has inheritance, multiple subgraphs can
     * be specified.  These additional subgraphs are intended to add
     * subclass-specific attributes.  Superclass subgraph entries will
     * be merged into subclass subgraphs.  
     *
     * <p> The value of this element is the name of the subgraph as
     * specified by the <code>name</code> element of the corresponding
     * <code>NamedSubgraph</code> element.  If multiple subgraphs are
     * specified due to inheritance, they are referenced by this name.
     */
    String subgraph() default "";

   /**
    * (Optional) If the attribute references a Map type, this element
    * can be used to specify a subgraph for the Key in the case of an
    * Entity key type.  A keySubgraph can not be specified without the
    * Map attribute also being specified.  If the target type has
    * inheritance, multiple subgraphs can be specified.  These
    * additional subgraphs are intended to add subclass-specific
    * attributes.  Superclass subgraph entries will be merged into
    * subclass subgraphs.  
    * 
    * <p> The value of this element is the name of the key subgraph as
    * specified by the <code>name</code> element of the corresponding
    * <code>NamedSubgraph</code> element.  If multiple key subgraphs
    * are specified due to inheritance, they are referenced by this
    * name.
    */
    String keySubgraph() default "";
}


