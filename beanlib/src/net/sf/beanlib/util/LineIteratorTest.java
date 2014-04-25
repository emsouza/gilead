package net.sf.beanlib.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.io.ByteArrayInputStream;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.Iterator;
import java.util.NoSuchElementException;

import junit.framework.JUnit4TestAdapter;

import org.junit.Test;

/**
 * @author Hanson Char
 */
public class LineIteratorTest
{
    private static final String EMPTY = "";
    private static final String ONE_LINER = "One line";
    private static final String TWO_LINER = ONE_LINER + "\n" + "Two line";
    
    private static final TextIterable PLACEHOLDER = new TextIterable((URL)null);
    
    @Test
    public void testClose() 
    {
        {
            ByteArrayInputStream is = new ByteArrayInputStream(TWO_LINER.getBytes());
            LineIterator itr = new LineIterator(PLACEHOLDER, is, true, (Charset)null);
            assertEquals(ONE_LINER, itr.next());
            assertTrue(itr.getLineNumber() == 1);
            itr.closeInPrivate();
            assertNull(itr.next());
            assertFalse(itr.hasNext());
        }
        {
            ByteArrayInputStream is = new ByteArrayInputStream(TWO_LINER.getBytes());
            LineIterator itr = new LineIterator(PLACEHOLDER, is, true, (Charset)null);
            assertEquals(ONE_LINER, itr.next());
            itr.closeInPrivate();
            assertFalse(itr.hasNext());
            assertNull(itr.next());
        }
    }
    
    @Test(expected=NoSuchElementException.class)
    public void testNoSuchElement() {
        {
            ByteArrayInputStream is = new ByteArrayInputStream(EMPTY.getBytes());
            LineIterator itr = new LineIterator(PLACEHOLDER, is, false, (Charset)null);
            assertTrue(itr.getLineNumber() == 0);
            assertFalse(itr.hasNext());
            assertFalse(itr.hasNext());
            itr.next();
        }
    }
    
    @Test(expected=NoSuchElementException.class)
    public void testNoSuchElement2() {
        {
            ByteArrayInputStream is = new ByteArrayInputStream(EMPTY.getBytes());
            LineIterator itr = new LineIterator(PLACEHOLDER, is, false, (Charset)null);
            itr.next();
        }
    }
    
    @Test(expected=NoSuchElementException.class)
    public void testNoSuchElement3() {
        {
            ByteArrayInputStream is = new ByteArrayInputStream(EMPTY.getBytes());
            LineIterator itr = new LineIterator(PLACEHOLDER, is, false, (Charset)null);
            assertFalse(itr.hasNext());
            itr.next();
        }
    }
    
    @Test
    public void testEmpty() {
        {
            ByteArrayInputStream is = new ByteArrayInputStream(EMPTY.getBytes());
            LineIterator itr = new LineIterator(PLACEHOLDER, is, true, (Charset)null);
            assertTrue(itr.getLineNumber() == 0);
            assertFalse(itr.hasNext());
            assertNull(itr.next());
            assertTrue(itr.getLineNumber() == -1);
            assertFalse(itr.hasNext());
            assertNull(itr.next());
            assertTrue(itr.getLineNumber() == -1);
            itr.close();
        }
        {
            ByteArrayInputStream is = new ByteArrayInputStream(EMPTY.getBytes());
            LineIterator itr = new LineIterator(PLACEHOLDER, is, true, (Charset)null);
            assertTrue(itr.getLineNumber() == 0);
            assertFalse(itr.hasNext());
            assertFalse(itr.hasNext());
            assertNull(itr.next());
            assertTrue(itr.getLineNumber() == -1);
            assertNull(itr.next());
            assertTrue(itr.getLineNumber() == -1);
        }
    }
    
    @Test
    public void testOneLiner() {
        {
            ByteArrayInputStream is = new ByteArrayInputStream(ONE_LINER.getBytes());
            LineIterator itr = new LineIterator(PLACEHOLDER, is, true, (Charset)null);
            assertTrue(itr.getLineNumber() == 0);
            assertTrue(itr.hasNext());
            assertTrue(itr.getLineNumber() == 1);
            assertEquals(ONE_LINER, itr.next());
            assertTrue(itr.getLineNumber() == 1);
            assertFalse(itr.hasNext());
            assertTrue(itr.getLineNumber() == -1);
            assertNull(itr.next());
            assertTrue(itr.getLineNumber() == -1);
        }
        {
            ByteArrayInputStream is = new ByteArrayInputStream(ONE_LINER.getBytes());
            LineIterator itr = new LineIterator(PLACEHOLDER, is, true, (Charset)null);
            assertTrue(itr.getLineNumber() == 0);
            assertTrue(itr.hasNext());
            assertTrue(itr.getLineNumber() == 1);
            assertTrue(itr.hasNext());
            assertTrue(itr.getLineNumber() == 1);
            assertEquals(ONE_LINER, itr.next());
            assertTrue(itr.getLineNumber() == 1);
            assertNull(itr.next());
            assertTrue(itr.getLineNumber() == -1);
        }
        {
            ByteArrayInputStream is = new ByteArrayInputStream(ONE_LINER.getBytes());
            Iterator itr = new LineIterator(PLACEHOLDER, is, true, (Charset)null);
            assertEquals(ONE_LINER, itr.next());
            assertFalse(itr.hasNext());
        }
    }

    @Test
    public void testTwoLiner() {
        {
            ByteArrayInputStream is = new ByteArrayInputStream(TWO_LINER.getBytes());
            Iterator itr = new LineIterator(PLACEHOLDER, is, true, (Charset)null);
            assertTrue(itr.hasNext());
            assertEquals(ONE_LINER, itr.next());
            assertTrue(itr.hasNext());
            assertEquals("Two line", itr.next());
            assertFalse(itr.hasNext());
            assertNull(itr.next());
        }
        {
            ByteArrayInputStream is = new ByteArrayInputStream(TWO_LINER.getBytes());
            LineIterator itr = new LineIterator(PLACEHOLDER, is, true, (Charset)null);
            assertTrue(itr.getLineNumber() == 0);
            assertTrue(itr.hasNext());
            assertTrue(itr.getLineNumber() == 1);
            assertTrue(itr.hasNext());
            assertTrue(itr.getLineNumber() == 1);
            assertEquals(ONE_LINER, itr.next());
            assertTrue(itr.getLineNumber() == 1);
            assertTrue(itr.hasNext());
            assertTrue(itr.getLineNumber() == 2);
            assertTrue(itr.hasNext());
            assertTrue(itr.getLineNumber() == 2);
            assertEquals("Two line", itr.next());
            assertTrue(itr.getLineNumber() == 2);
            assertFalse(itr.hasNext());
            assertTrue(itr.getLineNumber() == -1);
            assertFalse(itr.hasNext());
            assertTrue(itr.getLineNumber() == -1);
            assertNull(itr.next());
            assertTrue(itr.getLineNumber() == -1);
            assertNull(itr.next());
            assertTrue(itr.getLineNumber() == -1);
        }
        {
            ByteArrayInputStream is = new ByteArrayInputStream(TWO_LINER.getBytes());
            Iterator itr = new LineIterator(PLACEHOLDER, is, true, (Charset)null);
            assertEquals(ONE_LINER, itr.next());
            assertEquals("Two line", itr.next());
            assertNull(itr.next());
            assertFalse(itr.hasNext());
        }
    }
    
    public static junit.framework.Test suite() {
        return new JUnit4TestAdapter(LineIteratorTest.class);
    }
}
