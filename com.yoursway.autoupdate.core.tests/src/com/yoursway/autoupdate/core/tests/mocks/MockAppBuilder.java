package com.yoursway.autoupdate.core.tests.mocks;

import static com.google.common.collect.Lists.newArrayList;
import static com.google.common.collect.Maps.newHashMap;
import static com.yoursway.autoupdate.core.tests.mocks.MockFileFlags.MAIN_UPDATER_JAR;
import static com.yoursway.autoupdate.core.tests.mocks.MockFileFlags.UPDATER;
import static java.util.EnumSet.noneOf;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import com.yoursway.autoupdate.core.FileContainer;
import com.yoursway.autoupdate.core.UpdaterConfiguration;
import com.yoursway.autoupdate.core.filespec.ConcreteFilesSpec;
import com.yoursway.autoupdate.core.path.Path;
import com.yoursway.autoupdate.core.path.Pathes;
import com.yoursway.autoupdate.core.versiondef.RemoteFile;

public class MockAppBuilder {
    
    private Map<Path, RemoteFile> files = newHashMap();
    
    private Map<Path, Set<MockFileFlags>> flags = newHashMap();
    
    private Map<Path, Integer> versions = newHashMap();
    
    public MockAppBuilder file(String path) {
        return file(path, noneOf(MockFileFlags.class));
    }
    
    public MockAppBuilder file(String path, Set<MockFileFlags> flags) {
        Path p = Pathes.relativePath(path);
        if (files.containsKey(p))
            throw new IllegalArgumentException("File with path " + path + " already exists.");
        putNewFileAt(p, flags);
        return this;
    }
    
    public MockAppBuilder update(String path) {
        return update(path, null);
    }
    
    public MockAppBuilder update(String path, Set<MockFileFlags> flags) {
        Path p = Pathes.relativePath(path);
        if (!files.containsKey(p))
            throw new IllegalArgumentException("File with path " + path + " does not exist.");
        if (flags == null)
            flags = this.flags.get(p);
        putNewFileAt(p, flags);
        return this;
    }
    
    public MockAppBuilder delete(String path) {
        Path p = Pathes.relativePath(path);
        if (!files.containsKey(p))
            throw new IllegalArgumentException("File with path " + path + " does not exist.");
        files.remove(p);
        return this;
    }
    
    public FileContainer container() {
        return new MockFileContainer(files.values());
    }
    
    public Collection<RemoteFile> remoteFiles() {
        return newArrayList(files.values());
    }
    
    private void putNewFileAt(Path p, Set<MockFileFlags> flags) {
        files.put(p, newRemoteFile(p));
        this.flags.put(p, flags);
    }
    
    private RemoteFile newRemoteFile(Path p) {
        String hash = fakeMd5(p, newVersion(p));
        try {
            return new RemoteFile(p, hash, new URL("http://example.com/" + p + "#" + hash));
        } catch (MalformedURLException e) {
            throw new AssertionError(e);
        }
    }
    
    private int newVersion(Path p) {
        Integer version = versions.get(p);
        if (version == null)
            version = 1;
        else
            version += 1;
        versions.put(p, version);
        return version;
    }
    
    private static String fakeMd5(Path path, int version) {
        return "v" + version;
    }
    
    public UpdaterConfiguration updaterFiles() {
        return new UpdaterConfiguration(new ConcreteFilesSpec(collectUpdaterFiles()), findMainUpdaterJar());
    }

    private Collection<Path> collectUpdaterFiles() {
        Collection<Path> pathes = newArrayList();
        for (Entry<Path, Set<MockFileFlags>> entry : flags.entrySet())
            if (entry.getValue().contains(UPDATER))
                pathes.add(entry.getKey());
        return pathes;
    }
    
    private Path findMainUpdaterJar() {
        for (Entry<Path, Set<MockFileFlags>> entry : flags.entrySet())
            if (entry.getValue().contains(MAIN_UPDATER_JAR))
                return entry.getKey();
        throw new AssertionError("Main updater JAR not found");
    }
    
}
