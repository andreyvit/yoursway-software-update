package com.yoursway.autoupdater.installer;

import static com.yoursway.autoupdater.installer.external.InstallerCommunication.OK;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.Map;

import com.yoursway.autoupdater.protos.InstallationProtos.InstallationMemento;
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
            
            InputStream input = new FileInputStream("installation");
            InstallationMemento memento = InstallationMemento.parseFrom(input);
            input.close();
            
            Installation installation = Installation.fromMemento(memento);
            
            server.send(READY);
            server.receive(STOPPING);
            
            installation.perform();
            
            installation.startVersionExecutable();
            
            server.reconnect();
            Log.write("Sending OK");
            server.send(OK);
            Log.write("Receiving OK");
            server.receive(OK);
            Log.write("Closing");
            
        } catch (Throwable e) {
            OutputStream stream = Log.stream();
            e.printStackTrace(new PrintStream(stream));
            try {
                stream.close();
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        } finally {
            try {
                server.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
