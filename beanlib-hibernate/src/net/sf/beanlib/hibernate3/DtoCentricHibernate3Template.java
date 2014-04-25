/*
 * Copyright 2004-2006 The Apache Software Foundation.
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

import java.io.Serializable;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.LockMode;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.engine.SessionImplementor;
import org.springframework.dao.DataAccessException;
import org.springframework.orm.hibernate3.HibernateCallback;
import org.springframework.orm.hibernate3.HibernateTemplate;

/**
 * Base class for Hibernate 3 DTO Centric {@link org.springframework.orm.hibernate3.HibernateTemplate}.
 * 
 * @author Joe D. Velopar
 */
public class DtoCentricHibernate3Template extends HibernateTemplate 
{
    private String applicationPackagePrefix;
    private Class<?> applicationSampleClass;

    public DtoCentricHibernate3Template() {
	}

	public DtoCentricHibernate3Template(SessionFactory sessionFactory) {
		super(sessionFactory);
	}

	public DtoCentricHibernate3Template(SessionFactory sessionFactory, boolean allowCreate) {
		super(sessionFactory, allowCreate);
	}
    
    // TODO: verify this method must be invoked 
    public DtoCentricHibernate3Template init(String applicationPackagePrefix, Class<?> applicationSampleClass) {
        this.applicationPackagePrefix = applicationPackagePrefix;
        this.applicationSampleClass = applicationSampleClass;
        return this;
    }
    
    private Hibernate3DtoCopier getHibernateDtoCopier() {
        if (applicationPackagePrefix == null)
            throw new NullPointerException(
                    "Application package prefix must be initialized via the init method after DtoCentricHibernate3Template is constructed.");
        return new Hibernate3DtoCopier(applicationPackagePrefix, applicationSampleClass);
    }
    
    //-------------------------------------------------------------------------
    // Convenience methods for loading individual objects
    //-------------------------------------------------------------------------

    @Override
    public Object get(@SuppressWarnings("unchecked") final Class entityClass, 
                        final Serializable id, final LockMode lockMode)
            throws DataAccessException 
    {
        return execute(new HibernateCallback() {
            public Object doInHibernate(Session session) throws HibernateException {
                if (lockMode != null) {
                    return getHibernateDtoCopier().hibernate2dtoFully( session.get(entityClass, id, lockMode) );
                }
                return getHibernateDtoCopier().hibernate2dtoFully( session.get(entityClass, id) );
            }
        }, true);
    }

    @Override
    public Object get(final String entityName, final Serializable id, final LockMode lockMode)
            throws DataAccessException {
        return execute(new HibernateCallback() {
            public Object doInHibernate(Session session) throws HibernateException {
                if (lockMode != null) {
                    return getHibernateDtoCopier().hibernate2dtoFully( session.get(entityName, id, lockMode) );
                }
                return getHibernateDtoCopier().hibernate2dtoFully( session.get(entityName, id) );
            }
        }, true);
    }

    @Override
    public Object load(@SuppressWarnings("unchecked") final Class entityClass, 
                        final Serializable id, final LockMode lockMode)
            throws DataAccessException 
    {
        return execute(new HibernateCallback() {
            public Object doInHibernate(Session session) throws HibernateException {
                if (lockMode != null) {
                    return getHibernateDtoCopier().hibernate2dtoFully( session.load(entityClass, id, lockMode) );
                }
                return getHibernateDtoCopier().hibernate2dtoFully( session.load(entityClass, id) );
            }
        }, true);
    }

    @Override
    public Object load(final String entityName, final Serializable id, final LockMode lockMode)
            throws DataAccessException 
    {
        return execute(new HibernateCallback() {
            public Object doInHibernate(Session session) throws HibernateException {
                if (lockMode != null) {
                    return getHibernateDtoCopier().hibernate2dtoFully( session.load(entityName, id, lockMode) );
                }
                return getHibernateDtoCopier().hibernate2dtoFully( session.load(entityName, id) );
            }
        }, true);
    }


    @Override
    public @SuppressWarnings("unchecked") List loadAll(final Class entityClass) 
        throws DataAccessException 
    {
        return (List) execute(new HibernateCallback() {
            public Object doInHibernate(Session session) throws HibernateException {
                Criteria criteria = session.createCriteria(entityClass);
                prepareCriteria(criteria);
                return getHibernateDtoCopier().hibernate2dto(criteria.list());
            }
        }, true);
    }

    @SuppressWarnings("unchecked")
    public <T> List<T> loadByCriteria(final CriteriaSpecifiable specifier) 
        throws DataAccessException 
    {
        return (List<T>)execute(new HibernateCallback() {
            public Object doInHibernate(Session session) throws HibernateException {
                Criteria criteria = specifier.specify(session);
                prepareCriteria(criteria);
                return getHibernateDtoCopier().hibernate2dto(criteria.list());
            }
        }, true);
    }
    
    //-------------------------------------------------------------------------
    // Convenience finder methods for HQL strings
    //-------------------------------------------------------------------------
    
    @Override
    public @SuppressWarnings("unchecked") List find(final String queryString, final Object[] values) 
        throws DataAccessException 
    {
        return (List) execute(new HibernateCallback() {
            public Object doInHibernate(Session session) throws HibernateException {
                Query queryObject = session.createQuery(queryString);
                prepareQuery(queryObject);
                if (values != null) {
                    for (int i = 0; i < values.length; i++) {
                        queryObject.setParameter(i, values[i]);
                    }
                }
                return getHibernateDtoCopier().hibernate2dto(queryObject.list() /*, getSessionFactory() */);
            }
        }, true);
    }

    @Override
    public @SuppressWarnings("unchecked") List findByNamedParam(
                final String queryString, final String[] paramNames, final Object[] values)
            throws DataAccessException 
    {
        if (paramNames.length != values.length) {
            throw new IllegalArgumentException("Length of paramNames array must match length of values array");
        }
        return (List) execute(new HibernateCallback() {
            public Object doInHibernate(Session session) throws HibernateException {
                Query queryObject = session.createQuery(queryString);
                prepareQuery(queryObject);
                if (values != null) {
                    for (int i = 0; i < values.length; i++) {
                        applyNamedParameterToQuery(queryObject, paramNames[i], values[i]);
                    }
                }
                return getHibernateDtoCopier().hibernate2dto(queryObject.list() /*, getSessionFactory() */);
            }
        }, true);
    }

    @Override
    public @SuppressWarnings("unchecked") List findByValueBean(final String queryString, final Object valueBean)
            throws DataAccessException 
    {
        return (List) execute(new HibernateCallback() {
            public Object doInHibernate(Session session) throws HibernateException {
                Query queryObject = session.createQuery(queryString);
                prepareQuery(queryObject);
                queryObject.setProperties(valueBean);
                return getHibernateDtoCopier().hibernate2dto(queryObject.list() /*, getSessionFactory() */);
            }
        }, true);
    }


    //-------------------------------------------------------------------------
    // Convenience finder methods for named queries
    //-------------------------------------------------------------------------

    @Override
    public @SuppressWarnings("unchecked") List findByNamedQuery(final String queryName, final Object[] values) 
        throws DataAccessException 
    {
        return (List) execute(new HibernateCallback() {
            public Object doInHibernate(Session session) throws HibernateException {
                Query queryObject = session.getNamedQuery(queryName);
                prepareQuery(queryObject);
                if (values != null) {
                    for (int i = 0; i < values.length; i++) {
                        queryObject.setParameter(i, values[i]);
                    }
                }
                return getHibernateDtoCopier().hibernate2dto(queryObject.list() /*, getSessionFactory() */);
            }
        }, true);
    }

    @Override
    public @SuppressWarnings("unchecked") List findByNamedQueryAndNamedParam(
            final String queryName, final String[] paramNames, final Object[] values)
            throws DataAccessException 
    {
        if (paramNames != null && values != null && paramNames.length != values.length) {
            throw new IllegalArgumentException("Length of paramNames array must match length of values array");
        }
        return (List) execute(new HibernateCallback() {
            public Object doInHibernate(Session session) throws HibernateException {
                Query queryObject = session.getNamedQuery(queryName);
                prepareQuery(queryObject);
                if (values != null) {
                    for (int i = 0; i < values.length; i++) {
                        applyNamedParameterToQuery(queryObject, paramNames[i], values[i]);
                    }
                }
                return getHibernateDtoCopier().hibernate2dto(queryObject.list() /*, getSessionFactory() */);
            }
        }, true);
    }

    @Override
    public @SuppressWarnings("unchecked") List findByNamedQueryAndValueBean(final String queryName, final Object valueBean)
            throws DataAccessException 
    {
        return (List) execute(new HibernateCallback() {
            public Object doInHibernate(Session session) throws HibernateException {
                Query queryObject = session.getNamedQuery(queryName);
                prepareQuery(queryObject);
                queryObject.setProperties(valueBean);
                return getHibernateDtoCopier().hibernate2dto(queryObject.list() /*, getSessionFactory() */);
            }
        }, true);
    }

    @Override
    protected Session createSessionProxy(Session session) {
        Class<?>[] sessionIfcs = null;
        if (session instanceof SessionImplementor) {
            sessionIfcs = new Class[] {Session.class, SessionImplementor.class};
        }
        else {
            sessionIfcs = new Class[] {Session.class};
        }
        return (Session) Proxy.newProxyInstance(
                getClass().getClassLoader(), sessionIfcs,
                new DtoCentricCloseSuppressingInvocationHandler(session));
    }
    
    /**
     * Cloned from {@link org.springframework.orm.hibernate3.HibernateTemplate.CloseSuppressingInvocationHandler} 
     * so we can dig out the target session, if needs be.
     */
    public class DtoCentricCloseSuppressingInvocationHandler implements InvocationHandler {

        private final Session target;

        public DtoCentricCloseSuppressingInvocationHandler(Session target) {
            this.target = target;
        }

        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            // Invocation on Session interface coming in...

            if (method.getName().equals("equals")) {
                // Only consider equal when proxies are identical.
                return (proxy == args[0] ? Boolean.TRUE : Boolean.FALSE);
            }
            else if (method.getName().equals("hashCode")) {
                // Use hashCode of Session proxy.
                return new Integer(hashCode());
            }
            else if (method.getName().equals("close")) {
                // Handle close method: suppress, not valid.
                return null;
            }

            // Invoke method on target Session.
            try {
                Object retVal = method.invoke(this.target, args);

                // If return value is a Query or Criteria, apply transaction timeout.
                // Applies to createQuery, getNamedQuery, createCriteria.
                if (retVal instanceof Query) {
                    prepareQuery(((Query) retVal));
                }
                if (retVal instanceof Criteria) {
                    prepareCriteria(((Criteria) retVal));
                }

                return retVal;
            }
            catch (InvocationTargetException ex) {
                throw ex.getTargetException();
            }
        }

        public Session getTarget()
        {
            return target;
        }
    }
    
}
