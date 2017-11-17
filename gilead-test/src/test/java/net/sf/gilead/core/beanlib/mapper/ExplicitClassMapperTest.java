package net.sf.gilead.core.beanlib.mapper;

import junit.framework.TestCase;

/**
 * Test case for Explicit class mapper
 * 
 * @author Olaf Kock, Florian Siebert
 */
public class ExplicitClassMapperTest extends TestCase {

    /**
     * The mapper to test
     */
    private ExplicitClassMapper mapper;

    /**
     * Test initialization
     */
    @Override
    protected void setUp() throws Exception {
        super.setUp();
        mapper = new ExplicitClassMapper();
        mapper.addAssociation(Integer.class, Double.class);
        mapper.addAssociation(Boolean.class, String.class);
    }

    /**
     * Test Source -> Clone mapping
     */
    public void testMapSourceToClone() throws Exception {
        assertEquals(Double.class, mapper.getTargetClass(Integer.class));
        assertEquals(String.class, mapper.getTargetClass(Boolean.class));
    }

    /**
     * Error test for mapping
     * 
     * @throws Exception
     */
    public void testNonexistentAssociations() throws Exception {
        assertNull(mapper.getTargetClass(String.class));
        assertNull(mapper.getSourceClass(Boolean.class));
    }

    /**
     * Test Clone -> Source mapping
     */
    public void testMapCloneToSource() throws Exception {
        assertEquals(Integer.class, mapper.getSourceClass(Double.class));
        assertEquals(Boolean.class, mapper.getSourceClass(String.class));
    }
}
