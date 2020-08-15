package net.sf.gilead.core.serialization;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import junit.framework.TestCase;

/**
 * Test serialization manager behavior
 *
 * @author bruno.marchesson
 */
public class SerializationManagerTest extends TestCase {

    private static final Logger LOGGER = LoggerFactory.getLogger(SerializationManagerTest.class);

    /**
     * Test XStream serialization
     */
    public void testXStreamProxySerialization() {
        IProxySerialization proxySerialization = new XStreamProxySerialization();
        testIntegerConversion(proxySerialization);
        testLongConversion(proxySerialization);
        testMapConversion(proxySerialization);
    }

    /**
     * Test JBoss serialization
     */
    public void testJBossProxySerialization() {
        IProxySerialization proxySerialization = new DefaultProxySerialization();
        testIntegerConversion(proxySerialization);
        testLongConversion(proxySerialization);
        testMapConversion(proxySerialization);
    }

    /**
     * Test GWT serialization
     */
    public void testGwtProxySerialization() {
        IProxySerialization proxySerialization = new GwtProxySerialization();
        testIntegerConversion(proxySerialization);
        testLongConversion(proxySerialization);
        testMapConversion(proxySerialization);
    }

    /**
     * Test Integer convertor
     */
    protected void testIntegerConversion(IProxySerialization proxySerialization) {
        // Integer conversion
        Integer value = new Integer(1);
        long start = System.nanoTime();
        Object serialized = proxySerialization.serialize(value);
        long serialization = System.nanoTime();

        assertNotNull(serialized);
        assertEquals(value, proxySerialization.unserialize(serialized));
        long end = System.nanoTime();

        LOGGER.debug("Integer serialization took [" + (serialization - start) + ", " + (end - serialization) + "] nanoseconds");

        // int conversion
        int intValue = 1;
        start = System.nanoTime();
        serialized = proxySerialization.serialize(intValue);
        serialization = System.nanoTime();

        assertNotNull(serialized);
        assertEquals(intValue, proxySerialization.unserialize(serialized));
        end = System.nanoTime();

        LOGGER.debug("int serialization took [" + (serialization - start) + ", " + (end - serialization) + "] nanoseconds");
    }

    /**
     * Test Long convertor
     */
    protected void testLongConversion(IProxySerialization proxySerialization) {
        // Long conversion
        Long value = new Long(1);
        long start = System.nanoTime();
        Object serialized = proxySerialization.serialize(value);
        long serialization = System.nanoTime();

        assertNotNull(serialized);
        assertEquals(value, proxySerialization.unserialize(serialized));
        long end = System.nanoTime();

        LOGGER.debug("Long serialization took [" + (serialization - start) + ", " + (end - serialization) + "] ms");

        // long conversion
        long longValue = 1;
        start = System.nanoTime();
        serialized = proxySerialization.serialize(longValue);
        serialization = System.nanoTime();

        assertNotNull(serialized);
        assertEquals(longValue, proxySerialization.unserialize(serialized));
        end = System.nanoTime();

        LOGGER.debug("long serialization took [" + (serialization - start) + ", " + (end - serialization) + "] ms");
    }

    /**
     * Test Map conversion
     */
    @SuppressWarnings("unchecked")
    protected void testMapConversion(IProxySerialization proxySerialization) {
        // Map creation
        HashMap<String, Serializable> map = new HashMap<>();
        map.put("id", 1);
        map.put("initialized", true);
        ArrayList<Integer> idList = new ArrayList<>();
        idList.add(2);
        idList.add(3);
        idList.add(4);
        map.put("idList", idList);

        // Test conversion
        long start = System.nanoTime();
        Object serialized = proxySerialization.serialize(map);
        long serialization = System.nanoTime();

        assertNotNull(serialized);
        HashMap<String, Serializable> unserialized = (HashMap<String, Serializable>) proxySerialization.unserialize(serialized);
        long end = System.nanoTime();

        LOGGER.debug("Map serialization took [" + (serialization - start) + ", " + (end - serialization) + "] ms");

        // Map checking
        assertNotNull(unserialized);
        assertEquals(map.size(), unserialized.size());
        assertNotNull(unserialized.get("idList"));
        assertEquals(3, ((ArrayList<Integer>) unserialized.get("idList")).size());
    }
}
