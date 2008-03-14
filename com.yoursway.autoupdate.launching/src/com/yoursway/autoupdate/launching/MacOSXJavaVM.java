package com.yoursway.autoupdate.launching;

import java.io.File;
import java.io.IOException;

public class MacOSXJavaVM extends AbstractJavaVM {

	/*
	 * The directory structure for Java VMs is as follows:
	 * 
	 * 	/System/Library/Frameworks/JavaVM.framework/Versions/
	 * 		1.3.1
	 * 			Classes
	 * 				classes.jar
	 * 				ui.jar
	 * 			Home
	 * 				src.jar
	 * 		1.4.1
	 * 			Classes
	 * 				classes.jar
	 * 				ui.jar
	 * 			Home
	 * 				src.jar
	 * 		CurrentJDK -> 1.3.1
	 */
	 
	private static final String JAVA_VM_NAME= "Java HotSpot(TM) Client VM";	//$NON-NLS-1$
	
	/** The OS keeps all the JVM versions in this directory */
	private static final String JVM_VERSION_LOC= "/System/Library/Frameworks/JavaVM.framework/Versions/";	//$NON-NLS-1$
	/** The name of a Unix link to MacOS X's default VM */
	private static final String CURRENT_JVM= "CurrentJDK";	//$NON-NLS-1$
	/** The root of a JVM */
	private static final String JVM_ROOT= "Home";	//$NON-NLS-1$
	/** The doc (for all JVMs) lives here (if the developer kit has been expanded)*/
	private static final String JAVADOC_LOC= "/Developer/Documentation/Java/Reference/";	//$NON-NLS-1$
	/** The doc for 1.4.1 is kept in a sub directory of the above. */ 
	private static final String JAVADOC_SUBDIR= "/doc/api";	//$NON-NLS-1$
	
	/*
	 * @see IVMInstallType#detectInstallLocation()
	 */
	public File detectInstallLocation() {
		
		String javaVMName= System.getProperty("java.vm.name");	//$NON-NLS-1$
		if (javaVMName == null || !JAVA_VM_NAME.equals(javaVMName)) 
			return null;

		// find all installed VMs
		File defaultLocation= null;
		File versionDir= new File(JVM_VERSION_LOC);
		if (versionDir.exists() && versionDir.isDirectory()) {
			File currentJDK= new File(versionDir, CURRENT_JVM);
			try {
				currentJDK= currentJDK.getCanonicalFile();
			} catch (IOException ex) {
				// NeedWork
			}
			File[] versions= versionDir.listFiles();
			for (int i= 0; i < versions.length; i++) {
				String version= versions[i].getName();
				File home= new File(versions[i], JVM_ROOT);
				if (home.exists()) {
					boolean isDefault= currentJDK.equals(versions[i]);
//					IVMInstall install= findVMInstall(version);
//					if (install == null && !CURRENT_JVM.equals(version)) {
//						VMStandin vm= new VMStandin(this, version);
//						vm.setInstallLocation(home);
//						String format= MacOSXLaunchingPlugin.getString(isDefault
//													? "MacOSXVMType.jvmDefaultName"		//$NON-NLS-1$
//													: "MacOSXVMType.jvmName");				//$NON-NLS-1$
//						vm.setName(MessageFormat.format(format, new Object[] { version } ));
//						vm.setLibraryLocations(getDefaultLibraryLocations(home));
//						URL doc= getDefaultJavadocLocation(home);
//						if (doc != null)
//							vm.setJavadocLocation(doc);
//						
//						IVMInstall rvm= vm.convertToRealVM();					
//						if (isDefault) {
//							defaultLocation= home;
//							try {
//								JavaRuntime.setDefaultVMInstall(rvm, null);
//							} catch (CoreException e) {
//								LaunchingPlugin.log(e);
//							}
//						}
//					} else {
						if (isDefault) {
							defaultLocation= home;
//							try {
//								JavaRuntime.setDefaultVMInstall(install, null);
//							} catch (CoreException e) {
//								LaunchingPlugin.log(e);
//							}
						}
//					}
				}
			}
		}
		return defaultLocation;
	}
	
}
