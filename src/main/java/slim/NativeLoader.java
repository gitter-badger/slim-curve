/*
 * #%L
 * SLIM Curve package for exponential curve fitting of spectral lifetime data.
 * %%
 * Copyright (C) 2010 - 2014 Gray Institute University of Oxford and Board of
 * Regents of the University of Wisconsin-Madison.
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/gpl-3.0.html>.
 * #L%
 */

package slim;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Enumeration;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

/**
 * Provides a handy means of loading slim-curve native libraries from jar files
 * named "slim-curve-<i>VERSION</i>-natives-<i>PLATFORM</i>.jar"
 * <p>
 * The jar file is first extracted to (os specific) temporary directory and
 * loaded through <code>System.libraryLoader</code>.
 * 
 * @author Dasong Gao
 *
 */
public class NativeLoader {

	static final String ARTIFACTID = "slim-curve";

	
	/**
	 * Deletes old extracted libraries if not in use (.lock file not present)
	 */
	public static void cleanTempFiles() {
		try {
			File[] files = new File(System.getProperty("java.io.tmpdir")).listFiles();

			for (File file : files) {
				if (file.isDirectory() && file.getName().contains(ARTIFACTID + "-natives-tmp")) {
					File lock = new File(file, ".lock");

					// delete the temporary directory only if the lock does not
					// exist
					if (!lock.exists()) {
						Files.walk(file.toPath()).map(Path::toFile).sorted((f1, f2) -> -f1.compareTo(f2))
								.forEach(File::delete);
					}
				}
			}
		} catch (NullPointerException | IOException e) {
			System.err.println("Unable to delete leftover temporary directories: " + e);
			e.printStackTrace();
		}
	}

	/**
	 * @return a <code>File</code> containing the extracted libraries
	 * @throws IOException if an I/O error occurs or the temporary-file
	 * directory does not exist
	 */
	public static File extractNatives() throws IOException {
		cleanTempFiles();

		File tmpDir = Files.createTempDirectory(ARTIFACTID + "-natives-tmp").toFile();
		tmpDir.deleteOnExit();

		File lock = new File(tmpDir, ".lock");
		lock.createNewFile();
		lock.deleteOnExit();

		String[] classPaths = System.getProperty("java.class.path").split(File.pathSeparator);

		for (String cp : classPaths) {
			if (!(cp.contains(ARTIFACTID) && cp.contains("natives") && cp.endsWith(".jar")))
				continue;

			JarFile jar = new JarFile(cp);
			Enumeration<JarEntry> jEntries = jar.entries();
			while (jEntries.hasMoreElements()) {
				JarEntry entry = jEntries.nextElement();

				// only extract library files
				String extension = entry.getName().substring(entry.getName().lastIndexOf('.') + 1);
				if (!(extension.startsWith("so") || extension.startsWith("dll") || extension.startsWith("dylib")
						|| extension.startsWith("jnilib"))) {
					continue;
				}

				File flib = new File(tmpDir.getAbsolutePath() + File.separator + entry.getName());

				if (entry.isDirectory()) {
					flib.mkdir();
					continue;
				}

				// extract lib files
				Files.copy(jar.getInputStream(entry), flib.toPath(), StandardCopyOption.REPLACE_EXISTING);
				flib.deleteOnExit();
			}

			jar.close();
		}

		return tmpDir;
	}

	/**
	 * Loads the native lib of specified name. Extension is determined by
	 * <code>System.libraryLoader</code>.
	 * @param libName the name of native lib without lib- and .xx
	 */
	public static void loadLibrary(String libName) {
		String libPath = null;
		try {
			libPath = extractNatives().getCanonicalPath();
		} catch (IOException e) {
			System.err.println("Cannot extract naive library from jar: " + e);
			e.printStackTrace();
			System.exit(1);
		}

		String os = System.getProperty("os.name").toLowerCase();
		String specLibName = libName;

		if (os.contains("win")) {
			specLibName =  libName;
		} else if (os.contains("linux") || os.contains("mac")) {
			specLibName = "lib" + libName;
		}
		String lp = System.getProperty("java.library.path");
		System.setProperty("java.library.path", lp + File.pathSeparator + libPath);

		// System.setProperty does not change the cached sys_paths in ClassLoader 
		// See: https://stackoverflow.com/questions/5419039
		try {
            Field fieldSysPath = ClassLoader.class.getDeclaredField("sys_paths");
            fieldSysPath.setAccessible(true);
            fieldSysPath.set(null, null);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            System.err.println("Failed to set java.library.path: " + e);
            e.printStackTrace();
        }
		
		System.loadLibrary(specLibName);
	}
}
