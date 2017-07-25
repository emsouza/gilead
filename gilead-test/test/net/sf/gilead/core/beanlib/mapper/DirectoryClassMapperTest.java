/**
 *
 */
package net.sf.gilead.core.beanlib.mapper;

import junit.framework.TestCase;

/**
 * Test case for the Directory Class Mapper class
 *
 * @author bruno.marchesson
 */
public class DirectoryClassMapperTest extends TestCase {

    /**
     * Test method for {@link net.sf.gilead.core.beanlib.mapper.DirectoryClassMapper#getTargetClass(java.lang.Class)}.
     */
    public final void testNominalCase() {
        // Test mapper
        //
        DirectoryClassMapper classMapper = new DirectoryClassMapper();
        classMapper.setRootDomainPackage("net.sf.gilead.core.beanlib.mapper.domain1");
        classMapper.setRootClonePackage("net.sf.gilead.core.beanlib.mapper.dto1");
        classMapper.setCloneSuffix("DTO");

        Class<?> sourceClass = net.sf.gilead.core.beanlib.mapper.domain1.DomainClass1.class;
        Class<?> targetClass = net.sf.gilead.core.beanlib.mapper.dto1.DomainClass1DTO.class;

        // Test Domain -> DTO mapping
        //
        assertEquals(targetClass, classMapper.getTargetClass(sourceClass));
        assertNull(classMapper.getTargetClass(targetClass));

        // Test DTO -> Domain class
        //
        assertEquals(sourceClass, classMapper.getSourceClass(targetClass));
        assertNull(classMapper.getSourceClass(sourceClass));
    }

    /**
     * Test nested DTO and Domain package issue
     */
    public final void testNestedPackages() {
        // Test mapper
        //
        DirectoryClassMapper classMapper = new DirectoryClassMapper();
        classMapper.setRootDomainPackage("net.sf.gilead.core.beanlib.mapper.domain1");
        classMapper.setRootClonePackage("net.sf.gilead.core.beanlib.mapper.domain1.dto");
        classMapper.setCloneSuffix("DTO");

        Class<?> sourceClass = net.sf.gilead.core.beanlib.mapper.domain1.DomainClass1.class;
        Class<?> targetClass = net.sf.gilead.core.beanlib.mapper.domain1.dto.DomainClass1DTO.class;

        // Test Domain -> DTO mapping
        //
        assertEquals(targetClass, classMapper.getTargetClass(sourceClass));
        assertNull(classMapper.getTargetClass(targetClass));

        // Test DTO -> Domain class
        //
        assertEquals(sourceClass, classMapper.getSourceClass(targetClass));
        assertNull(classMapper.getSourceClass(sourceClass));
    }
}