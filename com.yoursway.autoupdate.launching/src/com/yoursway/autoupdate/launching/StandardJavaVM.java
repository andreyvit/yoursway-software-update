package com.yoursway.autoupdate.launching;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.ListIterator;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.osgi.service.environment.Constants;

public class StandardJavaVM extends AbstractJavaVM {

	/**
	 * Convenience handle to the system-specific file separator character
	 */															
	private static final char fgSeparator = File.separatorChar;

	/**
	 * The list of locations in which to look for the java executable in candidate
	 * VM install locations, relative to the VM install location.
	 */
	private static final String[] fgCandidateJavaFiles = {"javaw", "javaw.exe", "java", "java.exe", "j9w", "j9w.exe", "j9", "j9.exe"}; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$ //$NON-NLS-7$ //$NON-NLS-8$
	private static final String[] fgCandidateJavaLocations = {"bin" + fgSeparator, "jre" + fgSeparator + "bin" + fgSeparator}; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
	
	/**
	 * Starting in the specified VM install location, attempt to find the 'java' executable
	 * file.  If found, return the corresponding <code>File</code> object, otherwise return
	 * <code>null</code>.
	 */
	public static File findJavaExecutable(File vmInstallLocation) {
		// Try each candidate in order.  The first one found wins.  Thus, the order
		// of fgCandidateJavaLocations and fgCandidateJavaFiles is significant.
		for (int i = 0; i < fgCandidateJavaFiles.length; i++) {
			for (int j = 0; j < fgCandidateJavaLocations.length; j++) {
				File javaFile = new File(vmInstallLocation, fgCandidateJavaLocations[j] + fgCandidateJavaFiles[i]);
				if (javaFile.isFile()) {
					return javaFile;
				}				
			}
		}		
		return null;							
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.jdt.launching.IVMInstallType#detectInstallLocation()
	 */
	/* (non-Javadoc)
	 * @see com.yoursway.autoupdate.launching.IJavaVM#detectInstallLocation()
	 */
	public File detectInstallLocation() {
		// do not detect on the Mac OS
		if (Platform.getOS().equals(Constants.OS_MACOSX)) {
			return null;
		}		
		
		// Retrieve the 'java.home' system property.  If that directory doesn't exist, 
		// return null.
		File javaHome; 
		try {
			javaHome= new File (System.getProperty("java.home")).getCanonicalFile(); //$NON-NLS-1$
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
		if (!javaHome.exists()) {
			return null;
		}

		// Find the 'java' executable file under the java home directory.  If it can't be
		// found, return null.
		File javaExecutable = findJavaExecutable(javaHome);
		if (javaExecutable == null) {
			return null;
		}
		
		// If the reported java home directory terminates with 'jre', first see if 
		// the parent directory contains the required libraries
		boolean foundLibraries = false;
		if (javaHome.getName().equalsIgnoreCase("jre")) { //$NON-NLS-1$
			File parent= new File(javaHome.getParent());			
			if (canDetectDefaultSystemLibraries(parent, javaExecutable)) {
				javaHome = parent;
				foundLibraries = true;
			}
		}	
		
		// If we haven't already found the libraries, look in the reported java home dir
		if (!foundLibraries) {
			if (!canDetectDefaultSystemLibraries(javaHome, javaExecutable)) {
				return null;
			}			
		}
		
		return javaHome;
	}


	/**
	 * Return <code>true</code> if the appropriate system libraries can be found for the
	 * specified java executable, <code>false</code> otherwise.
	 */
	protected boolean canDetectDefaultSystemLibraries(File javaHome, File javaExecutable) {
		LibraryLocation[] locations = getDefaultLibraryLocations(javaHome);
		return locations.length > 0;
	}
	
	
	/**
	 * NOTE: We do not add libraries from the "endorsed" directory explicitly, as
	 * the bootpath contains these entries already (if they exist).
	 * 
	 * @see org.eclipse.jdt.launching.IVMInstallType#getDefaultLibraryLocations(File)
	 */
	public LibraryLocation[] getDefaultLibraryLocations(File installLocation) {
		// Determine the java executable that corresponds to the specified install location
		// and use this to generate library information.  If no java executable was found, 
		// the 'standard' libraries will be returned.
//		File javaExecutable = findJavaExecutable(installLocation);
		LibraryInfo libInfo;
//		if (javaExecutable == null) {
			libInfo = getDefaultLibraryInfo(installLocation);
		// } else {
		// libInfo = getLibraryInfo(installLocation, javaExecutable);
		//		}
		
		// Add all endorsed libraries - they are first, as they replace
		List allLibs = new ArrayList(gatherAllLibraries(libInfo.getEndorsedDirs()));
		
		// next is the boot path libraries
		String[] bootpath = libInfo.getBootpath();
		List boot = new ArrayList(bootpath.length);
//		URL url = getDefaultJavadocLocation(installLocation);
		URL url = null;
		for (int i = 0; i < bootpath.length; i++) {
			IPath path = new Path(bootpath[i]);
			File lib = path.toFile(); 
			if (lib.exists() && lib.isFile()) {
				LibraryLocation libraryLocation = new LibraryLocation(path,
								null, //getDefaultSystemLibrarySource(lib),
								null, //getDefaultPackageRootPath(),
								url);
				boot.add(libraryLocation);
			}
		}
		allLibs.addAll(boot);
				
		// Add all extension libraries
		allLibs.addAll(gatherAllLibraries(libInfo.getExtensionDirs()));
		
		//remove duplicates
		HashSet set = new HashSet();
		LibraryLocation lib = null;
		for(ListIterator liter = allLibs.listIterator(); liter.hasNext();) {
			lib = (LibraryLocation) liter.next();
			IPath systemLibraryPath = lib.getSystemLibraryPath();
			String device = systemLibraryPath.getDevice();
			if (device != null) {
				// @see Bug 197866 - Installed JRE Wizard creates duplicate system libraries when drive letter is lower case
				systemLibraryPath = systemLibraryPath.setDevice(device.toUpperCase());
			}
			if(!set.add(systemLibraryPath.toOSString())) {
				//did not add it, duplicate
				liter.remove();
			}
		}
		return (LibraryLocation[])allLibs.toArray(new LibraryLocation[allLibs.size()]);
	}
	
	
	
	
	/**
	 * Returns default library information for the given install location.
	 * 
	 * @param installLocation
	 * @return LibraryInfo
	 */
	protected LibraryInfo getDefaultLibraryInfo(File installLocation) {
		IPath rtjar = getDefaultSystemLibrary(installLocation);
		File extDir = getDefaultExtensionDirectory(installLocation);
		File endDir = getDefaultEndorsedDirectory(installLocation);
		String[] dirs = null;
		if (extDir == null) {
			dirs = new String[0];
		} else {
			dirs = new String[] {extDir.getAbsolutePath()};
		}
		String[] endDirs = null;
		if (endDir == null) {
			endDirs = new String[0]; 
		} else {
			endDirs = new String[] {endDir.getAbsolutePath()};
		}
		return new LibraryInfo("???", new String[] {rtjar.toOSString()}, dirs, endDirs);		 //$NON-NLS-1$
	}
	
	/**
	 * Returns a list of all zip's and jars contained in the given directories.
	 * 
	 * @param dirPaths a list of absolute paths of directories to search
	 * @return List of all zip's and jars
	 */
	protected static List gatherAllLibraries(String[] dirPaths) {
		List libraries = new ArrayList();
		for (int i = 0; i < dirPaths.length; i++) {
			File extDir = new File(dirPaths[i]);
			if (extDir.exists() && extDir.isDirectory()) {
				String[] names = extDir.list();
				for (int j = 0; j < names.length; j++) {
					String name = names[j];
					File jar = new File(extDir, name);
					if (jar.isFile()) {
						int length = name.length();
						if (length > 4) {
							String suffix = name.substring(length - 4);
							if (suffix.equalsIgnoreCase(".zip") || suffix.equalsIgnoreCase(".jar")) { //$NON-NLS-1$ //$NON-NLS-2$
								try {
									IPath libPath = new Path(jar.getCanonicalPath());
									LibraryLocation library = new LibraryLocation(libPath, Path.EMPTY, Path.EMPTY, null);
									libraries.add(library);
								} catch (IOException e) {
									e.printStackTrace();
								}
							}
						}
					}
				}
			}			
		}
		return libraries;
	}
	
	
	/**
	 * Returns the default location of the extension directory, based on the given
	 * install location. The resulting file may not exist, or be <code>null</code>
	 * if an extension directory is not supported.
	 * 
	 * @param installLocation 
	 * @return default extension directory or <code>null</code>
	 */
	protected File getDefaultExtensionDirectory(File installLocation) {
		File jre = null;
		if (installLocation.getName().equalsIgnoreCase("jre")) { //$NON-NLS-1$
			jre = installLocation;
		} else {
			jre = new File(installLocation, "jre"); //$NON-NLS-1$
		}
		File lib = new File(jre, "lib"); //$NON-NLS-1$
		File ext = new File(lib, "ext"); //$NON-NLS-1$
		return ext;
	}

	/**
	 * Returns the default location of the endorsed directory, based on the
	 * given install location. The resulting file may not exist, or be
	 * <code>null</code> if an endorsed directory is not supported.
	 * 
	 * @param installLocation 
	 * @return default endorsed directory or <code>null</code>
	 */
	protected File getDefaultEndorsedDirectory(File installLocation) {
		File lib = new File(installLocation, "lib"); //$NON-NLS-1$
		File ext = new File(lib, "endorsed"); //$NON-NLS-1$
		return ext;
	}
	
	/**
	 * Return an <code>IPath</code> corresponding to the single library file containing the
	 * standard Java classes for most VMs version 1.2 and above.
	 */
	protected IPath getDefaultSystemLibrary(File javaHome) {
		IPath jreLibPath= new Path(javaHome.getPath()).append("lib").append("rt.jar"); //$NON-NLS-2$ //$NON-NLS-1$
		if (jreLibPath.toFile().isFile()) {
			return jreLibPath;
		}
		return new Path(javaHome.getPath()).append("jre").append("lib").append("rt.jar"); //$NON-NLS-3$ //$NON-NLS-2$ //$NON-NLS-1$
	}
}
