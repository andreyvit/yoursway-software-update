package com.yoursway.autoupdater.core.installer.external;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.ConnectException;
import java.net.Socket;

class InstallerClient extends InstallerCommunication {
    
    private final int port;
    
    private static final int CONNECTION_ATTEMPTS = 5;
    private static final int ATTEMPTS_INTERVAL = 1000;
    
    private Socket socket;
    private BufferedReader reader;
    private OutputStreamWriter writer;
    
    public InstallerClient(int port) {
        this.port = port;
    }
    
    @Override
    protected BufferedReader reader() throws CommunicationException {
        if (reader == null)
            connect_();
        return reader;
    }
    
    @Override
    protected OutputStreamWriter writer() throws CommunicationException {
        if (writer == null)
            connect_();
        return writer;
    }
    
    private void connect_() throws CommunicationException {
        try {
            int i = CONNECTION_ATTEMPTS;
            while (socket == null) {
                try {
                    socket = new Socket("localhost", port);
                } catch (ConnectException e) {
                    i--;
                    if (i <= 0)
                        throw e;
                    
                    try {
                        Thread.sleep(ATTEMPTS_INTERVAL);
                    } catch (InterruptedException e1) {
                        i = 0;
                    }
                }
            }
            reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            writer = new OutputStreamWriter(socket.getOutputStream());
        } catch (IOException e) {
            throw new CommunicationException(e);
        }
    }
    
    @Override
    public void close() throws CommunicationException {
        super.close();
        try {
            socket.close();
        } catch (IOException e) {
            throw new CommunicationException(e);
        }
        socket = null;
    }
    
    public void reconnect() throws CommunicationException {
        close();
        connect_();
    }
    
    @Override
    public int port() {
        return port;
    }
    
}
