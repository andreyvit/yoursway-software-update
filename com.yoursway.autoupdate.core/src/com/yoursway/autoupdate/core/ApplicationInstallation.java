package com.yoursway.autoupdate.core;

import java.io.File;

public class ApplicationInstallation {

	public FileContainer getFileContainer() {
		File applicationPath = new File("/Applications/Eclipse 3.4M5");
		return new LocalFileContainer(applicationPath);
	}

}
