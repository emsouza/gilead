/*
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.annotations;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.PACKAGE;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Define the fetching strategy profile.
 *
 * @author Hardy Ferentschik
 */
@Target({ TYPE, PACKAGE })
@Retention(RUNTIME)
public @interface FetchProfile {
	/**
	 * The name of the fetch profile.
	 */
	String name();

	/**
	 * The association fetch overrides.
	 */
	FetchOverride[] fetchOverrides();

	/**
	 * Descriptor for a particular association override.
	 */
	@Target({ TYPE, PACKAGE })
	@Retention(RUNTIME)
	@interface FetchOverride {
		/**
		 * The entity containing the association whose fetch is being overridden.
		 */
		Class<?> entity();

		/**
		 * The association whose fetch is being overridden.
		 */
		String association();

		/**
		 * The fetch mode to apply to the association.
		 */
		FetchMode mode();
	}
}
