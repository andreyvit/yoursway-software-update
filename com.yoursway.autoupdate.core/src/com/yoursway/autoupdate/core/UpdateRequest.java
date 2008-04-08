package com.yoursway.autoupdate.core;

import static com.google.common.collect.Lists.newArrayList;

import java.io.File;
import java.util.Collection;
import java.util.List;

import org.eclipse.core.runtime.Assert;

import com.yoursway.autoupdate.core.actions.Action;
import com.yoursway.autoupdate.core.actions.EclipseStartInfo;
import com.yoursway.autoupdate.core.actions.Executor;
import com.yoursway.autoupdate.core.plan.dirs.Directory;
import com.yoursway.autoupdate.core.plan.dirs.DirectoryResolver;
import com.yoursway.autoupdate.core.versions.definitions.RemoteFile;
import com.yoursway.autoupdate.core.versions.definitions.UpdaterInfo;
import com.yoursway.utils.fileset.FileSet;
import com.yoursway.utils.filespec.FileSetSpec;
import com.yoursway.utils.relativepath.RelativePath;

public class UpdateRequest implements DirectoryResolver {
    
	public File appRoot;
	public Collection<FileAction> actions;
    private final FileSet allExistingFiles;
    private final Executor executor;
    private final UpdaterInfo updaterInfo;

	public UpdateRequest(File appRoot,
	        FileSet allExistingFiles,
			Collection<FileAction> actions, UpdaterInfo updaterInfo, Executor executor) {
        Assert.isNotNull(appRoot);
	    Assert.isNotNull(allExistingFiles);
	    Assert.isNotNull(actions);
	    Assert.isNotNull(executor);
	    Assert.isNotNull(updaterInfo);
		this.appRoot = appRoot;
        this.allExistingFiles = allExistingFiles;
		this.actions = actions;
		this.executor = executor;
		this.updaterInfo = updaterInfo;
	}
	
	public Collection<File> resolve(FileSetSpec spec) {
	    FileSet set = spec.resolve(allExistingFiles);
	    List<File> result = newArrayList();
	    for (RelativePath relativePath : set.asCollection())
	        result.add(relativePath.toFile(appRoot));
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
        return updaterInfo.mainJar().toFile(root);
    }

    public Collection<Action> resolveUpdate(FileSetSpec files) {
        Collection<Action> result = newArrayList();
        for (FileAction action : actions)
            if (files.contains(action.file())) {
                RemoteFile replacement = action.replacement();
                File localReplacement = null;
                if (replacement != null)
                    localReplacement = executor.download(replacement.source(), replacement.relativePath());
                result.add(action.createReal(appRoot, localReplacement));
            }
        return result;
    }

    public Collection<FilePair> resolvePairs(FileSetSpec spec, File t) {
        FileSet set = spec.resolve(allExistingFiles);
        List<FilePair> result = newArrayList();
        for (RelativePath relativePath : set.asCollection())
            result.add(new FilePair(relativePath.toFile(appRoot), relativePath.toFile(t)));
        return result;
    }
	
}
