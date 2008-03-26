package com.yoursway.autoupdate.core.filespec;

import static com.google.common.base.Functions.TO_STRING;
import static com.google.common.collect.Iterables.transform;
import static com.google.common.collect.Lists.newArrayListWithCapacity;
import static com.google.common.collect.Lists.sortedCopy;

import java.util.Collection;
import java.util.Set;

import com.google.common.collect.Sets;
import com.yoursway.autoupdate.core.fileset.FileSet;
import com.yoursway.autoupdate.core.path.Path;

public class ConcreteFilesSpec implements FileSetSpec {
	
	private final Set<Path> files;

	public ConcreteFilesSpec(Collection<Path> files) {
		this.files = Sets.newHashSet(files);
	}

	public boolean contains(Path file) {
		return files.contains(file);
	}
	
	@Override
	public String toString() {
		return sortedCopy(transform(files, TO_STRING)).toString();
	}

	public boolean isKnownToBeEmpty() {
		return files.isEmpty();
	}
	   
    public FileSet resolve(FileSet allFiles) {
        Collection<Path> validPathes = newArrayListWithCapacity(files.size());
        for (Path file : files)
            if (allFiles.contains(file))
                validPathes.add(file);
        return new FileSet(validPathes);
    }

}
