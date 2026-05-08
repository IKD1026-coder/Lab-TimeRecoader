package library.file;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class JarAccess {

	public static ClassLoader createClassLoader(String dirname) throws java.io.IOException {
		java.net.URL[] url = new java.net.URL[1];
		java.io.File file;
		if (dirname.endsWith("/")) {
			file = new java.io.File(dirname);
		} else {
			// ディレクトリは最後にスラッシュが必要
			file = new java.io.File(dirname + "/");
		}
		url[0] = file.toURI().toURL();

		ClassLoader parent = ClassLoader.getSystemClassLoader();
		java.net.URLClassLoader loader = new java.net.URLClassLoader(url, parent);

		return loader;
	}

	public static BufferedReader getBufferClassLoad(String dir) throws IOException {
		ClassLoader CL = createClassLoader((new JarAccess().getClass().getResource("")).getPath());
		return new BufferedReader(
				new InputStreamReader(CL.getResource(dir).openStream(), "UTF-8"));
	}

}
