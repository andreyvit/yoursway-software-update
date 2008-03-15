package com.yoursway.autoupdate.core;

import java.util.Collection;

import com.yoursway.autoupdate.core.path.Path;
import com.yoursway.autoupdate.core.versiondef.AppFile;

public interface FileContainer {
	
	AppFile resolve(Path path);
	
	Collection<Path> allFiles();

}
