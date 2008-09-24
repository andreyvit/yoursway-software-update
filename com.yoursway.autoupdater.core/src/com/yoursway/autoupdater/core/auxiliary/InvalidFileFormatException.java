package com.yoursway.autoupdater.core.auxiliary;

import java.net.URL;

public class InvalidFileFormatException extends Exception {
    private static final long serialVersionUID = 3667125877544341714L;
    
    public InvalidFileFormatException(URL url) {
        super("Format of the " + url + " file is invalid.");
    }
    
}
