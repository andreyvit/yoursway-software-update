package com.yoursway.autoupdater.installer.external;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.OutputStreamWriter;

public abstract class InstallerCommunication {
    
    public static final String READY = "READY";
    public static final String STOPPING = "STOPPING";
    public static final String OK = "OK";
    
    public InstallerCommunication() {
        super();
    }
    
    protected abstract OutputStreamWriter writer() throws IOException;
    
    protected abstract BufferedReader reader() throws IOException;
    
    public void send(String message) throws IOException {
        writer().write(message + "\n");
        writer().flush();
    }
    
    public void receive(String expected) throws UnexpectedMessageException, IOException {
        String message = reader().readLine();
        if (message == null || !message.equals(expected))
            throw new UnexpectedMessageException();
    }
    
    public void close() throws IOException {
        writer().close();
        reader().close();
    }
    
}