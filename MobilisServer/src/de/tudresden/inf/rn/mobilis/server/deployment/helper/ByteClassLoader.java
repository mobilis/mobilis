package de.tudresden.inf.rn.mobilis.server.deployment.helper;

import java.net.URL;
import java.net.URLClassLoader;
import java.util.HashMap;
import java.util.Map;

/**
 * This classloader takes care of loading classes from byte arrays.
 * 
 * @see <a href="http://stackoverflow.com/questions/1781091/java-how-to-load-class-stored-as-byte-into-the-jvm">stackoverflow.com</a>
 *
 */
public class ByteClassLoader extends URLClassLoader {

	private final Map<String, byte[]> extraClassDefs;

    public ByteClassLoader(URL[] urls, ClassLoader parent, Map<String, byte[]> extraClassDefs) {
      super(urls, parent);
      this.extraClassDefs = new HashMap<String, byte[]>(extraClassDefs);
    }

    public ByteClassLoader() {
    	this(new URL[] {}, null, new HashMap<String, byte[]>());
	}

	@Override
    protected Class<?> findClass(final String name) throws ClassNotFoundException {
      byte[] classBytes = this.extraClassDefs.remove(name);
      if (classBytes != null) {
        return defineClass(name, classBytes, 0, classBytes.length); 
      }
      return super.findClass(name);
    }
}
