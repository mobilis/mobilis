package de.tudresden.inf.rn.mobilis.server.persistence;

import java.util.List;
import java.util.logging.Level;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.Query;

import de.tudresden.inf.rn.mobilis.server.MobilisManager;
import de.tudresden.inf.rn.mobilis.server.persistence.model.GenericFile;

public class MobilisPersistence {
	private static final String PERSISTENCE_UNIT_NAME = "files";
	private static EntityManagerFactory factory;
	private static MobilisPersistence persistencyInstance;
	
	public MobilisPersistence() {
		factory = Persistence.createEntityManagerFactory(PERSISTENCE_UNIT_NAME);
		// TODO: set mode (DB or filesystem) based on corresponding MobilisSettings.xml entry
	}
	
	public static MobilisPersistence getInstance() {
		if (persistencyInstance == null) {
			persistencyInstance = new MobilisPersistence();
			EntityManager em = factory.createEntityManager();
			Query q = em.createQuery("select f from GenericFile f");
			@SuppressWarnings("unchecked")
			List<GenericFile> fileList = q.getResultList();
			for (GenericFile file : fileList) {
				System.out.println(file);
			}
			System.out.println("Size: " + fileList.size());
			
		}
		
		return persistencyInstance;
	}
	
	public void shutdown() {
		// TODO: check if all EntityManagers are closed (necessary?)
		factory.close();
	}
	
	/**
	 * Stores the given byte array in the database. Use the fileUserId to
	 * attach a name to it so you may find it later in the database.
	 * @param file
	 * 			the byte array to be stored in the database
	 * @param fileUserId
	 * 			a arbitrary String to identify the byte array
	 */
	public void storeFile(byte[] file, String fileUserId) {
		EntityManager em = factory.createEntityManager();
		
		em.getTransaction().begin();
		GenericFile genFile = new GenericFile();
		genFile.setFile(file);
		genFile.setFileUserId(fileUserId);
		em.persist(genFile);
		em.getTransaction().commit();
		
		em.close();
	}
	
	public byte[] loadFile(String fileUserId) {
		EntityManager em = factory.createEntityManager();
		
		Query q = em.createQuery("select f from GenericFile f where f.fileUserId = '" + fileUserId + "'");
		@SuppressWarnings("unchecked")
		List<GenericFile> fileList = q.getResultList();
		
		// TODO: is this really necessary?
		byte[] file = fileList.get(0).getFile().clone();
		
		em.close();
		return file;
	}
	
	public void deleteFile(String fileUserId) {
		EntityManager em = factory.createEntityManager();
		
		Query q = em.createQuery("select f from GenericFile f where f.fileUserId = '" + fileUserId + "'");
		@SuppressWarnings("unchecked")
		List<GenericFile> fileList = q.getResultList();
		
		if (fileList != null && fileList.get(0) != null) {
			em.getTransaction().begin();
			em.remove(fileList.get(0));
			em.getTransaction().commit();
		} else {
			MobilisManager.getLogger().log(Level.WARNING, "Attempted to remove non-existent file with fileUserId "
					+ fileUserId + " from database!");
		}
		
		em.close();
	}
	
}
