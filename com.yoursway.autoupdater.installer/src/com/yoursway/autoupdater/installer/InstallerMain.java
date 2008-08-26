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
import com.yoursway.autoupdater.filelibrary.Request;
import com.yoursway.autoupdater.protos.ExternalInstallerProtos.FileMemento;
import com.yoursway.autoupdater.protos.ExternalInstallerProtos.PackMemento;
import com.yoursway.autoupdater.protos.ExternalInstallerProtos.PacksMemento;
import com.yoursway.autoupdater.protos.LocalRepositoryProtos.ProductVersionMemento;
import com.yoursway.utils.log.Log;
import com.yoursway.utils.log.TcpIpLogger;

public class InstallerMain {
    
    static final String READY = "READY";
    static final String STOPPING = "STOPPING";
    
    private static Map<String, File> packs;
    private static File target;
    
    public static void main(String[] args) {
        Log.setLogger(new TcpIpLogger());
        
        InstallerServer server = null;
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
            
            start(version.executable());
            
            Log.write("Reconnecting");
            server.reconnect();
            Log.write("Sending OK");
            server.send(OK);
            Log.write("Receiving OK");
            server.receive(OK);
            Log.write("Closing");
            
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
    
    private static void start(String executable) throws IOException {
        Log.write("Starting " + executable);
        
        ProcessBuilder pb = new ProcessBuilder();
        pb.directory(target);
        
        //> target + executable = absolute
        
        final String JAR = ".jar";
        final String CLASS = " class";
        
        if (executable.endsWith(JAR) || executable.endsWith(CLASS)) {
            String javaHome = System.getProperty("java.home");
            String java = new File(javaHome, "bin/java").getAbsolutePath(); //! check "bin/java" at windows
            
            if (executable.endsWith(JAR))
                pb.command(java, "-jar", executable);
            else {
                String e = executable.substring(0, executable.length() - CLASS.length());
                int space = e.lastIndexOf(' ');
                String classpath = e.substring(0, space);
                String classname = e.substring(space + 1);
                Log.write(java + " -classpath " + classpath + " " + classname);
                pb.command(java, "-classpath", classpath, classname);
            }
        } else
            pb.command(executable);
        
        pb.start();
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
    
    private static void setupFile(ComponentFile file, Iterable<Request> packs) throws IOException {
        System.out.println("setupFile" + file.hash());
        
        ZipFile pack = null;
        ZipEntry entry = null;
        
        for (Request request : packs) {
            String packHash = request.hash();
            pack = new ZipFile(InstallerMain.packs.get(packHash));
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
}
