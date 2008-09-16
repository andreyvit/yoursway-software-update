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
    
    protected abstract OutputStreamWriter writer() throws CommunicationException;
    
    protected abstract BufferedReader reader() throws CommunicationException;
    
    public abstract int port();
    
    public void send(String message) throws CommunicationException {
        try {
            writer().write(message + "\n");
            writer().flush();
        } catch (IOException e) {
            throw new CommunicationException(e);
        }
    }
    
    public void receive(String expected) throws CommunicationException {
        String message = receive();
        if (message == null || !message.equals(expected))
            throw new UnexpectedMessageException(message);
    }
    
    private String receive() throws CommunicationException {
        try {
            return reader().readLine();
        } catch (IOException e) {
            throw new CommunicationException(e);
        }
    }
    
    public void close() throws CommunicationException {
        try {
            writer().close();
            reader().close();
        } catch (IOException e) {
            throw new CommunicationException(e);
        }
    }
    
    public String receiveOneOf(String... cases) throws CommunicationException {
        String received = receive();
        boolean expected = Arrays.asList(cases).contains(received);
        if (!expected)
            throw new UnexpectedMessageException(received);
        return received;
    }
    
    public void waitDisconnect() throws CommunicationException {
        try {
            String line;
            do
                line = reader().readLine();
            while (line != null);
        } catch (IOException e) {
            throw new CommunicationException(e);
        }
    }
    
    public static InstallerCommunication listen() throws CommunicationException {
        try {
            return new InstallerServer();
        } catch (IOException e) {
            throw new CommunicationException(e);
        }
    }
    
    public static InstallerCommunication connect() throws CommunicationException {
        try {
            String port = System.getenv(ExternalInstaller.EXTINSTALLER_PORT);
            return new InstallerClient(Integer.parseInt(port));
        } catch (Exception e) {
            throw new CommunicationException(e);
        }
        
    }
}