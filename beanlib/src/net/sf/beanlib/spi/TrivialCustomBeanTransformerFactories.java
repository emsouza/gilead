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

import net.sf.beanlib.PropertyInfo;

/**
 * @author Hanson Char
 */
public class TrivialCustomBeanTransformerFactories {
    /** Returns the factory of a custom transformer that is always not applicable. */
    public static CustomBeanTransformerSpi.Factory getNoopCustomTransformerFactory() {
        return NoopCustomTransformer.FACTORY;
    }
    
    /** Returns a custom transformer that is always not applicable. */
    public static CustomBeanTransformerSpi getNoopCustomTransformer() {
        return NoopCustomTransformer.SINGLETON;
    }
    
    /** 
     * Returns the factory of a custom transformer that always performs identical transformation 
     * (ie returns the same input instance). 
     */ 
    public static CustomBeanTransformerSpi.Factory getIdentityCustomTransformerFactory() {
        return IdentityCustomTransformer.FACTORY;
    }
    
    /** 
     * Returns a custom transformer that always performs identical transformation 
     * (ie returns the same input instance). 
     */ 
    public static CustomBeanTransformerSpi getIdentityCustomTransformer() {
        return IdentityCustomTransformer.SINGLETON;
    }
    
    /** Returns the factory a custom transformer that always transforms to null. */
    public static CustomBeanTransformerSpi.Factory getNullCustomTransformerFactory() {
        return NullCustomTransformer.FACTORY;
    }
    
    /** Returns a custom transformer that always transforms to null. */
    public static CustomBeanTransformerSpi getNullCustomTransformer() {
        return NullCustomTransformer.SINGLETON;
    }

    /** Used to delay loading class until necessary. */
    private static class NoopCustomTransformer {
        /** Factory of a transformer that performs no custom transformation. */
        static final CustomBeanTransformerSpi.Factory FACTORY = new CustomBeanTransformerSpi.Factory() {
            public CustomBeanTransformerSpi newCustomBeanTransformer(BeanTransformerSpi contextBeanTransformer) {
                return SINGLETON;
            }
        };
        /** Performs no custom transformation. */
        static final CustomBeanTransformerSpi SINGLETON = new CustomBeanTransformerSpi() {
            public boolean isTransformable(Object from, Class<?> toClass, PropertyInfo propertyInfo) {
                return false;
            }

            public <T> T transform(Object in, Class<T> toClass, PropertyInfo propertyInfo) {
                return null;
            }
        };
    }
    
    /** Used to delay loading class until necessary. */
    private static class IdentityCustomTransformer {
        /** Factory of a transformer that always transform to the same input instance. */
        static final CustomBeanTransformerSpi.Factory FACTORY = new CustomBeanTransformerSpi.Factory() {
            public CustomBeanTransformerSpi newCustomBeanTransformer(BeanTransformerSpi contextBeanTransformer) {
                return SINGLETON;
            }
        };
        /** Always transform to the same input instance. */
        static final CustomBeanTransformerSpi SINGLETON = new CustomBeanTransformerSpi() {
            public boolean isTransformable(Object from, Class<?> toClass, PropertyInfo propertyInfo) {
                return true;
            }

            @SuppressWarnings("unchecked")
            public <T> T transform(Object in, Class<T> toClass, PropertyInfo propertyInfo) {
                return (T)in;
            }
        };
    }
    
    /** Used to delay loading class until necessary. */
    private static class NullCustomTransformer {
        /** Factory of a transformer that always transform to null. */
        static final CustomBeanTransformerSpi.Factory FACTORY = new CustomBeanTransformerSpi.Factory() {
            public CustomBeanTransformerSpi newCustomBeanTransformer(BeanTransformerSpi contextBeanTransformer) {
                return SINGLETON;
            }
        };
        /** Always transform to null. */
        static final CustomBeanTransformerSpi SINGLETON = new CustomBeanTransformerSpi() {
            public boolean isTransformable(Object from, Class<?> toClass, PropertyInfo propertyInfo) {
                return true;
            }

            public <T> T transform(Object in, Class<T> toClass, PropertyInfo propertyInfo) {
                return null;
            }
        };
    }
}
