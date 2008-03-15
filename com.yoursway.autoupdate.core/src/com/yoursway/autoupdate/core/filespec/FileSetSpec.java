package com.yoursway.autoupdate.core.filespec;

import com.yoursway.autoupdate.core.path.Path;
import com.yoursway.autoupdate.core.versiondef.AppFile;


public interface FileSetSpec {
	
//	Collection<Path> resolve();

	boolean contains(Path file);
	
	boolean isKnownToBeEmpty();

}
