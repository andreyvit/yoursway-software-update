package com.yoursway.autoupdater.installer;

public class UnexpectedMessageException extends Exception {
    
    public UnexpectedMessageException() {
        super("Unexpected message received");
    }
    
}
