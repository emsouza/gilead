/*
 * Copyright 2008 The Apache Software Foundation.
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
package net.sf.beanlib.spi;

import java.util.ArrayList;
import java.util.List;

import net.sf.beanlib.PropertyInfo;

/**
 * Supports a chain of custom transformation.
 * 
 * @author Hanson Char
 */
public class ChainedCustomBeanTransformer implements CustomBeanTransformerSpi 
{
    private final List<CustomBeanTransformerSpi> customTransformers = new ArrayList<CustomBeanTransformerSpi>();
    
    /**
     * Factory to create a {@link ChainedCustomBeanTransformer}.
     * 
     * @author Hanson Char
     *
     */
    public static class Factory implements CustomBeanTransformerSpi.Factory {
        private final List<CustomBeanTransformerSpi.Factory> customTransformerFactories = new ArrayList<CustomBeanTransformerSpi.Factory>();
        
        public Factory() {}
        
        public Factory(CustomBeanTransformerSpi.Factory ... customBeanTransformerFactories) {
            if (customBeanTransformerFactories != null) {
                for (CustomBeanTransformerSpi.Factory f: customBeanTransformerFactories)
                    this.customTransformerFactories.add(f);
            }
        }
        
        public Factory appendCustomerBeanTransformerFactory(CustomBeanTransformerSpi.Factory customBeanTransformerFactory) {
            this.customTransformerFactories.add(customBeanTransformerFactory);
            return  this;
        }
        
        public CustomBeanTransformerSpi newCustomBeanTransformer(BeanTransformerSpi contextBeanTransformer) {
            ChainedCustomBeanTransformer chained = new ChainedCustomBeanTransformer();
            
            for (CustomBeanTransformerSpi.Factory f: customTransformerFactories)
                chained.appendCustomerBeanTransformer(f.newCustomBeanTransformer(contextBeanTransformer));
            return chained;
        }
    }
    
    private ChainedCustomBeanTransformer() {}
    
    private ChainedCustomBeanTransformer appendCustomerBeanTransformer(CustomBeanTransformerSpi c) {
        this.customTransformers.add(c);
        return this;
    }

    public boolean isTransformable(Object from, Class<?> toClass, PropertyInfo propertyInfo) {
        for (CustomBeanTransformerSpi cbt : customTransformers) {
            if (cbt.isTransformable(from, toClass, propertyInfo))
                return true;
        }
        return false;
    }

    public <T> T transform(Object in, Class<T> toClass, PropertyInfo propertyInfo) {
        boolean isTransformed = false;
        T t = null;

        for (CustomBeanTransformerSpi cbt : customTransformers) {
            if (cbt.isTransformable(in, toClass, propertyInfo)) {
                t = cbt.transform(isTransformed ? t : in, toClass, propertyInfo);
                isTransformed = true;
            }
        }
        return t;
    }
}