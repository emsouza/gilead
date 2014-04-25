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
package net.sf.beanlib.utils.xml;

import net.sf.beanlib.utils.ClassUtils;

import com.thoughtworks.xstream.XStream;

/**
 * Convenient XStream Factory that uses ID reference and simple class alias registration.
 * 
 * @author Joe D. Velopar
 */
public class XStreamFactory
{
    private static final XStreamFactory inst = new XStreamFactory();
    private volatile XStream xstream;
    
    public static XStreamFactory getInstance() {
        return inst;
    }

    private XStreamFactory() {
    }

    public XStream getXStream() {
        return xstream == null ? xstream = new XStream() : xstream;
    }
    
//    public void registerAliases(Set<Class> classes) {
//        XStream xstream = new XStream();
//        xstream.setMode(XStream.ID_REFERENCES);
//        
//        for (Class c : classes)
//            xstream.alias(ClassUtils.inst.unqualify(c), c);
//        this.xstream = xstream;
//    }
//    
    public void registerAliases(Class<?>[] classes) {
        XStream xstream = new XStream();
        xstream.setMode(XStream.ID_REFERENCES);
        
        for (Class<?> c : classes)
            xstream.alias(ClassUtils.unqualify(c), c);
        this.xstream = xstream;
    }
}
