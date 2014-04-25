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
package net.sf.beanlib.hibernate3;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Joe D. Velopar
 */
public class FooWithList {
	private List<Object> list;
	FooWithList fooWithList;
	
	protected FooWithList() {
	}

	public List getList() {
		return list;
	}

	public void setList(List<Object> list) {
		this.list = list;
	}
	
	public boolean addToList(Object obj) {
        if (list == null)
            list = new ArrayList<Object>();
		return this.list.add(obj);
	}

	public FooWithList getFooWithList() {
		return fooWithList;
	}

	public void setFooWithList(FooWithList fooWithList) {
		this.fooWithList = fooWithList;
	}

}
