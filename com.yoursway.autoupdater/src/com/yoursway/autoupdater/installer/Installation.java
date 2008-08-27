package com.yoursway.autoupdater.installer;

import static com.google.common.collect.Maps.newHashMap;
import static com.google.common.collect.Sets.newHashSet;
import static com.yoursway.utils.YsFileUtils.saveToFile;
import static com.yoursway.utils.log.LogEntryType.ERROR;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.util.Map;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import com.yoursway.autoupdater.auxiliary.Component;
import com.yoursway.autoupdater.auxiliary.ComponentFile;
import com.yoursway.autoupdater.auxiliary.ProductVersion;
import com.yoursway.autoupdater.filelibrary.Request;
import com.yoursway.autoupdater.protos.InstallationProtos.InstallationMemento;
import com.yoursway.autoupdater.protos.InstallationProtos.PackMemento;
import com.yoursway.autoupdater.protos.InstallationProtos.InstallationMemento.Builder;
import com.yoursway.utils.log.Log;

public class Installation {
    
    private final ProductVersion current;
    private final ProductVersion version;
    private final Map<String, File> packs;
    private final File target;
    
    public Installation(ProductVersion current, ProductVersion version, Map<String, File> packs, File target) {
        
        this.current = current;
        this.version = version;
        this.packs = packs;
        this.target = target;
    }
    
    public void perform() throws IOException {
        Set<String> newVersionFilePaths = newHashSet();
        
        for (Component component : version.components()) {
            for (ComponentFile file : component.files()) {
                newVersionFilePaths.add(file.path());
                setupFile(file, component.packs());
            }
        }
        
        for (ComponentFile file : current.files()) {
            if (!newVersionFilePaths.contains(file.path())) {
                File localFile = new File(target, file.path());
                boolean deleted = localFile.delete();
                if (!deleted)
                    Log.write("Cannot delete file " + localFile, ERROR);
            }
        }
    }
    
    private void setupFile(ComponentFile file, Iterable<Request> packs) throws IOException {
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
    
    public static Installation fromMemento(InstallationMemento memento) throws MalformedURLException {
        ProductVersion current = ProductVersion.fromMemento(memento.getCurrent());
        ProductVersion version = ProductVersion.fromMemento(memento.getVersion());
        Map<String, File> packs = newHashMap();
        for (PackMemento m : memento.getPackList())
            packs.put(m.getHash(), new File(m.getPath()));
        File target = new File(memento.getTarget());
        return new Installation(current, version, packs, target);
    }
    
    public void startVersionExecutable() throws IOException {
        String executable = version.executable();
        if (executable.length() == 0)
            return;
        Log.write("Starting " + executable);
        
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
