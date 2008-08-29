package com.yoursway.autoupdater.installer;

import static com.google.common.collect.Maps.newHashMap;
import static com.google.common.collect.Sets.newHashSet;
import static com.yoursway.utils.YsFileUtils.saveToFile;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.util.Map;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import com.yoursway.autoupdater.auxiliary.ComponentDefinition;
import com.yoursway.autoupdater.auxiliary.ComponentFile;
import com.yoursway.autoupdater.auxiliary.ProductVersionDefinition;
import com.yoursway.autoupdater.filelibrary.Request;
import com.yoursway.autoupdater.installer.log.InstallerLog;
import com.yoursway.autoupdater.protos.InstallationProtos.InstallationMemento;
import com.yoursway.autoupdater.protos.InstallationProtos.PackMemento;
import com.yoursway.autoupdater.protos.InstallationProtos.InstallationMemento.Builder;

public class Installation {
    
    private final ProductVersionDefinition current;
    private final ProductVersionDefinition version;
    private final Map<String, File> packs;
    private final File target;
    
    private final InstallerLog log;
    
    public Installation(ProductVersionDefinition current, ProductVersionDefinition version, Map<String, File> packs, File target,
            InstallerLog log) {
        if (current == null)
            throw new NullPointerException("current is null");
        if (version == null)
            throw new NullPointerException("version is null");
        if (packs == null)
            throw new NullPointerException("packs is null");
        if (target == null)
            throw new NullPointerException("target is null");
        if (log == null)
            throw new NullPointerException("log is null");
        
        this.current = current;
        this.version = version;
        this.packs = packs;
        this.target = target;
        
        this.log = log;
    }
    
    public void perform() throws IOException {
        Set<String> newVersionFilePaths = newHashSet();
        
        for (ComponentDefinition component : version.components()) {
            for (ComponentFile file : component.files()) {
                newVersionFilePaths.add(file.path());
                setupFile(file, component.packs());
            }
        }
        
        for (ComponentFile file : current.files()) {
            String path = file.path();
            if (!newVersionFilePaths.contains(path)) {
                log.debug("Deleting old file " + path);
                File localFile = new File(target, path);
                boolean deleted = localFile.delete();
                if (!deleted)
                    log.error("Cannot delete file " + localFile);
            }
        }
    }
    
    private void setupFile(ComponentFile file, Iterable<Request> packs) throws IOException {
        log.debug("Setting up file " + file.path());
        
        ZipFile pack = null;
        ZipEntry entry = null;
        
        for (Request request : packs) {
            String packHash = request.hash();
            pack = new ZipFile(this.packs.get(packHash));
            entry = pack.getEntry(file.hash());
            if (entry != null)
                break;
        }
        
        if (entry == null)
            throw new FileNotFoundException(); //?
            
        InputStream in = pack.getInputStream(entry);
        File targetFile = new File(target, file.path());
        targetFile.getParentFile().mkdirs();
        saveToFile(in, targetFile);
        
        boolean ok = targetFile.setLastModified(file.modified());
        if (!ok)
            log.error("Cannot set lastmodified property of file " + targetFile);
    }
    
    public InstallationMemento toMemento() {
        Builder b = InstallationMemento.newBuilder().setCurrent(current.toMemento()).setVersion(
                version.toMemento()).setTarget(target.getAbsolutePath());
        for (Map.Entry<String, File> entry : packs.entrySet()) {
            String hash = entry.getKey();
            File file = entry.getValue();
            b.addPack(PackMemento.newBuilder().setHash(hash).setPath(file.getPath()));
        }
        return b.build();
    }
    
    public static Installation fromMemento(InstallationMemento memento, InstallerLog log) throws MalformedURLException {
        ProductVersionDefinition current = ProductVersionDefinition.fromMemento(memento.getCurrent());
        ProductVersionDefinition version = ProductVersionDefinition.fromMemento(memento.getVersion());
        Map<String, File> packs = newHashMap();
        for (PackMemento m : memento.getPackList())
            packs.put(m.getHash(), new File(m.getPath()));
        File target = new File(memento.getTarget());
        return new Installation(current, version, packs, target, log);
    }
    
    public void startVersionExecutable() throws IOException {
        String executable = version.executable();
        if (executable.length() == 0)
            return;
        
        ProcessBuilder pb = new ProcessBuilder();
        pb.directory(target);
        
        if (executable.endsWith(".jar")) {
            String javaHome = System.getProperty("java.home");
            File java = new File(javaHome, "bin/java"); //! check "bin/java" at windows
            
            pb.command(java.getAbsolutePath(), "-jar", executable);
        } else
            pb.command(executable);
        
        pb.start();
    }
    
}
