package com.yoursway.autoupdater.installer.external;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.ServerSocket;
import java.net.Socket;

class InstallerServer extends InstallerCommunication {
    
    private static final int ACCEPT_TIMEOUT = 30000;
    
    private final ServerSocket server;
    private BufferedReader reader;
    private OutputStreamWriter writer;
    private Socket socket;
    
    public InstallerServer() throws IOException {
        server = new ServerSocket(0);
        server.setSoTimeout(ACCEPT_TIMEOUT);
    }
    
    @Override
    public int port() {
        return server.getLocalPort();
    }
    
    @Override
    public void close() throws CommunicationException {
        //super.close();
        try {
            server.close();
        } catch (IOException e) {
            throw new CommunicationException(e);
        }
    }
    
    @Override
    protected BufferedReader reader() throws CommunicationException {
        if (reader == null)
            accept();
        return reader;
    }
    
    @Override
    protected OutputStreamWriter writer() throws CommunicationException {
        if (writer == null)
            accept();
        return writer;
    }
    
    private void accept() throws CommunicationException {
        try {
            socket = server.accept();
            writer = new OutputStreamWriter(socket.getOutputStream());
            reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        } catch (IOException e) {
            throw new CommunicationException(e);
        }
    }
    
    public void reconnect() throws CommunicationException {
        try {
            socket.close();
        } catch (IOException e) {
            throw new CommunicationException(e);
        }
        accept();
    }
    
}
