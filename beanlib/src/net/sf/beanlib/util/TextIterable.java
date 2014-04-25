package net.sf.beanlib.util;

import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import net.jcip.annotations.ThreadSafe;

/**
 * @author Hanson Char
 */
@ThreadSafe
public class TextIterable implements Iterable<String>, Closeable {
    private final URL url;
    private final List<LineIterator> openedIterators = new ArrayList<LineIterator>();
    private volatile boolean returnNullUponEof;
    private String charsetname;
    private Charset charset;
    private CharsetDecoder charsetDecoder;

    public TextIterable(File file) throws MalformedURLException {
        this(file.toURI().toURL());
    }

    public TextIterable(URL url) {
        this.url = url;
    }

    public TextIterable(String resourcePath) {
        this(
            Thread.currentThread()
                  .getContextClassLoader()
                  .getResource(resourcePath));
    }

    public LineIterator iterator() {
        LineIterator ret;
        final String charsetname;
        final Charset charset;
        final CharsetDecoder charsetDecoder;
        
        synchronized(this) {
            charsetname = this.charsetname;
            charset = this.charset;
            charsetDecoder = this.charsetDecoder;
        }
        try {
            if (charsetDecoder != null)
                ret = new LineIterator(this, url.openStream(), returnNullUponEof, charsetDecoder);
            else if (charset != null)
                ret = new LineIterator(this, url.openStream(), returnNullUponEof, charset);
            else if (charsetname != null)
                ret = new LineIterator(this, url.openStream(), returnNullUponEof, Charset.forName(charsetname));
            else
                ret = new LineIterator(this, url.openStream(), returnNullUponEof, (Charset)null);
            synchronized (openedIterators) {
                openedIterators.add(ret);
            }
            return ret;
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }
    
    public void close() {
        final LineIterator[] lineIterators;

        synchronized (openedIterators) {
            lineIterators = openedIterators.toArray(
                                        new LineIterator[
                                             openedIterators.size()]);
            for (Iterator<LineIterator> itr=openedIterators.iterator(); itr.hasNext();) {
                itr.next();
                itr.remove();
            }
        }
        for (LineIterator li : lineIterators)
            li.closeInPrivate();
    }

    public int numberOfopenedIterators() {
        return openedIterators.size();
    }

    void removeLineIterator(LineIterator li) {
        synchronized (openedIterators) {
            openedIterators.remove(li);
        }
    }

    public boolean isReturnNullUponEof() {
        return returnNullUponEof;
    }

    public void setReturnNullUponEof(boolean returnNullUponEof) {
        this.returnNullUponEof = returnNullUponEof;
    }

    public TextIterable withReturnNullUponEof(boolean returnNullUponEof) {
        setReturnNullUponEof(returnNullUponEof);
        return this;
    }

    public synchronized Charset getCharset() {
        return charset;
    }

    public synchronized void setCharset(Charset charset) {
        this.charset = charset;
        this.charsetname = null;
        this.charsetDecoder = null;
    }
    
    public TextIterable withCharset(Charset charset) {
        setCharset(charset);
        return this;
    }

    public synchronized CharsetDecoder getCharsetDecoder() {
        return charsetDecoder;
    }

    public synchronized void setCharsetDecoder(CharsetDecoder charsetDecoder) {
        this.charsetDecoder = charsetDecoder;
        this.charsetname = null;
        this.charset = null;
    }
    
    public TextIterable withCharsetDecoder(CharsetDecoder charsetDecoder) {
        setCharsetDecoder(charsetDecoder);
        return this;
    }

    public synchronized String getCharsetname() {
        return charsetname;
    }

    public synchronized void setCharsetname(String charsetname) {
        this.charsetname = charsetname;
        this.charset = null;
        this.charsetDecoder = null;
    }
    
    public TextIterable withCharsetname(String charsetname) {
        setCharsetname(charsetname);
        return this;
    }
}
