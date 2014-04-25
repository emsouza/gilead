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

import java.io.IOException;
import java.io.Reader;

/**
 * A non-thread-safe fast line number reader that preserves the end-of-line character(s). 
 * Code is originally based on {@link java.io.BufferedReader}.
 * 
 * @author Joe D. Velopar
 */
public class FastLineNumberReader extends Reader {
    private static int DEFAULT_CHAR_BUFFER_SIZE = 8192;
    private static int DEFAULT_LINE_LENGTH = 80;
    /** The underlying reader. */
    private Reader reader;
    /** Character buffer. */
    private char charbuf[];
    /** Number of characters in the character buffer. */
    private int numCharInBuf;
    /** Index to the next character. */
    private int nextCharIdx;
    /**
     * True iff we need to read in more data into the character buffer 
     * to check if the next char is a LF;
     * False otherwise.
     */
    private boolean checkNextLF;
    /** Current line number. */
    private int lineNumber = -1;
    /** Last End of line. */
    private EolEnum eolEnum = EolEnum.NONE;
    
    /** 
     * End-of-line enums. 
     * 
     * @author Joe D. Velopar
     */
    private static enum EolEnum {
        CR,
        LF,
        CR_LF,
        NONE
        ;
        
        @Override
        public String toString() {
            switch(this) {
                case CR:
                    return "\r";
                case LF:
                    return "\n";
                case CR_LF:
                    return "\r\n";
                case NONE:
                    return "";
            }
            throw new IllegalStateException("EolEnum " + this + " is not yet supported.");
        }
    }
    /**
     * Create a buffering character-input stream that uses an input buffer of
     * the specified size.
     * 
     * @param in
     *            A Reader
     * @param sz
     *            Input-buffer size
     * 
     * @exception IllegalArgumentException
     *                If sz is <= 0
     */
    public FastLineNumberReader(Reader in, int sz) {
        super(in);
        if (sz <= 0)
            throw new IllegalArgumentException("Buffer size <= 0");
        this.reader = in;
        charbuf = new char[sz];
    }

    /**
     * Create a buffering character-input stream that uses a default-sized input
     * buffer.
     * 
     * @param in
     *            A Reader
     */
    public FastLineNumberReader(Reader in) {
        this(in, DEFAULT_CHAR_BUFFER_SIZE);
    }

    /** Reads and fills the internal character buffer. */
    private void fill() throws IOException {
        int n;
        do {
            n = reader.read(charbuf, 0, charbuf.length);
        } while (n == 0);
        numCharInBuf = n;
        nextCharIdx = 0;
    }

    @Override
    public int read(char[] cbuf, int off, int len) throws IOException {
        if ((off < 0) || (off > cbuf.length) || (len < 0)
                || ((off + len) > cbuf.length) || ((off + len) < 0)) {
            throw new IndexOutOfBoundsException();
        }
        if (len == 0)
            return 0;
        int n = read1(cbuf, off, len);
        if (n <= 0)
            return n;
        while ((n < len) && reader.ready()) {
            int n1 = read1(cbuf, off + n, len - n);
            if (n1 <= 0)
                break;
            n += n1;
        }
        return n;
    }

    private int read1(char[] cbuf, int off, int len) throws IOException {
        if (nextCharIdx >= numCharInBuf) {
            /*
             * If the requested length is at least as large as the buffer, do
             * not bother to copy the characters into the local buffer. In this
             * way buffered streams will cascade harmlessly.
             */
            if (len >= charbuf.length)
                return reader.read(cbuf, off, len);
            fill();
        }
        if (nextCharIdx >= numCharInBuf)
            return -1;
        int n = Math.min(len, numCharInBuf - nextCharIdx);
        System.arraycopy(charbuf, nextCharIdx, cbuf, off, n);
        nextCharIdx += n;
        return n;
    }

    @Override
    public void close() throws IOException {
        if (reader == null)
            return;
        reader.close();
        reader = null;
        charbuf = null;
    }

    /** Returns the end of line character(s) as string. */
    public String readEndOfLine() throws IOException {
        if (checkNextLF) {
            checkNextLF = false;
            // The last char in the buffer was a CR,  
            // so we need to read more to check the next char
            fill();

            if (nextCharIdx < numCharInBuf) {
                // Not EOF
                if (charbuf[0] == '\n') {
                    this.eolEnum = EolEnum.CR_LF;
                    nextCharIdx++;
                }
            }
        }
        String ret = eolEnum.toString();
        eolEnum = EolEnum.NONE;
        return ret;
    }
    
    /** Returns the current line number. */
    public int getLineNumber() {
        return lineNumber;
    }
    
    /** Returns the next line read. */
    public String readLine() throws IOException {
        if (checkNextLF)
            readEndOfLine();   // clear the eol character(s)
        StringBuilder sb = null;
        int startChar;

        for (;;)
        {
            if (nextCharIdx >= numCharInBuf)
                fill();
            if (nextCharIdx >= numCharInBuf) { /* EOF */
                if (sb != null && sb.length() > 0) {
                    lineNumber++;
                    return sb.toString();
                }
                return null;
            }
            boolean eol = false;
            char c = 0;
            int i;

            for (i = nextCharIdx; i < numCharInBuf; i++) {
                c = charbuf[i];
                if (c == '\n') {
                    this.eolEnum = EolEnum.LF;
                    eol = true;
                    break;
                }
                if (c == '\r') {
                    this.eolEnum = EolEnum.CR;
                    // Need to check further if it is a CR followed by a LF.
                    
                    if (i+1 < numCharInBuf) {
                        // The next char is in the buffer, so it's easy.
                        if (charbuf[i+1] == '\n') {
                            this.eolEnum = EolEnum.CR_LF;
                        }
                    }
                    else {
                        checkNextLF = true;
                    }
                    eol = true;
                    break;
                }
            }

            startChar = nextCharIdx;
            nextCharIdx = i;

            if (eol) {
                String str;
                
                if (sb == null) {
                    str = new String(charbuf, startChar, i - startChar);
                } else {
                    sb.append(charbuf, startChar, i - startChar);
                    str = sb.toString();
                }
                nextCharIdx++;
                
                if (eolEnum == EolEnum.CR_LF)
                    nextCharIdx++;
                lineNumber++;
                return str;
            }

            if (sb == null)
                sb = new StringBuilder(DEFAULT_LINE_LENGTH);
            sb.append(charbuf, startChar, i - startChar);
        }
    }
}
