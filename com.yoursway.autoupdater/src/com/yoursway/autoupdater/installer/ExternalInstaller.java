package com.yoursway.autoupdater.installer;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Map;

import com.google.protobuf.Message;
import com.yoursway.autoupdater.auxiliary.ProductVersion;
import com.yoursway.autoupdater.protos.ExternalInstallerProtos.FileMemento;
import com.yoursway.autoupdater.protos.ExternalInstallerProtos.PackMemento;
import com.yoursway.autoupdater.protos.ExternalInstallerProtos.PacksMemento;
import com.yoursway.autoupdater.protos.ExternalInstallerProtos.PacksMemento.Builder;
import com.yoursway.utils.YsFileUtils;

class ExternalInstaller {
    
    private final File folder;
    private File installer;
    private boolean prepared;
    
    public ExternalInstaller(File folder) {
        if (folder == null)
            throw new NullPointerException("folder is null");
        this.folder = folder;
        
        System.out.println(folder);
        
        for (String s : folder.list())
            System.out.println(s);
        
        String currentDir = System.getProperty("user.dir");
        installer = new File(currentDir, "../com.yoursway.autoupdater.installer/installer.jar"); //!
        
    }
    
    void prepare(ProductVersion current, ProductVersion version, Map<String, File> packs, File target)
            throws InstallerException {
        
        try {
            File copy = new File(folder, "installer.jar");
            YsFileUtils.fileCopy(installer, copy);
            installer = copy;
        } catch (IOException e) {
            throw new InstallerException("Cannot copy external installer");
        }
        
        try {
            
            write(current.toMemento(), "current");
            write(version.toMemento(), "version");
            write(packsToMemento(packs), "packs");
            write(fileToMemento(target), "target");
        } catch (IOException e) {
            throw new InstallerException("Cannot write data for external installer");
        }
        
        prepared = true;
    }
    
    private FileMemento fileToMemento(File file) {
        return FileMemento.newBuilder().setPath(file.getPath()).build();
    }
    
    private PacksMemento packsToMemento(Map<String, File> packs) {
        Builder b = PacksMemento.newBuilder();
        for (Map.Entry<String, File> entry : packs.entrySet()) {
            String hash = entry.getKey();
            File file = entry.getValue();
            b.addPack(PackMemento.newBuilder().setHash(hash).setPath(file.getPath()));
        }
        return b.build();
    }
    
    private void write(Message message, String filename) throws IOException {
        OutputStream os = new BufferedOutputStream(new FileOutputStream(new File(folder, filename)));
        message.writeTo(os);
        os.close();
    }
    
    void start() throws InstallerException {
        if (!prepared)
            throw new IllegalStateException("ExternalInstaller should be prepared before starting");
        
        String javaHome = System.getProperty("java.home");
        File java = new File(javaHome, "bin/java"); //! check at windows
        
        ProcessBuilder pb = new ProcessBuilder();
        pb.directory(folder);
        pb.command(java.getAbsolutePath(), "-jar", installer.getAbsolutePath());
        try {
            pb.start();
        } catch (IOException e) {
            throw new InstallerException("Cannot start the external installer", e);
        }
        
    }
}
