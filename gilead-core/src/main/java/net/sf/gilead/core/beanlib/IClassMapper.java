package net.sf.gilead.core.beanlib;

/**
 * Interface of the class mapper service
 * 
 * @author bruno.marchesson
 */
public interface IClassMapper {

    /**
     * @return the mapped class for the argument class
     */
    Class<?> getTargetClass(Class<?> sourceClass);

    /**
     * @return the mapped class for the argument class
     */
    Class<?> getSourceClass(Class<?> targetClass);
}