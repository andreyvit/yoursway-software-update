package com.yoursway.autoupdater.installer;

import static com.yoursway.autoupdater.installer.external.InstallerCommunication.OK;
import static com.yoursway.autoupdater.installer.external.InstallerCommunication.READY;
import static com.yoursway.autoupdater.installer.external.InstallerCommunication.STOPPING;

import java.io.FileInputStream;
import java.io.InputStream;

import com.yoursway.autoupdater.installer.gui.InstallerView;
import com.yoursway.autoupdater.protos.InstallationProtos.InstallationMemento;

public class InstallerThread extends Thread {
    
    private final InstallerView view;
    
    public InstallerThread(InstallerView view) {
        super(InstallerThread.class.getSimpleName());
        setDaemon(false);
        
        if (view == null)
            throw new NullPointerException("view is null");
        this.view = view;
    }
    
    @Override
    public void run() {
        InstallerServer server = null;
        try {
            view.debug("Initializing communication with the application");
            server = new InstallerServer();
            
            view.debug("Restoring installation from file");
            InputStream input = new FileInputStream("installation");
            InstallationMemento memento = InstallationMemento.parseFrom(input);
            input.close();
            Installation installation = Installation.fromMemento(memento, view);
            
            view.debug("Stopping the application");
            server.send(READY);
            server.receive(STOPPING);
            server.waitDisconnect();
            
            view.debug("Starting installation");
            installation.perform();
            
            view.debug("Restarting the application");
            installation.startVersionExecutable();
            
            view.debug("Checking application state");
            server.reconnect();
            server.send(OK);
            server.receive(OK);
            
        } catch (Throwable e) {
            view.error(e);
        } finally {
            try {
                server.close();
            } catch (Exception e) {
                view.error(e);
            }
            
            view.debug("Termination");
            view.done();
            interrupt();
        }
    }
    
}
