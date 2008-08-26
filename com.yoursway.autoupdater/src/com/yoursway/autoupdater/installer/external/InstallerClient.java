package com.yoursway.autoupdater.installer.external;

import static com.yoursway.autoupdater.installer.external.ExternalInstaller.PORT;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.ConnectException;
import java.net.Socket;
import java.net.UnknownHostException;

public class InstallerClient extends InstallerCommunication {
    
    private static final int CONNECTION_ATTEMPTS = 5;
    private static final int ATTEMPTS_INTERVAL = 1000;
    
    private Socket socket;
    private BufferedReader reader;
    private OutputStreamWriter writer;
    
    @Override
    protected BufferedReader reader() throws IOException {
        if (reader == null)
            connect();
        return reader;
    }
    
    @Override
    protected OutputStreamWriter writer() throws IOException {
        if (writer == null)
            connect();
        return writer;
    }
    
    private void connect() throws UnknownHostException, IOException {
        int i = CONNECTION_ATTEMPTS;
        while (socket == null) {
            try {
                socket = new Socket("localhost", PORT);
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
    }
    
    @Override
    public void close() throws IOException {
        super.close();
        socket.close();
        socket = null;
    }
    
    public void reconnect() throws IOException {
        close();
        connect();
    }
    
}
