package com.yoursway.autoupdate.core;

import com.yoursway.autoupdate.core.fileset.FileSet;
import com.yoursway.autoupdate.core.path.Path;
import com.yoursway.autoupdate.core.versiondef.AppFile;

public interface FileContainer {
	
	AppFile resolve(Path path);
	
	FileSet allFiles();

}
