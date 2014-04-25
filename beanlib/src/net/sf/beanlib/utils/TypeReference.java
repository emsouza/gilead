package net.sf.beanlib.utils;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

//http://gafter.blogspot.com/2006/12/super-type-tokens.html
/**
 * References a generic type.
 *
 * @author crazybob@google.com (Bob Lee)
 */
public abstract class TypeReference<T> {

    private final Type type;
    private volatile Constructor<T> constructor;

    protected TypeReference() {
        Type superclass = getClass().getGenericSuperclass();
        if (superclass instanceof Class) {
            throw new RuntimeException("Missing type parameter.");
        }
        this.type = ((ParameterizedType) superclass).getActualTypeArguments()[0];
    }

    /**
     * Instantiates a new instance of {@code T} using the default, no-arg
     * constructor.
     */
    public T newInstance()
            throws NoSuchMethodException, IllegalAccessException,
                   InvocationTargetException, InstantiationException {
        if (constructor == null) {
            @SuppressWarnings("unchecked")
            Class<T> rawType = type instanceof Class<?>
                ? (Class<T>) type
                : (Class<T>) ((ParameterizedType) type).getRawType();
            constructor = rawType.getConstructor();
        }
        return constructor.newInstance();
    }

    /**
     * Gets the referenced type.
     */
    public Type getType() {
        return this.type;
    }

    public static void main(String[] args) throws Exception {
        List<String> l1 = new TypeReference<ArrayList<String>>() {}.newInstance();
        System.out.println(l1);
        
        List<?> l2 = new TypeReference<ArrayList<?>>() {}.newInstance();
        System.out.println(l2);
    }
}