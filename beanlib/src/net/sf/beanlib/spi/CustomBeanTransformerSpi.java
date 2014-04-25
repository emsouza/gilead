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
package net.sf.beanlib.spi;

import net.sf.beanlib.PropertyInfo;

/**
 * Custom Bean Transformer SPI.
 * <p>
 * Used to provide custom transformation that takes precedence over the default transformation.
 * 
 * <h2>Quick Start</h2>
 * For example, by default when BeanReplicator is replicating the content of a JavaBean, 
 * it would try to instantiate the target class via the no-arg constructor.  
 * If the no-arg constructor is not defined (such as in the Point class below), 
 * it would cause a NoSuchMethodException to be thrown:
 * <pre><blockquote>public class Point {
 *     private final int x, y;
 *     
 *     // missing no-arg constructor 
 *     public Point(int x, int y) {
 *         this.x = x;
 *         this.y = y;
 *     }
 *     public int getX() { return x; }
 *     public int getY() { return y; }
 * }
 * 
 * public class Bean {
 *     private Point point;
 *     public Point getPoint() { return point; }
 *     public void setPoint(Point point) { this.point = point; }
 * }
 * 
 * ...
 * // Initialize a bean
 * Bean from = new Bean();
 * from.setPoint(new Point(1,2));
 * 
 * // Tries to replicate the bean using the default implementation
 * BeanReplicator replicator = new BeanReplicator();
 * // Will cause NoSuchMethodException, as Point does not have a no-arg constructor
 * Bean to = replicator.replicateBean(from);
 * </blockquote></pre>One way to get around this problem 
 * is to define a custom transformer and the respective factory:
 * <pre><blockquote>public class MyBeanTransformer implements CustomBeanTransformerSpi {
 *     public  boolean isTransformable(Object from, Class toClass, PropertyInfo propertyInfo) {
 *         return toClass == Point.class;
 *     }
 *     
 *     public <T> T transform(Object in, Class<T> toClass, PropertyInfo propertyInfo) {
 *         Point from = (Point)in;
 *         // Note the framework takes care of the issue of object identity,
 *         // so we don't need to here.
 *         return (T)new Point(from.getX(), from.getY());
 *     }
 * }
 * 
 * public class MyBeanTransformerFactory implements CustomBeanTransformerSpi.Factory {
 *     public CustomBeanTransformerSpi newCustomBeanTransformer(BeanTransformerSpi beanTransformer) {
 *         return new MyBeanTransformer();
 *     }
 * }
 * 
 * ...
 * // Initialize a bean
 * Bean from = new Bean();
 * from.setPoint(new Point(1,2));
 * 
 * // Partially overrides the default transformer behavior
 * BeanTransformerSpi transformer = new BeanTransformer(new MyBeanTransformerFactory());
 * BeanReplicator replicator = new BeanReplicator(transformer);
 * 
 * // Replicates the bean
 * Bean to = replicator.replicateBean(from); // now works!
 * </blockquote></pre>
 * <h2>Customizing HibernateBeanReplicator</h2>
 * Customizing the behavior of HibernateBeanReplicator is basically identical to that of BeanReplicator as decribed above.
 * For example, assuming the same MyBeanTransformerFactory defined above is used,
 * <pre><blockquote>// Partially overrides the default Hibernate bean transformer's behavior
 * HibernateBeanReplicator replicator = new Hibernate3BeanReplicator();
 * replicator.initCustomTransformerFactory(new MyBeanTransformerFactory());
 * replicator.copy(...);
 * </blockquote></pre>
 * 
 * @see BeanTransformerSpi
 * 
 * @author Joe D. Velopar
 */
public interface CustomBeanTransformerSpi extends Transformable {
    /**
     * Custom Bean Transformer Factory SPI.
     *  
     * @author Joe D. Velopar
     */
    public static interface Factory {
        /**
         * Returns a custom transformer.
         * 
         * @param contextBeanTransformer the context bean transformer currently used to provide the 
         * default transformation behavior.
         */
        public CustomBeanTransformerSpi newCustomBeanTransformer(BeanTransformerSpi contextBeanTransformer);
    }
    
    /**
     * Returns true if the given object is to be transformed by this transformer;
     * false otherwise.
     * 
     * @param from source object
     * @param propertyInfo If null, it means the in object is a root level object.
     * Otherwise, propertyInfo contains information about the input object 
     * as a java bean property value to be transformed.  
     * 
     * @param toClass target class
     */
    public boolean isTransformable(Object from, Class<?> toClass, PropertyInfo propertyInfo);
}
