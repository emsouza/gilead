/*
 * Copyright 2005 The Apache Software Foundation.
 *
 * Licensed under the Apache License, Version 2.0 (the "License")
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package net.sf.beanlib.provider;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

/**
 * @author Joe D. Velopar
 */
public class Foo {
	private String string;
	private boolean boo;
	
	private String protectedSetString;
	
	public Foo() {
	}

	public Foo(String protectedSetString) {
		this.protectedSetString = protectedSetString;
	}
	public boolean isBoo() {
		return boo;
	}
	public void setBoo(boolean boo) {
		this.boo = boo;
	}
	public String getString() {
		return string;
	}
	public void setString(String string) {
		this.string = string;
	}
	
	@Override
    public boolean equals(Object that) {
		return EqualsBuilder.reflectionEquals(this, that);
	}
	public String getProtectedSetString() {
		return protectedSetString;
	}
	
	protected void setProtectedSetString(String protectedSetString) {
		this.protectedSetString = protectedSetString;
	}
	
	@Override
    public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}
}
