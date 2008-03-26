package com.yoursway.autoupdate.core;

import static com.google.common.collect.Lists.newArrayList;

import java.io.File;
import java.util.Collection;
import java.util.List;

import org.eclipse.core.runtime.Assert;

import com.yoursway.autoupdate.core.dirs.Directory;
import com.yoursway.autoupdate.core.dirs.DirectoryResolver;
import com.yoursway.autoupdate.core.fileset.FileSet;
import com.yoursway.autoupdate.core.filespec.FileSetSpec;
import com.yoursway.autoupdate.core.path.Path;
import com.yoursway.autoupdate.core.versiondef.RemoteFile;

public class UpdateRequest implements DirectoryResolver {
    
	public File appRoot;
	public Collection<FileAction> actions;
    private final FileSet allExistingFiles;
    private final Executor executor;
    private final UpdaterConfiguration config;

	public UpdateRequest(File appRoot,
	        FileSet allExistingFiles,
			Collection<FileAction> actions, UpdaterConfiguration config, Executor executor) {
        this.config = config;
        Assert.isNotNull(appRoot);
	    Assert.isNotNull(allExistingFiles);
	    Assert.isNotNull(actions);
	    Assert.isNotNull(executor);
		this.appRoot = appRoot;
        this.allExistingFiles = allExistingFiles;
		this.actions = actions;
		this.executor = executor;
	}
	
	public Collection<File> resolve(FileSetSpec spec) {
	    FileSet set = spec.resolve(allExistingFiles);
	    List<File> result = newArrayList();
	    for (Path path : set.asCollection())
	        result.add(path.toFile(appRoot));
        return result;
	}
	
	public EclipseStartInfo determineCurrentEclipseStartInfo() {
	    return executor.determineCurrentEclipseStartInfo();
	}
	
	public File resolve(Directory directory) {
	    return directory.resolve(this);
	}

    public File resolveApplicationDirectory() {
        return appRoot;
    }

    public File resolveTemporaryDirectory() {
        return executor.createTemporaryDirectory();
    }
    
    public File resolveUpdaterJar(File root) {
        return config.updaterJar().toFile(root);
    }

    public Collection<Action> resolveUpdate(FileSetSpec files) {
        Collection<Action> result = newArrayList();
        for (FileAction action : actions)
            if (files.contains(action.file())) {
                RemoteFile replacement = action.replacement();
                File localReplacement = null;
                if (replacement != null)
                    localReplacement = executor.download(replacement);
                result.add(action.createReal(appRoot, localReplacement));
            }
        return result;
    }

    public Collection<FilePair> resolvePairs(FileSetSpec spec, File t) {
        FileSet set = spec.resolve(allExistingFiles);
        List<FilePair> result = newArrayList();
        for (Path path : set.asCollection())
            result.add(new FilePair(path.toFile(appRoot), path.toFile(t)));
        return result;
    }
	
}
