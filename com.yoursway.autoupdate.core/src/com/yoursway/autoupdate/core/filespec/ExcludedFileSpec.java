package com.yoursway.autoupdate.core.filespec;

import com.yoursway.autoupdate.core.path.Path;

public class ExcludedFileSpec implements FileSetSpec {
	
	private final FileSetSpec included;
	private final FileSetSpec excluded;

	public ExcludedFileSpec(FileSetSpec included, FileSetSpec excluded) {
		this.included = included;
		this.excluded = excluded;
	}

	public boolean contains(Path file) {
		return included.contains(file) && !excluded.contains(file);
	}
	
	@Override
	public String toString() {
		if (excluded.isKnownToBeEmpty())
			return included.toString();
		else
			return "(" + included + " WITHOUT " + excluded + ")";
	}

	public boolean isKnownToBeEmpty() {
		return included.isKnownToBeEmpty();
	}

}
