package de.tudresden.inf.rn.mobilis.server.persistency;

import java.util.List;

/**
 * The Interface IORPersistenceImplementor is an interface to simplify the
 * database handling while using helpers like hibernate or derby. This interafce
 * is using the CRUD-Method.
 */
public interface IORPersistenceImplementor {

	/**
	 * Adds an annotated class which will be used as OR-class to map into the
	 * database.
	 * 
	 * @param clazz
	 *            the clazz which is annotated
	 */
	void addAnnotatedClass( Class clazz );

	/**
	 * Insert command.
	 * 
	 * @param entity
	 *            the entity to insert something
	 * @return true, if successful
	 */
	boolean insert( Object entity );

	/**
	 * Update command.
	 * 
	 * @return true, if successful
	 */
	boolean update();

	/**
	 * Query sql command.
	 * 
	 * @param queryString
	 *            the string to query something from database
	 * @return the list of matched objects
	 */
	List< Object > querySQL( String queryString );

	/**
	 * Delete command.
	 * 
	 * @return true, if successful
	 */
	boolean delete();
}
