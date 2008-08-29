package com.yoursway.autoupdater.installer;

import static com.yoursway.autoupdater.installer.external.InstallerCommunication.OK;
import static com.yoursway.autoupdater.installer.external.InstallerCommunication.READY;
import static com.yoursway.autoupdater.installer.external.InstallerCommunication.STOPPING;

import java.io.FileInputStream;
import java.io.InputStream;

import com.yoursway.autoupdater.installer.gui.InstallerView;
import com.yoursway.autoupdater.installer.log.InstallerLog;
import com.yoursway.autoupdater.protos.InstallationProtos.InstallationMemento;

public class InstallerThread extends Thread {
    
    private final InstallerView view;
    private final InstallerLog log;
    
    public InstallerThread(InstallerView view, InstallerLog log) {
        super(InstallerThread.class.getSimpleName());
        setDaemon(false);
        
        if (view == null)
            throw new NullPointerException("view is null");
        if (log == null)
            throw new NullPointerException("log is null");
        this.view = view;
        this.log = log;
    }
    
    @Override
    public void run() {
        InstallerServer server = null;
        try {
            log.debug("Initializing communication with the application");
            server = new InstallerServer();
            
            log.debug("Restoring installation from file");
            InputStream input = new FileInputStream("installation");
            InstallationMemento memento = InstallationMemento.parseFrom(input);
            input.close();
            Installation installation = Installation.fromMemento(memento, log);
            
            log.debug("Stopping the application");
            server.send(READY);
            server.receive(STOPPING);
            server.waitDisconnect();
            
            log.debug("Starting installation");
            installation.perform();
            
            log.debug("Restarting the application");
            installation.startVersionExecutable();
            
            log.debug("Checking application state");
            server.reconnect();
            server.send(OK);
            server.receive(OK);
            
        } catch (Throwable e) {
            log.error(e);
        } finally {
            try {
                server.close();
            } catch (Exception e) {
                log.error(e);
            }
            
            log.debug("Termination");
            view.done();
            interrupt();
        }
    }
    
}
