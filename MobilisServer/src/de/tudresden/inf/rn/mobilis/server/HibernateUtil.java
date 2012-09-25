/*******************************************************************************
 * Copyright (C) 2010 Technische Universit�t Dresden
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * 	http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * 
 * Dresden, University of Technology, Faculty of Computer Science
 * Computer Networks Group: http://www.rn.inf.tu-dresden.de
 * mobilis project: http://mobilisplatform.sourceforge.net
 ******************************************************************************/
package de.tudresden.inf.rn.mobilis.server;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.AnnotationConfiguration;

public class HibernateUtil {
	
	private static SessionFactory sessionFactory;
	private static final AnnotationConfiguration annotationConfiguration;
	
	private static final ThreadLocal<Session> sessionThreadLocal = new ThreadLocal<Session>();
	private static final ThreadLocal<Transaction> transactionThreadLocal = new ThreadLocal<Transaction>();
	
	
	static {
		annotationConfiguration = new AnnotationConfiguration().configure("/META-INF/hibernate.xml");
		sessionFactory = annotationConfiguration.buildSessionFactory();
	}
	
	public static AnnotationConfiguration getAnnotationconfiguration() {
		return annotationConfiguration;
	}
	
	public static void rebuildSessionFactory(){
		sessionFactory = annotationConfiguration.buildSessionFactory();
	}
	
	public static SessionFactory getSessionFactory() {
        return sessionFactory;
    }
	
	public static Session getSession() {
		Session s = HibernateUtil.sessionThreadLocal.get();
		if (s == null) {
			s = HibernateUtil.sessionFactory.openSession();            
			HibernateUtil.sessionThreadLocal.set(s);
		} 
		return s;
	}
	
	public static void closeSession() throws HibernateException {
		Session s = HibernateUtil.sessionThreadLocal.get();
		HibernateUtil.sessionThreadLocal.set(null);
		if (s != null && s.isOpen()) s.close();
	}
	
	public static void beginTransaction() throws HibernateException {
		try {
			Transaction t = transactionThreadLocal.get();
			if (t == null) {
				t = getSession().beginTransaction();
				transactionThreadLocal.set(t);
			}
		} catch(Exception e) { throw new HibernateException(e); }
	}
	
	public static void commitTransaction() throws HibernateException {
		try {
			Transaction t = transactionThreadLocal.get();
			if(t != null && !t.wasCommitted() && !t.wasRolledBack()) {
				t.commit();
				transactionThreadLocal.set(null);
			}
		} catch(Exception e) {
			HibernateUtil.rollBackTransaction();
			throw new HibernateException(e);
		}
	}
	
	public static void rollBackTransaction() throws HibernateException {
		Transaction t = (Transaction) transactionThreadLocal.get();
		transactionThreadLocal.set(null);
		try {
			if(t != null && !t.wasCommitted() && !t.wasRolledBack())
				t.rollback();
		} finally {
			HibernateUtil.closeSession();
		}
	}
	
}
