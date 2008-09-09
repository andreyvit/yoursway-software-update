package com.yoursway.autoupdater.installer;

import static com.google.common.collect.Maps.newHashMap;
import static com.google.common.collect.Sets.newHashSet;
import static com.yoursway.utils.YsFileUtils.saveToFile;
import static java.lang.Runtime.getRuntime;

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
import com.yoursway.autoupdater.localrepository.internal.LocalProduct;
import com.yoursway.autoupdater.localrepository.internal.LocalProductVersion;
import com.yoursway.autoupdater.protos.InstallationProtos.InstallationMemento;
import com.yoursway.autoupdater.protos.InstallationProtos.PackMemento;
import com.yoursway.autoupdater.protos.InstallationProtos.InstallationMemento.Builder;

public class Installation {
    
    private final ProductVersionDefinition currentVD;
    private final ProductVersionDefinition newVD;
    final Map<String, File> packs;
    private final File target;
    private final String executablePath;
    
    Installation(ProductVersionDefinition currentVD, ProductVersionDefinition newVD, Map<String, File> packs,
            File target, String executablePath) {
        if (currentVD == null)
            throw new NullPointerException("current is null");
        if (newVD == null)
            throw new NullPointerException("version is null");
        if (packs == null)
            throw new NullPointerException("packs is null");
        if (target == null)
            throw new NullPointerException("target is null");
        if (executablePath == null)
            throw new NullPointerException("executablePath is null");
        
        this.currentVD = currentVD;
        this.newVD = newVD;
        this.packs = packs;
        this.target = target;
        this.executablePath = executablePath;
    }
    
    public Installation(LocalProductVersion version, Map<String, File> packs) throws InstallerException {
        LocalProduct product = version.product();
        
        currentVD = product.currentVersion();
        newVD = version.definition();
        
        if (!currentVD.product().equals(newVD.product()))
            throw new AssertionError("Must not update one product to another.");
        
        try {
            target = product.rootFolder();
        } catch (IOException e) {
            throw new InstallerException("Cannot get application root folder", e);
        }
        
        this.packs = packs;
        
        executablePath = product.executablePath();
    }
    
    public void perform(InstallerLog log) throws IOException {
        Set<String> newVersionFilePaths = newHashSet();
        
        for (ComponentDefinition component : newVD.components()) {
            if (component.isInstaller())
                continue;
            
            for (ComponentFile file : component.files()) {
                newVersionFilePaths.add(file.path());
                setupFile(file, component.packs(), log, target);
            }
        }
        
        for (ComponentFile file : currentVD.files()) {
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
    
    private void setupFile(ComponentFile file, Iterable<Request> packs, InstallerLog log, File target)
            throws IOException {
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
        
        if (file.hasExecAttribute() || file.path().equals(executablePath)) {
            String command = "chmod +x " + targetFile.getCanonicalPath();
            log.debug(command);
            Process process = getRuntime().exec(command);
            try {
                process.waitFor();
            } catch (InterruptedException e) {
                e.printStackTrace(); //!
            }
            log.debug("exit value: " + process.exitValue());
        }
    }
    
    public InstallationMemento toMemento() {
        Builder b = InstallationMemento.newBuilder().setCurrent(currentVD.toMemento()).setVersion(
                newVD.toMemento()).setTarget(target.getAbsolutePath()).setExecutable(executablePath);
        for (Map.Entry<String, File> entry : packs.entrySet()) {
            String hash = entry.getKey();
            File file = entry.getValue();
            b.addPack(PackMemento.newBuilder().setHash(hash).setPath(file.getPath()));
        }
        return b.build();
    }
    
    public static Installation fromMemento(InstallationMemento memento) throws MalformedURLException {
        ProductVersionDefinition current = ProductVersionDefinition.fromMemento(memento.getCurrent());
        ProductVersionDefinition version = ProductVersionDefinition.fromMemento(memento.getVersion());
        Map<String, File> packs = newHashMap();
        for (PackMemento m : memento.getPackList())
            packs.put(m.getHash(), new File(m.getPath()));
        File target = new File(memento.getTarget());
        return new Installation(current, version, packs, target, memento.getExecutable());
    }
    
    public void startVersionExecutable(InstallerLog log) throws Exception {
        //File executable = new File(target, newVD.executable().path());
        File executable = new File(target, executablePath);
        
        ProcessBuilder pb = new ProcessBuilder();
        pb.directory(target);
        
        if (executable.getName().endsWith(".jar")) {
            String javaHome = System.getProperty("java.home");
            File java = new File(javaHome, "bin/java"); //! check "bin/java" at windows
            
            pb.command(java.getCanonicalPath(), "-jar", executable.getCanonicalPath());
        } else {
            log.debug(target.toString());
            log.debug(executable.getCanonicalPath());
            
            pb.command(executable.getCanonicalPath());
        }
        
        pb.start();
    }
    
    public ComponentDefinition getInstallerComponent() throws Exception {
        return newVD.installer();
    }
    
    public void setupExternalInstaller(File dir) throws Exception {
        ComponentDefinition externalInstaller = newVD.installer();
        
        for (ComponentFile file : externalInstaller.files())
            setupFile(file, externalInstaller.packs(), InstallerLog.NOP, dir);
    }
    
}
