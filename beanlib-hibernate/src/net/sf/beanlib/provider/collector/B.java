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

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

/**
 * @author Joe D. Velopar
 */
public class B {
    private A a;
    private Date date;
    private Set<B> bSet;
    private String name;
    
    public B() {}
    
    public B(String name) { this.name = name; }
    
    @Override
    public String toString() { return name; }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Set<B> getBSet() {
        return bSet;
    }

    public void setBSet(Set<B> bSet) {
        this.bSet = bSet;
    }
    
    public boolean addB(B a) {
        if (bSet == null)
            bSet = new HashSet<B>();
        return bSet.add(a);
    }
    
    @Override
    public int hashCode() {
        return name == null ? 0 : name.hashCode();
    }
    
    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof B))
            return false;
        B that = (B)obj;

        if (this.name == null)
            return that.name == null;
        return this.name.equals(that.name);
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public A getA() {
        return a;
    }

    public void setA(A a) {
        this.a = a;
    }
}
