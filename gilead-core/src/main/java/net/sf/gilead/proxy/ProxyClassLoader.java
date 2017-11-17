package net.sf.gilead.proxy;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Enumeration;

import net.sf.gilead.proxy.xml.AdditionalCode;

/**
 * This wrapping class loader is used to generate proxy every time that a IProxy assignable class is loaded
 * 
 * @author bruno.marchesson
 */
public class ProxyClassLoader extends URLClassLoader {

    /**
     * The wrapped class loader
     */
    private ClassLoader _wrappedClassLoader;

    /**
     * Indicates if the wrapped classloader is an URL one or not
     */
    private boolean _isUrlClassLoader;

    /**
     * Constructor
     */
    public ProxyClassLoader(ClassLoader wrappedClassLoader) {
        super(new URL[] {});
        _wrappedClassLoader = wrappedClassLoader;
        _isUrlClassLoader = (wrappedClassLoader instanceof URLClassLoader);
    }

    /**
     * Find Resource simple override
     */
    @Override
    public URL findResource(String name) {
        if (_isUrlClassLoader) {
            return ((URLClassLoader) _wrappedClassLoader).findResource(name);
        } else {
            return super.findResource(name);
        }
    }

    /**
     * Find Resources simple override
     */
    @Override
    public Enumeration<URL> findResources(String name) throws IOException {
        if (_isUrlClassLoader) {
            return ((URLClassLoader) _wrappedClassLoader).findResources(name);
        } else {
            return super.findResources(name);
        }
    }

    /**
     * @param name
     * @return
     * @see java.lang.ClassLoader#getResource(java.lang.String)
     */
    @Override
    public URL getResource(String name) {
        return _wrappedClassLoader.getResource(name);
    }

    /**
     * @param name
     * @return
     * @see java.lang.ClassLoader#getResourceAsStream(java.lang.String)
     */
    @Override
    public InputStream getResourceAsStream(String name) {
        return _wrappedClassLoader.getResourceAsStream(name);
    }

    /**
     * @param name
     * @return
     * @throws IOException
     * @see java.lang.ClassLoader#getResources(java.lang.String)
     */
    @Override
    public Enumeration<URL> getResources(String name) throws IOException {
        return _wrappedClassLoader.getResources(name);
    }

    /**
     * Load class wrapping
     */
    @Override
    public Class<?> loadClass(String name) throws ClassNotFoundException {
        System.out.println(name);
        AdditionalCode additionalCode = AdditionalCodeManager.getInstance().getAdditionalCodeFor(name);
        if (additionalCode != null) {
            // Get source class name
            //
            String sourceClassName = AdditionalCodeManager.getInstance().getSourceClassName(name, additionalCode);
            Class<?> sourceClass = _wrappedClassLoader.loadClass(sourceClassName);

            // Generate proxy
            //
            return ProxyManager.getInstance().generateProxyClass(sourceClass, additionalCode);
        } else {
            // Load class
            //
            return _wrappedClassLoader.loadClass(name);
        }
    }
}
