package com.yoursway.autoupdater.installer.external;

public class UnexpectedMessageException extends CommunicationException {
    private static final long serialVersionUID = -7834327908763298520L;
    
    public UnexpectedMessageException(String receivedMessage) {
        super("Unexpected message received from external installer: " + receivedMessage);
    }
    
}
