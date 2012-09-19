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

package net.sf.gilead.core.beanlib.transformer;

import java.sql.Timestamp;
import java.util.Date;
import java.util.Map;

import net.sf.beanlib.PropertyInfo;
import net.sf.beanlib.spi.BeanTransformerSpi;
import net.sf.beanlib.spi.CustomBeanTransformerSpi;

/**
 * Timestamp transformer. Needed to keep nanoseconds part (by default, beanLib will convert it to simple Date if the
 * setter only declares an argument of type java.util.Date)
 * 
 * @author BMARCHESSON
 */
public class TimestampCustomTransformer implements CustomBeanTransformerSpi {
	// ----
	// Attributes
	// ----
	private BeanTransformerSpi _beanTransformer;

	/**
	 * Constructor
	 * 
	 * @param beanTransformer
	 */
	public TimestampCustomTransformer(final BeanTransformerSpi beanTransformer) {
		_beanTransformer = beanTransformer;
	}

	/**
	 * Filter method
	 */

	@Override
	public boolean isTransformable(Object from, Class<?> toClass, PropertyInfo info) {
		return ((from instanceof Timestamp) && (toClass == Date.class));
	}

	/**
	 * Transformation method
	 */
	@Override
	@SuppressWarnings("unchecked")
	public <T> T transform(Object in, Class<T> toClass, PropertyInfo info) {
		Map<Object, Object> cloneMap = _beanTransformer.getClonedMap();
		Object clone = cloneMap.get(in);

		if (clone != null) {
			return (T) clone;
		}

		Timestamp date = (Timestamp) in;
		clone = new Timestamp(date.getTime());

		((Timestamp) clone).setNanos(date.getNanos());
		cloneMap.put(in, clone);

		return (T) clone;
	}
}
