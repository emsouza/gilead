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

import org.apache.log4j.Logger;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Transaction;

/**
 * Hibernate 3 Unit Of Work.
 * 
 * A Hibernate Unit-Of-Work means a unit of work that executes within a single
 * atomic Hibernate transaction that either commits or roll backs upon returning
 * from the execution.
 * 
 * @author Joe D. Velopar
 */
public abstract class Hibernate3UnitOfWork {
	private static final Logger logger = Logger.getLogger(Hibernate3UnitOfWork.class);

	protected final Session session;
	
	public Hibernate3UnitOfWork(Session sess) {
		this.session = sess;
	}
	/** 
	 * Executes a unit of work within a Hibernate transaction.
	 */
	public final void execute() throws HibernateException {
		Transaction tx = session.beginTransaction();

		logger.info("execute() - beginTransaction");
		try {
			doit();
			tx.commit();
			logger.info("execute() - tx.commit");
			tx = null;
		} finally {
			if (tx != null) {
				tx.rollback();
				logger.info("execute() - tx.rollback");
			}
		}
	}
	/** 
	 * Method that a subclass overrides to provide the
	 * actual work to be done within a Hibernate transaction.
	 */
	protected abstract void doit() throws HibernateException;
}
