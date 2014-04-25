/*
 * Copyright 2007 The Apache Software Foundation.
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
package net.sf.beanlib.provider.collector;

import java.util.HashSet;
import java.util.Set;

/**
 * @author Joe D. Velopar
 */
public class A {
    private Set<A> aset;
    private String name;
    
    public A() {}
    
    public A(String name) { this.name = name; }
    
    @Override
    public String toString() { return name; }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Set<A> getAset() {
        return aset;
    }

    public void setAset(Set<A> aset) {
        this.aset = aset;
    }
    
    public boolean addA(A a) {
        if (aset == null)
            aset = new HashSet<A>();
        return aset.add(a);
    }
    
    @Override
    public int hashCode() {
        return name == null ? 0 : name.hashCode();
    }
    
    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof A))
            return false;
        A that = (A)obj;

        if (this.name == null)
            return that.name == null;
        return this.name.equals(that.name);
    }
}
