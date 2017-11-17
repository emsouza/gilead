package net.sf.gilead.annotations;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * Annotation for limited access properties. Accessibility of these properties is defined by the defined AccessManager,
 * and can be ReadOnly, ServerOnly or null (write access).
 *
 * @author bruno.marchesson
 */
@Target({ METHOD, FIELD })
@Retention(RUNTIME)
public @interface LimitedAccess {}
