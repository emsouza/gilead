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

/**
 * Bean Populator SPI.
 * This SPI provides various options to control the propagation behavior of JavaBean properties.
 * 
 * @see BeanPopulatorBaseSpi
 * 
 * @author Joe D. Velopar
 */
public interface BeanPopulatorSpi extends BeanPopulatorBaseSpi 
{
    /**
     * Bean Populator Factory SPI, which is used to create
     * a bean populator which can then be used to determine 
     * whether a specific JavaBean property should be propagated 
     * from a source bean to a target bean.
     * 
     * @author Joe D. Velopar
     */
    public static interface Factory {
        /** Returns a bean populator, given the from bean and to bean. */ 
        public BeanPopulatorSpi newBeanPopulator(Object from, Object to);
    }

    /**
     * Used to configure a transformer to transform a property value read from a source JavaBean
     * into a value to be to set the corresponding property of a target JavaBean.
     * 
     * @param transformer is used to transform every property value read from a source JavaBean
     * into a value to be to set the corresponding property of a target JavaBean.
     * 
     * @return the current object (ie this) for method chaining purposes.
     */
    public BeanPopulatorSpi initTransformer(Transformable transformer);

    /**
     * Returns the transformer used by this bean populator.
     */
    public Transformable getTransformer();
    
    /** 
     * Propagates properties from the source JavaBean to the target JavaBean. 
     */
    public <T> T populate(); 
    
    // Overrides for co-variant return type.
    public BeanPopulatorSpi initPropertyFilter(PropertyFilter propertyFilter);
    // Overrides for co-variant return type.
    public BeanPopulatorSpi initDetailedPropertyFilter(DetailedPropertyFilter detailedPropertyFilter);
    // Overrides for co-variant return type.
    public BeanPopulatorSpi initBeanSourceHandler(BeanSourceHandler beanSourceHandler);
    // Overrides for co-variant return type.
    public BeanPopulatorSpi initReaderMethodFinder(BeanMethodFinder readerMethodFinder);
    // Overrides for co-variant return type.
    public BeanPopulatorSpi initSetterMethodCollector(BeanMethodCollector setterMethodCollector);
    // Overrides for co-variant return type.
    public BeanPopulatorSpi initDebug(boolean debug);
    // Overrides for co-variant return type.
    public BeanPopulatorSpi initBeanPopulationExceptionHandler(BeanPopulationExceptionHandler beanPopulationExceptionHandler);
    // Overrides for co-variant return type.
    public BeanPopulatorSpi initBeanPopulatorBaseConfig(BeanPopulatorBaseConfig baseConfig);
}
