/**
 * 
 */
package net.sf.gilead.annotations;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * Annotation for "ServerOnly" properties. Relevant value is not sent to the client side and replaced by null. Is is
 * replaced by the original value during the 'merge' phase to prevent any hack.
 * 
 * @author bruno.marchesson
 */
@Target({ METHOD, FIELD })
@Retention(RUNTIME)
public @interface ServerOnly {}
