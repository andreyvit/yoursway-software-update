package com.yoursway.autoupdater.localrepository.internal;

import com.yoursway.autoupdater.auxiliary.AutoupdaterException;

public class InvalidDownloadedFilesException extends AutoupdaterException {
    private static final long serialVersionUID = 2325001023416320108L;
    
    public InvalidDownloadedFilesException(Throwable cause) {
        super("Downloaded files are invalid", cause);
    }
    
}
