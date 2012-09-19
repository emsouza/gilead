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
 * Annotation for "ReadOnly" properties. Value coming back from client side is ignored during 'merge' phase
 * 
 * @author bruno.marchesson
 */
@Target({ METHOD, FIELD })
@Retention(RUNTIME)
public @interface ReadOnly {}
