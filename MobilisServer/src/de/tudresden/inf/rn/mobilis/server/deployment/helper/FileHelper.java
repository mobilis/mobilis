package de.tudresden.inf.rn.mobilis.server.deployment.helper;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.jar.Attributes;
import java.util.jar.JarFile;
import java.util.jar.Manifest;
import java.util.logging.Level;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import de.tudresden.inf.rn.mobilis.server.MobilisManager;

/**
 * The Class FileHelper to simplify the file handling.
 */
public abstract class FileHelper {

	/**
	 * Creates a file from an input stream.
	 * 
	 * @param inputStream
	 *            the input stream of the file
	 * @param filePath
	 *            the absolute file path for the output file
	 * @return the written file
	 */
	public static File createFileFromInputStream( InputStream inputStream, String filePath ) {
		File file = null;

		if ( null != inputStream ) {
			try {
				file = new File( filePath );

				// if file doesn't exist, create it
				if ( !file.exists() )
					file.createNewFile();

				OutputStream outputStream = new FileOutputStream( file );
				byte buffer[] = new byte[1024];
				int length;

				while ( ( length = inputStream.read( buffer ) ) > 0 )
					outputStream.write( buffer, 0, length );

				outputStream.close();
				inputStream.close();
			} catch ( IOException e ) {
				MobilisManager.getLogger().log( Level.WARNING,
						"createFileFromInputStream Error: " + e.getMessage() );
			}
		}

		return file;
	}

	/**
	 * Gets the filenames of a path, filtered by extension.
	 * 
	 * @param absolutePath
	 *            the absolute path were to start at
	 * @param extensionFilters
	 *            list of extensions starting with the dot e.g. ".jar"
	 * @return the filenames which were found
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	public static ArrayList< String > getFilenames( String absolutePath,
			final String[] extensionFilters ) throws IOException {
		ArrayList< String > files = new ArrayList< String >();

		if ( null != absolutePath && !absolutePath.isEmpty() ) {
			File rootDir = new File( absolutePath );
			File tmpFile = new File( absolutePath );
			String[] filenames;

			if ( null == extensionFilters || extensionFilters.length == 0 ) {
				filenames = rootDir.list();
			} else {
				filenames = rootDir.list( new FilenameFilter() {
					@Override
					public boolean accept( File dir, String name ) {
						if ( !name.contains( "." ) ) {
							return true;
						} else {
							for ( int i = 0; i < extensionFilters.length; i++ ) {
								if ( name.endsWith( extensionFilters[i] ) )
									return true;
							}
						}

						return false;
					}
				} );
			}

			for ( int i = 0; i < filenames.length; i++ ) {
				tmpFile = new File( absolutePath + File.separator + filenames[i] );

				if ( tmpFile.isDirectory() )
					files.addAll( getFilenames( tmpFile.getCanonicalPath(), extensionFilters ) );
				else
					files.add( absolutePath + File.separator + filenames[i] );
			}
		}

		return files;
	}

	/**
	 * Gets queries a value of the manifest of a jar file by a specific key.
	 * 
	 * @param jarFile
	 *            the jar file which should be queried
	 * @param key
	 *            the key of the value in the manifest file
	 * @return the value which was found by the key
	 */
	public static String getJarManifestValue( File jarFile, String key ) {
		String value = null;

		try {
			if ( jarFile.exists() ) {
				JarFile jarfile = new JarFile( jarFile.getAbsoluteFile() );
				Manifest manifest = jarfile.getManifest();
				Attributes attrs = (Attributes)manifest.getMainAttributes();

				value = attrs.getValue( key );
				jarfile.close();
			}
		} catch ( IOException e ) {
			e.printStackTrace();
		}

		return value;
	}

	/**
	 * Gets the a list of files in a jar file.
	 * 
	 * @param jarFile
	 *            the jar file which should be queried
	 * @return the packed files of the jar file as relative paths
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	public static List< String > getJarFiles( File jarFile ) throws IOException {
		List< String > jarFiles = new ArrayList< String >();

		FileInputStream fileInputStream = new FileInputStream( jarFile );
		ZipInputStream zipInputStream = new ZipInputStream( fileInputStream );
		ZipEntry zipEntry;

		while ( ( zipEntry = zipInputStream.getNextEntry() ) != null ) {
			jarFiles.add( zipEntry.getName() );
		}

		zipInputStream.close();
		fileInputStream.close();

		return jarFiles;
	}

	/**
	 * Gets the a list of files in a jar file filtered by extension.
	 * 
	 * @param jarFile
	 *            the jar file which should be queried
	 * @param fileExtension
	 *            the file extension of the wanted files
	 * @return the packed files of the jar file as relative paths
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	public static List< String > getJarFiles( File jarFile, String fileExtension )
			throws IOException {
		List< String > jarFiles = FileHelper.getJarFiles( jarFile );
		List< String > filteredJarFiles = new ArrayList< String >();

		for ( String file : jarFiles ) {
			if ( file.toLowerCase().indexOf( fileExtension ) > -1
					&& ( file.length() - file.indexOf( fileExtension ) ) == fileExtension.length() ) {
				filteredJarFiles.add( file );
			}
		}

		return filteredJarFiles;
	}
}
