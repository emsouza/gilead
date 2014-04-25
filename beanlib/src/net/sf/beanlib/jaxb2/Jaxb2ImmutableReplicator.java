/*
 * Copyright 2007 The Apache Software Foundation.
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
package net.sf.beanlib.jaxb2;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import net.jcip.annotations.ThreadSafe;
import net.sf.beanlib.BeanlibException;
import net.sf.beanlib.provider.replicator.ImmutableReplicator;
import net.sf.beanlib.spi.BeanTransformerSpi;
import net.sf.beanlib.spi.replicator.ImmutableReplicatorSpi;

/**
 * Implementation of {@link net.sf.beanlib.spi.replicator.ImmutableReplicatorSpi}
 * with support of Jaxb2 generated enums.
 * 
 * @author Hanson Char
 */
public class Jaxb2ImmutableReplicator extends ImmutableReplicator
{
    public static final Factory factory = new Factory();
    
    /**
     * Factory for {@link Jaxb2ImmutableReplicator}.
     * 
     * @author Hanson Char
     */
    @ThreadSafe
    public static class Factory implements ImmutableReplicatorSpi.Factory {
        private Factory() {}
        
        public Jaxb2ImmutableReplicator newImmutableReplicatable(BeanTransformerSpi beanTransformer) {
            return new Jaxb2ImmutableReplicator();
        }
    }

    public static Jaxb2ImmutableReplicator newImmutableReplicatable(BeanTransformerSpi beanTransformer) {
        return factory.newImmutableReplicatable(beanTransformer);
    }
        
    protected Jaxb2ImmutableReplicator() {}
    
    @Override
    public <V, T> T replicateImmutable(V immutableFrom, Class<T> toClass) 
    {
        T result = super.replicateImmutable(immutableFrom, toClass);

        if (result != null)
            return result;
        if (immutableFrom instanceof Enum)
        {
            return toValue(immutableFrom, toClass);
        }
        return null;
    }

    private <V, T> T toValue(V immutableFrom, Class<T> toClass) 
    {
        try {
            Method valueMethod = immutableFrom.getClass().getMethod("value");
            Object value = valueMethod.invoke(immutableFrom);
            Method fromValueMethod = toClass.getMethod("fromValue", String.class);
            return toClass.cast(fromValueMethod.invoke(null, value));
        } catch (SecurityException e) {
            throw new BeanlibException(e);
        } catch (NoSuchMethodException e) {
            throw new BeanlibException(e);
        } catch (IllegalAccessException e) {
            throw new BeanlibException(e);
        } catch (InvocationTargetException e) {
            throw new BeanlibException(e.getTargetException());
        }
    }
}
