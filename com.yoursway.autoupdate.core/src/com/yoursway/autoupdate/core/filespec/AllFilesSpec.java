package com.yoursway.autoupdate.core.filespec;

import com.yoursway.autoupdate.core.fileset.FileSet;
import com.yoursway.autoupdate.core.path.Path;

public class AllFilesSpec implements FileSetSpec {

	public boolean contains(Path file) {
		return true;
	}
	
	@Override
	public String toString() {
		return "*";
	}

	public boolean isKnownToBeEmpty() {
		return false;
	}
    
    public FileSet resolve(FileSet allFiles) {
        return allFiles;
    }

}
