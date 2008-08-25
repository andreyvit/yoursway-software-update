package com.yoursway.autoupdater.installer;

import static com.google.common.collect.Maps.newHashMap;
import static com.yoursway.autoupdater.installer.external.InstallerCommunication.OK;
import static com.yoursway.utils.YsFileUtils.saveToFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import com.yoursway.autoupdater.auxiliary.Component;
import com.yoursway.autoupdater.auxiliary.ComponentFile;
import com.yoursway.autoupdater.auxiliary.ProductVersion;
import com.yoursway.autoupdater.installer.external.InstallerCommunication;
import com.yoursway.autoupdater.protos.ExternalInstallerProtos.FileMemento;
import com.yoursway.autoupdater.protos.ExternalInstallerProtos.PackMemento;
import com.yoursway.autoupdater.protos.ExternalInstallerProtos.PacksMemento;
import com.yoursway.autoupdater.protos.LocalRepositoryProtos.ProductVersionMemento;

public class InstallerMain {
    
    static final String READY = "READY";
    static final String STOPPING = "STOPPING";
    
    private static Map<String, File> packs;
    private static File target;
    
    public static void main(String[] args) {
        InstallerCommunication server = null;
        try {
            server = new InstallerServer();
            
            ProductVersion current = versionFromFile("current");
            ProductVersion version = versionFromFile("version");
            packs = packsFromFile("packs");
            target = fileFromMementoFile("target");
            
            server.send(READY);
            server.receive(STOPPING);
            
            for (Component component : version.components())
                for (ComponentFile file : component.files())
                    setupFile(file, component.packs());
            
            version.execute();
            server.send(OK);
            server.receive(OK);
            
        } catch (Throwable e) {
            e.printStackTrace();
        } finally {
            try {
                server.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
    
    private static ProductVersion versionFromFile(String filename) throws IOException {
        InputStream input = new FileInputStream(filename);
        ProductVersionMemento memento = ProductVersionMemento.parseFrom(input);
        return ProductVersion.fromMemento(memento);
    }
    
    private static Map<String, File> packsFromFile(String filename) throws IOException {
        FileInputStream input = new FileInputStream(filename);
        PacksMemento memento = PacksMemento.parseFrom(input);
        
        Map<String, File> packs = newHashMap();
        for (PackMemento m : memento.getPackList())
            packs.put(m.getHash(), new File(m.getPath()));
        return packs;
    }
    
    private static File fileFromMementoFile(String filename) throws IOException {
        FileInputStream input = new FileInputStream(filename);
        FileMemento memento = FileMemento.parseFrom(input);
        
        return new File(memento.getPath());
    }
    
    private static void setupFile(ComponentFile file, Iterable<String> collection) throws IOException {
        System.out.println("setupFile" + file.hash());
        
        ZipFile pack = null;
        ZipEntry entry = null;
        
        for (String packHash : collection) {
            pack = new ZipFile(packs.get(packHash));
            entry = pack.getEntry(file.hash());
            if (entry != null)
                break;
        }
        
        if (entry == null)
            throw new FileNotFoundException(); //?
            
        InputStream in = pack.getInputStream(entry);
        saveToFile(in, new File(target, file.path()));
    }
}
