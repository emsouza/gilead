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

import java.io.Serializable;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;
import java.util.Properties;

import net.sf.beanlib.hibernate3.DtoCentricHibernate3Template.DtoCentricCloseSuppressingInvocationHandler;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Settings;
import org.hibernate.dialect.Dialect;
import org.hibernate.id.IdentifierGenerator;
import org.hibernate.id.IdentifierGeneratorFactory;
import org.hibernate.impl.SessionFactoryImpl;
import org.hibernate.impl.SessionImpl;
import org.hibernate.type.TypeFactory;
import org.springframework.orm.hibernate3.HibernateTemplate;

/**
 * Hibernate 3 DB Sequence Generator.
 * 
 * @author Joe D. Velopar
 */
public class Hibernate3SequenceGenerator
{
    private Hibernate3SequenceGenerator() {
    }
    
    /** Returns the next sequence id from the specified sequence and session. */
    public static long nextval(final String sequenceName, final Session session) 
    {
        Object target = session;
        
        if (Proxy.isProxyClass(session.getClass())) 
        {
            // Dig out the underlying session.
            InvocationHandler invocationHandler = Proxy.getInvocationHandler(session);
            
            if (invocationHandler instanceof DtoCentricCloseSuppressingInvocationHandler) 
            {
                // This is faster for we don't need to use reflection.
                DtoCentricCloseSuppressingInvocationHandler dch = (DtoCentricCloseSuppressingInvocationHandler)invocationHandler;
                target = dch.getTarget();
            }
            else {
                Class<?> invocationHandlerClass = invocationHandler.getClass();
                Class<?> invocationHandlerDeclaringClass = invocationHandlerClass.getDeclaringClass();
                
                if (invocationHandlerDeclaringClass == HibernateTemplate.class) 
                {
                    String className = invocationHandlerClass.getName();
                    
                    if (className.endsWith("CloseSuppressingInvocationHandler")) 
                    {
                        // Assume this is the private class org.springframework.orm.hibernate3.HibernateTempate$CloseSuppressingInvocationHandler
                        // Dig out the private target.  
                        // I know this is bad, but there doesn't seem to be a better way.  Oh well.
                        try {
                            Field f = invocationHandlerClass.getDeclaredField("target");
                            f.setAccessible(true);
                            target = f.get(invocationHandler);
                        } catch (SecurityException e) {
                            throw new RuntimeException(e);
                        } catch (NoSuchFieldException e) {
                            throw new RuntimeException(e);
                        } catch (IllegalAccessException e) {
                            throw new RuntimeException(e);
                        }
                        
                    }
                }
                
            }
        }
        SessionImpl sessionImpl;
        
        if (target instanceof SessionImpl)
            sessionImpl = (SessionImpl)target;
        else
            throw new IllegalStateException("Not yet know how to handle the given session!");
        IdentifierGenerator idGenerator = createIdentifierGenerator(sequenceName, session);
        Serializable id = idGenerator.generate(sessionImpl, null);
        return (Long)id;
    }
    
    /** Returns the identifier generator created for the specified sequence and session. */
    private static IdentifierGenerator createIdentifierGenerator(String sequenceName, Session session) 
    {
        SessionFactory sessionFactory = session.getSessionFactory();
        
        if (!(sessionFactory instanceof SessionFactoryImpl))
            throw new IllegalStateException("Not yet know how to handle the session factory of the given session!");
        SessionFactoryImpl sessionFactoryImpl = (SessionFactoryImpl)sessionFactory;
        Settings settings = sessionFactoryImpl.getSettings();
        Dialect dialect = settings.getDialect();

        Properties params = new Properties();
        params.setProperty("sequence", sequenceName);
        
        return IdentifierGeneratorFactory.create(
                "sequence",
                TypeFactory.heuristicType("long"),
                params,
                dialect
            );
    }
}
