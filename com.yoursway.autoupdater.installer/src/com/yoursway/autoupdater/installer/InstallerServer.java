package com.yoursway.autoupdater.installer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.ServerSocket;
import java.net.Socket;

public class InstallerServer {
    
    static final int PORT = 32123;
    
    private OutputStreamWriter writer = null;
    private BufferedReader reader = null;
    
    private final ServerSocket server;
    
    public InstallerServer() throws IOException {
        server = new ServerSocket(PORT);
    }
    
    private void acceptIfNotYet() throws IOException {
        if (writer != null)
            return;
        
        Socket socket = server.accept();
        writer = new OutputStreamWriter(socket.getOutputStream());
        reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
    }
    
    public void send(String message) throws IOException {
        acceptIfNotYet();
        
        writer.write(message + "\n");
        writer.flush();
    }
    
    public void receive(String expected) throws UnexpectedMessageException, IOException {
        acceptIfNotYet();
        
        String message = reader.readLine();
        if (!message.equals(expected))
            throw new UnexpectedMessageException();
    }
    
    public void close() throws IOException {
        server.close();
        writer.close();
    }
}
