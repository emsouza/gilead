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
package net.sf.beanlib;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

/**
 * Information about a JavaBean property.
 * 
 * @author Joe D. Velopar
 */
public class PropertyInfo 
{
    private final String propertyName;
    private final Object fromBean;
    private final Object toBean;
    
    public PropertyInfo(String propertyName, Object fromBean, Object toBean) {
        this.propertyName = propertyName;
        this.fromBean = fromBean;
        this.toBean = toBean;
    }

    /** Returns the property name. */
    public String getPropertyName() {
        return propertyName;
    }

    /** Returns the Java Bean from which the property is read. */ 
    public Object getFromBean() {
        return fromBean;
    }

    /** Returns the Java Bean to which the property is to be written. */ 
    public Object getToBean() {
        return toBean;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.MULTI_LINE_STYLE);
    }
}
