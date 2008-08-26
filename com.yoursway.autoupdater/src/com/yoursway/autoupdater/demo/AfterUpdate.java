package com.yoursway.autoupdater.demo;

import static com.yoursway.autoupdater.installer.external.InstallerCommunication.OK;

import com.yoursway.autoupdater.installer.external.ExternalInstaller;
import com.yoursway.utils.log.Log;
import com.yoursway.utils.log.TcpIpLogger;

public class AfterUpdate {
    
    public static void main(String[] args) {
        Log.setLogger(new TcpIpLogger());
        Log.write("AfterUpdate started");
        
        try {
            Log.write("receiving OK");
            ExternalInstaller.client().receive(OK);
            Log.write("sending OK");
            ExternalInstaller.client().send(OK);
            Log.write("closing");
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }
}
