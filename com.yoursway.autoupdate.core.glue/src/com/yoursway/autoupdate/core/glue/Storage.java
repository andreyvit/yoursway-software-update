package com.yoursway.autoupdate.core.glue;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public interface Storage {
    
    InputStream openRead() throws IOException;
    
    OutputStream openWrite() throws IOException;

    void trash() throws IOException;
    
}
