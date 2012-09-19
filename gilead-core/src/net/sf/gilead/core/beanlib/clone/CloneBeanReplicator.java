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

package net.sf.gilead.core.beanlib.clone;

import net.sf.beanlib.hibernate.HibernateBeanReplicator;
import net.sf.beanlib.hibernate3.Hibernate3BeanTransformer;
import net.sf.beanlib.hibernate3.Hibernate3BlobReplicator;
import net.sf.beanlib.hibernate3.Hibernate3MapReplicator;
import net.sf.beanlib.spi.BeanTransformerSpi;
import net.sf.beanlib.spi.CustomBeanTransformerSpi;
import net.sf.gilead.core.IPersistenceUtil;
import net.sf.gilead.core.beanlib.IClassMapper;
import net.sf.gilead.core.beanlib.finder.FastPrivateReaderMethodFinder;
import net.sf.gilead.core.beanlib.finder.FastPrivateSetterMethodCollector;
import net.sf.gilead.core.beanlib.transformer.CustomTransformersFactory;
import net.sf.gilead.core.store.IProxyStore;

/**
 * Hibernate Bean Replicator override to inject the class mapper used for clone to a different class.
 * 
 * @author bruno.marchesson
 */
public class CloneBeanReplicator extends HibernateBeanReplicator {

	public CloneBeanReplicator(IClassMapper classMapper, IPersistenceUtil persistenceUtil, IProxyStore proxyStore) {
		super(newBeanTransformer(classMapper, persistenceUtil, proxyStore));
	}

	private static Hibernate3BeanTransformer newBeanTransformer(IClassMapper classMapper, IPersistenceUtil persistenceUtil, IProxyStore proxyStore) {
		Hibernate3BeanTransformer transformer = new Hibernate3BeanTransformer();

		// Custom collection replicator
		transformer.initCollectionReplicatableFactory(CloneCollectionReplicator.factory);

		// Set associated PersistenceUtil
		((CloneCollectionReplicator) transformer.getCollectionReplicatable()).setPersistenceUtil(persistenceUtil);

		transformer.initMapReplicatableFactory(Hibernate3MapReplicator.getFactory());
		transformer.initBlobReplicatableFactory(Hibernate3BlobReplicator.getFactory());

		// Custom bean replicatable
		transformer.initBeanReplicatableFactory(CloneClassBeanReplicator.factory);

		// Set the associated class mapper
		((CloneClassBeanReplicator) transformer.getBeanReplicatable()).setClassMapper(classMapper);
		((CloneClassBeanReplicator) transformer.getBeanReplicatable()).setPersistenceUtil(persistenceUtil);

		// Custom transformers (timestamp handling)
		transformer.initCustomTransformerFactory(new CustomBeanTransformerSpi.Factory() {
			@Override
			public CustomBeanTransformerSpi newCustomBeanTransformer(final BeanTransformerSpi beanTransformer) {
				return CustomTransformersFactory.getInstance().createUnionCustomBeanTransformerForClone(beanTransformer);
			}
		});

		// Lazy properties handling
		transformer.initDetailedPropertyFilter(new ClonePropertyFilter(persistenceUtil, proxyStore));

		// Protected and private setter collection
		transformer.initSetterMethodCollector(new FastPrivateSetterMethodCollector());
		transformer.initReaderMethodFinder(new FastPrivateReaderMethodFinder());

		return transformer;
	}
}
