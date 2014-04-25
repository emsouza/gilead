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

import java.util.Map;
import java.util.TreeMap;

/**
 * @author Joe D. Velopar
 */
public class FooWithMap {
	private Map<Object,Object> map;
	
	private FooWithMap() {
		
	}

	public FooWithMap(Map<Object,Object> map) {
		if (map != null)
			this.map = map;
			
	}
	public Map getMap() {
		return map;
	}

	public void setMap(Map<Object,Object> map) {
		this.map = map;
	}

	public Object addToMap(Object key, Object value) {
        if (map == null)
            map = new TreeMap<Object,Object>();
		return this.map.put(key, value);
	}
}
