package net.sf.gilead.core.cache;

import java.lang.ref.SoftReference;

/**
 * Hacking ThreadLocal leak https://github.com/evanj/expire-threadlocal
 *
 * @author eduardo.souza
 */
public class SoftLocalCache<T> {

    private final ThreadLocal<SoftReference<T>> cache = new ThreadLocal<>();

    public void set(T obj) {
        SoftReference<T> ref = cache.get();
        if (ref != null) {
            ref.clear();
            cache.remove();
        }
        cache.set(new SoftReference<>(obj));
    }

    public T get() {
        SoftReference<T> ref = cache.get();
        if (ref != null) {
            return ref.get();
        }
        return null;
    }

    public void remove() {
        SoftReference<T> ref = cache.get();
        if (ref != null) {
            ref.clear();
        }
        cache.remove();
    }
}
