package com.yoursway.autoupdater.installer.external;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.Arrays;

public abstract class InstallerCommunication {
    
    public static final String READY = "READY";
    public static final String STOPPING = "STOPPING";
    public static final String OK = "OK";
    public static final String INSTALL_FAILED = "FAILED";
    public static final String CRASHED = "CRASHED";
    
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
        String message = receive();
        if (message == null || !message.equals(expected))
            throw new UnexpectedMessageException();
    }
    
    private String receive() throws IOException {
        return reader().readLine();
    }
    
    public void close() throws IOException {
        writer().close();
        reader().close();
    }
    
    public String receiveOneOf(String... cases) throws IOException, UnexpectedMessageException {
        String received = receive();
        boolean expected = Arrays.asList(cases).contains(received);
        if (!expected)
            throw new UnexpectedMessageException();
        return received;
    }
    
}