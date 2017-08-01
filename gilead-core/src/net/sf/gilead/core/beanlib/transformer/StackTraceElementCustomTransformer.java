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

package net.sf.gilead.core.beanlib.transformer;

import java.util.Map;

import net.sf.beanlib.PropertyInfo;
import net.sf.beanlib.spi.BeanTransformerSpi;
import net.sf.beanlib.spi.CustomBeanTransformerSpi;

/**
 * StackTraceElement transformer. Used to clone an exception that contains a persistent POJO
 *
 * @author BMARCHESSON
 */
public class StackTraceElementCustomTransformer implements CustomBeanTransformerSpi {

    private BeanTransformerSpi beanTransformer;

    /**
     * Constructor
     *
     * @param beanTransformer
     */
    public StackTraceElementCustomTransformer(final BeanTransformerSpi beanTransformer) {
        this.beanTransformer = beanTransformer;
    }

    /**
     * Filter method
     */
    @Override
    public boolean isTransformable(Object from, Class<?> toClass, PropertyInfo info) {
        return (toClass == StackTraceElement.class);
    }

    /**
     * Transformation method
     */
    @Override
    @SuppressWarnings("unchecked")
    public <T> T transform(Object in, Class<T> toClass, PropertyInfo info) {
        Map<Object, Object> cloneMap = beanTransformer.getClonedMap();
        Object clone = cloneMap.get(in);

        if (clone != null) {
            return (T) clone;
        }

        StackTraceElement stackTraceElement = (StackTraceElement) in;
        clone = new StackTraceElement(stackTraceElement.getClassName(), stackTraceElement.getMethodName(), stackTraceElement.getFileName(),
                stackTraceElement.getLineNumber());

        cloneMap.put(in, clone);

        return (T) clone;
    }
}
