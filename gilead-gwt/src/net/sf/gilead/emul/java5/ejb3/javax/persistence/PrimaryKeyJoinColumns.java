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
package javax.persistence;

import java.lang.annotation.Target;
import java.lang.annotation.Retention;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;
import static javax.persistence.ConstraintMode.PROVIDER_DEFAULT;

/**
 * Groups {@link PrimaryKeyJoinColumn} annotations.
 * It is used to map composite foreign keys.
 *
 * <pre>
 *    Example: ValuedCustomer subclass
 *
 *    &#064;Entity
 *    &#064;Table(name="VCUST")
 *    &#064;DiscriminatorValue("VCUST")
 *    &#064;PrimaryKeyJoinColumns({
 *        &#064;PrimaryKeyJoinColumn(name="CUST_ID", 
 *            referencedColumnName="ID"),
 *        &#064;PrimaryKeyJoinColumn(name="CUST_TYPE",
 *            referencedColumnName="TYPE")
 *    })
 *    public class ValuedCustomer extends Customer { ... }
 * </pre>
 *
 * @see ForeignKey
 *
 * @since Java Persistence 1.0
 */
@Target({TYPE, METHOD, FIELD})
@Retention(RUNTIME)

public @interface PrimaryKeyJoinColumns {

    /** One or more <code>PrimaryKeyJoinColumn</code> annotations. */
    PrimaryKeyJoinColumn[] value();

    /**
     *  (Optional) Used to specify or control the generation of a
     *  foreign key constraint when table generation is in effect. 
     *  If both this element and the <code>foreignKey</code> element 
     *  of any of the <code>PrimaryKeyJoinColumn</code> elements are specified, 
     *  the behavior is undefined.  If no foreign key annotation element
     *  is specified in either location, the persistence provider's
     *  default foreign key strategy will apply.
     *
     *  @since Java Persistence 2.1
     */
    ForeignKey foreignKey() default @ForeignKey(PROVIDER_DEFAULT);

}
