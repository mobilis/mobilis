package de.tudresden.inf.rn.mobilis.server.persistency;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.Query;
import org.hibernate.Session;

import de.tudresden.inf.rn.mobilis.server.HibernateUtil;

public class PIHibernate implements IORPersistenceImplementor {

	@Override
	public void addAnnotatedClass( Class clazz ) {
		HibernateUtil.getAnnotationconfiguration().addAnnotatedClass( clazz );
		HibernateUtil.rebuildSessionFactory();
	}

	@Override
	public boolean insert( Object entity ) {
		Session session = HibernateUtil.getSessionFactory().getCurrentSession();

		session.beginTransaction();
		session.save( entity );
		session.getTransaction().commit();

		return session.getTransaction().wasCommitted();
	}

	@Override
	public boolean update() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public List< Object > querySQL( String queryString ) {
		List< Object > queryResultList = new ArrayList< Object >();

		Session session = HibernateUtil.getSessionFactory().getCurrentSession();
		session.beginTransaction();

		Query result = session.createSQLQuery( queryString );
		queryResultList = result.list();

		session.getTransaction().commit();

		return queryResultList;
	}

	@Override
	public boolean delete() {
		// TODO Auto-generated method stub
		return false;
	}

}
