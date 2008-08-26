package com.yoursway.autoupdater.installer;

import static com.yoursway.autoupdater.installer.external.ExternalInstaller.PORT;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.ServerSocket;
import java.net.Socket;

import com.yoursway.autoupdater.installer.external.InstallerCommunication;
import com.yoursway.utils.log.Log;

public class InstallerServer extends InstallerCommunication {
    
    private final ServerSocket server;
    private BufferedReader reader;
    private OutputStreamWriter writer;
    private Socket socket;
    
    public InstallerServer() throws IOException {
        server = new ServerSocket(PORT);
    }
    
    @Override
    public void close() throws IOException {
        super.close();
        server.close();
    }
    
    @Override
    protected BufferedReader reader() throws IOException {
        if (reader == null)
            accept();
        return reader;
    }
    
    @Override
    protected OutputStreamWriter writer() throws IOException {
        if (writer == null)
            accept();
        return writer;
    }
    
    private void accept() throws IOException {
        socket = server.accept();
        writer = new OutputStreamWriter(socket.getOutputStream());
        reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
    }
    
    public void reconnect() throws IOException {
        Log.write("Reconnecting");
        socket.close();
        accept();
    }
    
}
