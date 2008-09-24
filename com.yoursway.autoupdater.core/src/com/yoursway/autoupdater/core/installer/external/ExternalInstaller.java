package com.yoursway.autoupdater.core.installer.external;

import static com.google.common.collect.Lists.newLinkedList;
import static com.yoursway.autoupdater.core.installer.external.InstallerCommunication.CRASHED;
import static com.yoursway.autoupdater.core.installer.external.InstallerCommunication.INSTALL_FAILED;
import static com.yoursway.autoupdater.core.installer.external.InstallerCommunication.OK;
import static com.yoursway.utils.YsFileUtils.createTempFolder;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

import com.yoursway.autoupdater.core.auxiliary.ComponentStopper;
import com.yoursway.autoupdater.core.installer.Installation;
import com.yoursway.autoupdater.core.installer.Installer;
import com.yoursway.autoupdater.core.installer.InstallerException;
import com.yoursway.utils.log.Log;
import com.yoursway.utils.os.YsOSUtils;

public class ExternalInstaller implements Installer {
    
    public static final String EXTINSTALLER_PORT = "EXTINSTALLER_PORT";
    private File folder;
    private boolean prepared;
    
    private final boolean gui;
    
    private static InstallerClient client;
    
    public ExternalInstaller() {
        this(false);
    }
    
    public ExternalInstaller(boolean gui) {
        this.gui = gui;
    }
    
    public void install(Installation installation, ComponentStopper stopper) throws InstallerException {
        try {
            setFolder();
        } catch (IOException e) {
            throw new InstallerException("Cannot set temprary folder for external installer", e);
        }
        
        prepare(installation);
        Log.write("External installer prepared in " + folder + ".");
        
        InstallerCommunication communication = InstallerCommunication.listen();
        start(communication.port());
        
        communication.receive(InstallerCommunication.READY);
        communication.send(InstallerCommunication.STOPPING);
        
        boolean stopped = stopper.stop();
        if (!stopped)
            throw new InstallerException("Cannot stop the application");
        
    }
    
    private void setFolder() throws IOException {
        folder = createTempFolder("com.yoursway.autoupdater.installer", null); //!
        
        if (folder.list().length != 0)
            throw new AssertionError("An external installer folder must be empty.");
    }
    
    private void prepare(Installation installation) throws InstallerException {
        
        try {
            installation.setupExternalInstaller(folder);
        } catch (Exception e) {
            throw new InstallerException("Cannot setup external installer", e);
        }
        
        try {
            OutputStream stream = new BufferedOutputStream(new FileOutputStream(new File(folder,
                    "installation")));
            
            installation.toMemento().writeTo(stream);
            
            stream.close();
        } catch (IOException e) {
            throw new InstallerException("Cannot write data for external installer", e);
        }
        
        prepared = true;
    }
    
    private void start(int port) throws InstallerException {
        if (!prepared)
            throw new IllegalStateException("ExternalInstaller should be prepared before starting");
        
        Log.write("Starting external installer");
        
        List<String> cmd;
        try {
            cmd = newLinkedList();
            cmd.add(YsOSUtils.javaPath());
            cmd.add("-XstartOnFirstThread");
            cmd.add("-cp");
            
            StringBuilder sb = new StringBuilder();
            getJars(sb, folder);
            cmd.add(sb.substring(1));
            
            cmd.add("com.yoursway.autoupdater.installer.InstallerMain");
            cmd.add("gui");
        } catch (Exception e) {
            throw new InstallerException("Cannot create java cmd", e); //!
        }
        Log.write(cmd.toString());
        
        try {
            startProcess(cmd, folder, port);
        } catch (IOException e) {
            throw new InstallerException("Cannot start the external installer", e);
        }
    }
    
    public static void startProcess(List<String> cmd, File dir, int port) throws IOException {
        ProcessBuilder pb = new ProcessBuilder();
        pb.directory(dir);
        pb.command(cmd);
        pb.environment().put(EXTINSTALLER_PORT, Integer.toString(port));
        pb.start();
    }
    
    private void getJars(StringBuilder sb, File folder) throws IOException {
        for (File file : folder.listFiles()) {
            if (file.getName().endsWith(".jar"))
                sb.append(":" + file.getCanonicalPath());
            else if (file.isDirectory())
                getJars(sb, file);
        }
        
    }
    
    public static String afterInstall() throws InstallerException {
        try {
            InstallerCommunication communication = InstallerCommunication.connect();
            
            String result = communication.receiveOneOf(OK, INSTALL_FAILED, CRASHED);
            communication.send(OK);
            
            return result;
        } catch (Throwable e) {
            throw new InstallerException("Cannot communicate with external installer", e);
        }
    }
    
}
