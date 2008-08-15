package com.yoursway.autoupdater.internal.downloader;

import java.io.BufferedOutputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import com.yoursway.autoupdater.filelibrary.RequiredFile;

class DownloadingFile {
    
    private static final int partsize = 512 * 1024;
    
    private final OutputStream out;
    
    private int partOffset = 0;
    private int partCounter = 0;
    
    public DownloadingFile(RequiredFile file, String place) throws FileNotFoundException {
        out = new BufferedOutputStream(new FileOutputStream(place + file.filename()));
    }
    
    public void write(byte[] b, int off, int len) throws IOException, FilePartIsntCorrectException {
        out.write(b, off, len);
        
        partCounter += len;
        
        if (partCounter >= partsize) {
            checkPart(partOffset, partsize);
            partOffset += partsize;
            partCounter -= partsize;
        }
    }
    
    public void close() throws IOException, FilePartIsntCorrectException {
        checkPart(partOffset, partCounter);
        
        out.close();
    }
    
    private void checkPart(int offset, int size) throws FilePartIsntCorrectException {
        throw new FilePartIsntCorrectException();
        //throw new UnsupportedOperationException();
    }
    
}
