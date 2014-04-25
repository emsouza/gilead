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
 */package net.sf.beanlib.hibernate3;

import java.sql.Blob;

import net.jcip.annotations.ThreadSafe;
import net.sf.beanlib.spi.BeanTransformerSpi;
import net.sf.beanlib.spi.replicator.BlobReplicatorSpi;
import net.sf.beanlib.utils.BlobUtils;

import org.hibernate.Hibernate;

/**
 * Hibernate 3 specific Blob Replicator.
 * 
 * @author Joe D. Velopar
 */
public class Hibernate3BlobReplicator implements BlobReplicatorSpi {
    private static final Factory factory = new Factory();
    
    public static Factory getFactory() {
        return factory;
    }
    /**
     * Factory for {@link Hibernate3BlobReplicator}
     * 
     * @author Joe D. Velopar
     */
    @ThreadSafe
    public static class Factory implements BlobReplicatorSpi.Factory {
        private Factory() {}
        
        public Hibernate3BlobReplicator newBlobReplicatable(BeanTransformerSpi beanTransformer) {
            return new Hibernate3BlobReplicator();
        }
    }
    
    public static Hibernate3BlobReplicator newBlobReplicatable(BeanTransformerSpi beanTransformer) {
        return factory.newBlobReplicatable(beanTransformer);
    }
    
    private final BlobUtils blobUtils = new BlobUtils();
    
    private Hibernate3BlobReplicator() {}
    
    public <T> T replicateBlob(Blob fromBlob, Class<T> toClass) {
        byte[] byteArray = blobUtils.toByteArray(fromBlob);
        @SuppressWarnings("unchecked") T ret = (T)Hibernate.createBlob(byteArray);
        return ret;
    }
}
