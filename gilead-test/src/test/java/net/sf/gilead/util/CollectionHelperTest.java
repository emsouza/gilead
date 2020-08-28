package net.sf.gilead.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import junit.framework.TestCase;

/**
 * Test case for Collection helper class
 * 
 * @author bruno.marchesson
 */
public class CollectionHelperTest extends TestCase {

    /**
     * Test method for {@link net.sf.gilead.util.CollectionHelper#isUnmodifiableCollection(java.util.Collection)}.
     */
    @SuppressWarnings("unchecked")
    public final void testIsUnmodifiableCollection() {
        List<Object> testList = new ArrayList<>();
        assertFalse(CollectionHelper.isUnmodifiableCollection(testList));

        List<Object> unmodifiable = Collections.unmodifiableList(testList);
        assertTrue(CollectionHelper.isUnmodifiableCollection(unmodifiable));

        List<Object> retrieved = (List<Object>) CollectionHelper.getUnmodifiableCollection(unmodifiable);
        assertEquals(testList, retrieved);
    }
}
