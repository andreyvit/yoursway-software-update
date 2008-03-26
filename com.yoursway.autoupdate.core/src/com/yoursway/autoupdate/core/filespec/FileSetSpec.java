package com.yoursway.autoupdate.core.filespec;

import com.yoursway.autoupdate.core.fileset.FileSet;
import com.yoursway.autoupdate.core.path.Path;

public interface FileSetSpec {
	
    FileSet resolve(FileSet allFiles);

	boolean contains(Path file);
	
	boolean isKnownToBeEmpty();

}
