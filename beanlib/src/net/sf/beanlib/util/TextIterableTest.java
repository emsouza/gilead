package net.sf.beanlib.util;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.Iterator;
import java.util.NoSuchElementException;

import junit.framework.JUnit4TestAdapter;

import org.junit.Test;

/**
 * @author Hanson Char
 */
public class TextIterableTest 
{
    @Test
    public void testIteration() {
        TextIterable ti = new TextIterable("testing_text_iterable.txt");

        int count = 0;
        
        for (String line : ti) {
            assertTrue(ti.numberOfopenedIterators() == 1);
            count++;
            System.out.println(line);
        }
        assertTrue(count == 10);
        assertTrue(ti.numberOfopenedIterators() == 0);
    }
    
    @Test
    public void testMultipleIterators() {
        TextIterable ti = new TextIterable("testing_text_iterable.txt");

        int count = 0;
        
        ti.iterator();  // 1st
        ti.iterator();  // 2nd
        
        for (String line : ti) {
            assertTrue(ti.numberOfopenedIterators() == 3);
            count++;
            System.out.println(line);
        }
        assertTrue(count == 10);
        assertTrue(ti.numberOfopenedIterators() == 2);
        ti.close();
        assertTrue(ti.numberOfopenedIterators() == 0);
    }
    
    @Test
    public void testCloseInMiddle() {
        TextIterable ti = new TextIterable("testing_text_iterable.txt");
        int count = 0;
        
        for (String line : ti) {
            count++;
            assertTrue(ti.numberOfopenedIterators() == 1);
            System.out.println(line);

            if (count == 5)
                break;
        }
        assertTrue(count == 5);
        assertTrue(ti.numberOfopenedIterators() == 1);
        ti.close();
        assertTrue(ti.numberOfopenedIterators() == 0);
        ti.close();
    }
   
    @Test(expected=NoSuchElementException.class)
    public void testNoSuchElement() {
        TextIterable ti = new TextIterable("testing_text_iterable.txt");
        Iterator itr=ti.iterator();
        
        while (itr.hasNext())
            itr.next();
        itr.next();
    }
    
    @Test
    public void testReturnNull() {
        TextIterable ti = new TextIterable("testing_text_iterable.txt")
                          .withReturnNullUponEof(true);
        Iterator itr=ti.iterator();
        
        while (itr.hasNext())
            itr.next();
        assertNull(itr.next());
        assertNull(itr.next());
        assertFalse(itr.hasNext());
        assertFalse(itr.hasNext());
        assertNull(itr.next());
    }
    
    @Test
    public void testReturnNull2() {
        TextIterable ti = new TextIterable("testing_text_iterable.txt")
                          .withReturnNullUponEof(true);
        Iterator itr=ti.iterator();
        
        while (itr.hasNext())
            itr.next();
        assertFalse(itr.hasNext());
        assertNull(itr.next());
    }
    
    public static junit.framework.Test suite() {
        return new JUnit4TestAdapter(TextIterableTest.class);
    }
}
