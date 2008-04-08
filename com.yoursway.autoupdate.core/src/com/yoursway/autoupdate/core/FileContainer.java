package com.yoursway.autoupdate.core;

import java.io.File;

import com.yoursway.autoupdate.core.versions.definitions.AppFile;
import com.yoursway.utils.fileset.FileSet;
import com.yoursway.utils.relativepath.RelativePath;

public interface FileContainer {
	
	AppFile resolve(RelativePath relativePath);
	
    /**
     * Implementations are encouraged to cache the results of this call.
     */
	FileSet allFiles();
	
	RelativePath relativePathOf(File file);

}
