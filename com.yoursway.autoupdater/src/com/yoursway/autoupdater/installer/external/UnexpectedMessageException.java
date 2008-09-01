package com.yoursway.autoupdater.installer.external;

public class UnexpectedMessageException extends Exception {
    private static final long serialVersionUID = -7834327908763298520L;
    
    public UnexpectedMessageException() {
        super("Unexpected message received");
    }
    
}
