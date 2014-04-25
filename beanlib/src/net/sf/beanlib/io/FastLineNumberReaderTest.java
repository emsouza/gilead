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
package net.sf.beanlib.io;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringReader;
import java.io.StringWriter;

import junit.framework.JUnit4TestAdapter;
import net.sf.beanlib.utils.StringUtils;

import org.apache.log4j.Logger;
import org.junit.Test;

/**
 * @author Joe D. Velopar
 */
public class FastLineNumberReaderTest 
{
    private static final Logger log = Logger.getLogger(FastLineNumberReaderTest.class);
    private static final String LINE_1 = "Line one.";
    private static final String LINE_2 = "Line two.";

    /** Tests various eol combinations. */
    @Test public void testEol() throws IOException 
    {
        for (int bufSize=LINE_1.length() + LINE_2.length() + 3; bufSize > 0; bufSize--) {
            // cause single line break
            String[] eol1 = {"\n", "\r", "\r\n"};
            // cause double line breaks
            String[] eol2 = {"\n\r", "\r\r", "\n\n"};
            checkTwoLines(eol1, 1, bufSize, false);
            checkTwoLines(eol1, 1, bufSize, true);
            checkTwoLines(eol2, 2, bufSize, false);
            checkTwoLines(eol2, 2, bufSize, true);
        }
    }
    
    /**
     * @param eols various eol characters to try
     * @param maxLineIdx the expected last line number
     * @param bufSize the buffer size used to for the FastLineReader  
     * @param endWithLineBreak true if the last line is to be ended with a line break; false otherwise.
     * @throws IOException should never be thrown
     */
    private void checkTwoLines(String[] eols, final int maxLineIdx, 
            final int bufSize, final boolean endWithLineBreak) 
        throws IOException 
    {
        for (String eol : eols) {
            String before = LINE_1 + eol
                          + LINE_2 
                          + (endWithLineBreak ? System.getProperty("line.separator") : "")
                          ;
            log.debug(toHexString(StringUtils.getBytesFromAsciiString(before)));
            FastLineNumberReader r = getFastLineNumberReader(before, bufSize);
            StringWriter sw = new StringWriter();
            String line;
            
            while ((line = r.readLine()) != null) {
                log.debug("Line# " + r.getLineNumber() + ": " + line);
                sw.write(line);
                sw.write(r.readEndOfLine());
            }
            String after = sw.toString();
            assertEquals(before, after);
            assertEquals(maxLineIdx, r.getLineNumber());
        }
    }
    
    /** Test the cases when the eol characters are not explicitely read. */
    @Test public void testEolNotRead() throws IOException {
        String before = LINE_1 + System.getProperty("line.separator")
                      + LINE_2 + System.getProperty("line.separator")
                      ;
        log.debug(toHexString(StringUtils.getBytesFromAsciiString(before)));
        FastLineNumberReader r = getFastLineNumberReader(before, 1);
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        String line;
        
        while ((line = r.readLine()) != null) {
            log.debug("Line# " + r.getLineNumber() + ": " + line);
            pw.println(line);
        }
        String after = sw.toString();
        assertEquals(before, after);
        assertEquals(1, r.getLineNumber());
        
    }

    private FastLineNumberReader getFastLineNumberReader(String msg, final int bufSize) {
        return new FastLineNumberReader(new StringReader(msg), bufSize);
    }
    
    private String toHexString(byte[] bytes) {
        StringBuilder sb = new StringBuilder(bytes.length);
        
        for (byte b : bytes)
            sb.append("0x").append(Integer.toHexString(b)).append(" ");
        return sb.toString();
    }

    public static junit.framework.Test suite() {
        return new JUnit4TestAdapter(FastLineNumberReaderTest.class);
    }
}
