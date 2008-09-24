package com.yoursway.autoupdater.installer;

import static com.yoursway.autoupdater.core.installer.external.InstallerCommunication.INSTALL_FAILED;
import static com.yoursway.autoupdater.core.installer.external.InstallerCommunication.OK;
import static com.yoursway.autoupdater.core.installer.external.InstallerCommunication.READY;
import static com.yoursway.autoupdater.core.installer.external.InstallerCommunication.STOPPING;

import java.io.FileInputStream;
import java.io.InputStream;

import com.yoursway.autoupdater.core.installer.Installation;
import com.yoursway.autoupdater.core.installer.external.InstallerCommunication;
import com.yoursway.autoupdater.core.installer.log.InstallerLog;
import com.yoursway.autoupdater.core.protos.InstallationProtos.InstallationMemento;
import com.yoursway.autoupdater.installer.gui.InstallerView;

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
        InstallerCommunication communication = null;
        try {
            log.debug("Initializing communication with the application");
            communication = InstallerCommunication.connect();
            
            log.debug("Restoring installation from file");
            InputStream input = new FileInputStream("installation");
            InstallationMemento memento = InstallationMemento.parseFrom(input);
            input.close();
            Installation installation = Installation.fromMemento(memento);
            
            log.debug("Stopping the application");
            communication.send(READY);
            communication.receive(STOPPING);
            communication.waitDisconnect();
            
            log.debug("Starting installation");
            String rollbackReason = INSTALL_FAILED;
            try {
                installation.perform(log);
                rollbackReason = InstallerCommunication.CRASHED;
                
                log.debug("Restarting the application");
                communication = InstallerCommunication.listen();
                installation.startVersionExecutable(log, communication.port());
                
                log.debug("Checking application state");
                communication.send(OK);
                communication.receive(OK); //! terminate app if not receive OK
                
            } catch (Throwable e) {
                log.error(e);
                log.debug("Starting rollback");
                try {
                    installation.rollback();
                    
                    log.debug("Restarting the application");
                    communication = InstallerCommunication.listen();
                    installation.startVersionExecutable(log, communication.port());
                    
                    log.debug("Checking application state");
                    communication.send(rollbackReason);
                    communication.receive(OK);
                    
                } catch (Throwable e1) {
                    //> ROLLBACK_FAILED user message
                    //>     "download new version from site" 
                    throw e1;
                }
            }
            
            // only if installation or rollback done successfully
            installation.deleteBackupFiles();
            
        } catch (Throwable e) {
            log.error(e);
            
        } finally {
            try {
                communication.close();
            } catch (Exception e) {
                log.error(e);
            }
            
            log.debug("Termination");
            view.done();
            interrupt();
        }
    }
}
