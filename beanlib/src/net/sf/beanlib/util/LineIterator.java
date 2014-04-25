package net.sf.beanlib.util;

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.util.Iterator;
import java.util.NoSuchElementException;

import net.jcip.annotations.NotThreadSafe;

/**
 * @author Hanson Char
 */
@NotThreadSafe
class LineIterator implements Iterator<String>, Closeable {
    private boolean hasNextExecuted;
    private String line;
    private LineNumberReader lnr;
    private final TextIterable textIterable;
    private final boolean returnNullUponEof;

    LineIterator(TextIterable textIterable, InputStream is, boolean returnNullUponEof, Charset charset) {
        this.textIterable = textIterable;
        this.returnNullUponEof = returnNullUponEof;
        InputStreamReader isr = null;

        try {
            isr = charset == null 
                ? new InputStreamReader(is) 
                : new InputStreamReader(is, charset)
                ;
            lnr = new LineNumberReader(isr);
        } catch (Exception ex) {
            try {
                if (lnr != null)
                    lnr.close();
                else if (isr != null)
                    isr.close();
                else if (is != null)
                    is.close();
            } catch (Throwable ignore) {
            }
        }
    }
    
    LineIterator(TextIterable textIterable, InputStream is, boolean returnNullUponEof, CharsetDecoder decoder) {
        this.textIterable = textIterable;
        this.returnNullUponEof = returnNullUponEof;
        InputStreamReader isr = null;

        try {
            isr = new InputStreamReader(is, decoder);
            lnr = new LineNumberReader(isr);
        } catch (Exception ex) {
            try {
                if (lnr != null)
                    lnr.close();
                else if (isr != null)
                    isr.close();
                else if (is != null)
                    is.close();
            } catch (Throwable ignore) {
            }
        }
    }

    public boolean hasNext() {
        if (hasNextExecuted)
            return line != null;
        try {
            hasNextExecuted = true;

            if (lnr != null) {
                line = lnr.readLine();

                if (line == null)
                    close();
            }
            return line != null;
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }

    public String next() {
        if (hasNextExecuted) {
            hasNextExecuted = false;
            return line == null 
                 ? eof() 
                 : line
                 ;
        }
        return hasNext() 
             ? next() 
             : eof()
             ;
    }
    
    private String eof() {
        if (returnNullUponEof)
            return null;
        throw new NoSuchElementException();
    }

    public void close() {
        if (lnr != null) {
            textIterable.removeLineIterator(this);
            closeInPrivate();
        }
    }
    
    public int getLineNumber() {
        return lnr == null  
             ? -1 
             : lnr.getLineNumber()
             ;
    }
    
    void closeInPrivate() {
        if (lnr != null) {
            try {
                lnr.close();
            } catch (IOException ignore) {
            }
            line = null;
            lnr = null;
        }
    }

    public void remove() {
        throw new UnsupportedOperationException("remove not supported");
    }

    @Override
    public void finalize() {
        try {
            super.finalize();
        } catch (Throwable ex) {
        }
        close();
    }
}