package com.yoursway.autoupdate.core.fileset;

import static com.google.common.collect.Sets.newHashSet;

import java.util.Collection;
import java.util.Set;

import com.yoursway.autoupdate.core.path.Path;

public class FileSet {
    
    private final Set<Path> files;

    public FileSet(Collection<Path> files) {
        this.files = newHashSet(files);
    }
    
    public boolean contains(Path path) {
        return files.contains(path);
    }

    public Collection<Path> asCollection() {
        return files;
    }
    
}
