package de.tudresden.inf.rn.mobilis.server.deployment.helper;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;

/**
 * The Class JarClassLoader to load classes from jar files.
 * 
 * @see http://download.oracle.com/javase/tutorial/deployment/jar/jarclassloader.html
 */

public class JarClassLoader extends URLClassLoader {

	/** The prefix for linux-based systems. */
	private static String FILE_JAR_PREFIX_LINUX = "jar:file://";

	/** The prefix for windows.based systems. */
	private static String FILE_JAR_PREFIX_WINDOWS = "jar:file:/";

	/** The default path of the manifest file in a jar file. */
	public static String MANIFEST_FILE_PATH = "META-INF/MANIFEST.MF";

	/**
	 * Instantiates a new jar class loader with an empty URL array.
	 */
	public JarClassLoader() {
		super( new URL[] {} );
	}

	/**
	 * Instantiates a new jar class loader with the given urls.
	 * 
	 * @param urls
	 *            the urls for relative files in a jar file
	 */
	public JarClassLoader(URL[] urls) {
		super( urls );
	}

	/**
	 * Adds an absolute jar file path to the class loader.
	 * 
	 * @param path
	 *            the absolute path to the jar file
	 * @throws MalformedURLException
	 *             the malformed url exception
	 */
	public void addFilePathAbsolute( String path ) throws MalformedURLException {
		StringBuilder sbUrlPath = new StringBuilder();
		String osName = System.getProperty( "os.name" ).toLowerCase();

		if ( osName.contains( "windows" ) )
			sbUrlPath.append( FILE_JAR_PREFIX_WINDOWS );
		else
			sbUrlPath.append( FILE_JAR_PREFIX_LINUX );

		sbUrlPath.append( path );
		sbUrlPath.append( "!/" );

		addURL( new URL( sbUrlPath.toString() ) );
	}

	/**
	 * Adds an relative jar file path to the class loader.
	 * 
	 * @param path
	 *            the relative path to the jar file from user dir
	 * @throws MalformedURLException
	 *             the malformed url exception
	 */
	public void addFilePathRelative( String path ) throws MalformedURLException {
		addFilePathAbsolute( System.getProperty( "user.dir" ) + File.separator + path );
	}

}