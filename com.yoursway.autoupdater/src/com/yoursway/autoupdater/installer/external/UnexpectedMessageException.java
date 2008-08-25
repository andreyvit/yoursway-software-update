package com.yoursway.autoupdater.installer.external;

public class UnexpectedMessageException extends Exception {
    
    public UnexpectedMessageException() {
        super("Unexpected message received");
    }
    
}
