/*
 * Copyright 2005 The Apache Software Foundation.
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
package net.sf.beanlib.utils;

import static org.junit.Assert.assertTrue;

import java.util.Arrays;

import junit.framework.JUnit4TestAdapter;

import org.junit.Test;

/**
 * JUnit Test for {@link StringUtils}.
 * 
 * @author Joe D. Velopar
 */
public class StringUtilsTest {
    /** Test ASCII characters from 0 to 127. */
    @Test public void testAscii() {
        char[] ca = new char[0x7f+1];
        
        for (int i=0x7f; i > -1; i--)
            ca[i] = (char)i;
        String s = new String(ca);
        byte[] ba1 = s.getBytes();
        byte[] ba2 = StringUtils.getBytesFromAsciiString(s, true);
        byte[] ba3 = StringUtils.getBytesFromAsciiString(s, false);
        byte[] ba4 = StringUtils.getBytesFromAsciiString(s);
        assertTrue(Arrays.equals(ba1, ba2));
        assertTrue(Arrays.equals(ba1, ba3));
        assertTrue(Arrays.equals(ba1, ba4));
    }
    
    /** Test non ASCII character with value beyond 1 byte. */
    @Test(expected=IllegalArgumentException.class)
    public void testNonAscii() {
        char[] ca = {0x01ff};
        String s = new String(ca);
        byte[] ba1 = s.getBytes();
        byte[] ba3 = StringUtils.getBytesFromAsciiString(s, false);
        assertTrue(Arrays.equals(ba1, ba3));
    }

    /** Test non ASCII character with value beyond 1 byte with the least memory option. */
    @Test(expected=IllegalArgumentException.class)
    public void testNonAscii_leastMemory() {
        char[] ca = {0x01ff};
        String s = new String(ca);
        byte[] ba1 = s.getBytes();
        byte[] ba2 = StringUtils.getBytesFromAsciiString(s, true);
        assertTrue(Arrays.equals(ba1, ba2));
    }

    /** Test non ASCII character with negative value beyond 1 byte. */
    @Test(expected=IllegalArgumentException.class)
    public void testNonAsciiNegative() {
        char[] ca = {0x1000};
        String s = new String(ca);
        byte[] ba1 = s.getBytes();
        byte[] ba2 = StringUtils.getBytesFromAsciiString(s);
        assertTrue(Arrays.equals(ba1, ba2));
    }
    
    public static junit.framework.Test suite() {
        return new JUnit4TestAdapter(StringUtilsTest.class);
    }

}
