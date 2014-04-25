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
package net.sf.beanlib.hibernate3;

import org.hibernate.SessionFactory;
import org.springframework.orm.hibernate3.HibernateTemplate;

/**
 * Hibernate 3 DAO Support Interface.
 * <p> 
 * This interface is necessary as proxy via CGLIB won't work for 
 * methods which are final.
 * 
 * @author Joe D. Velopar
 */
public interface Hibernate3DaoSupportable {
	public SessionFactory getSessionFactory();
	public HibernateTemplate getHibernateTemplate();

}
