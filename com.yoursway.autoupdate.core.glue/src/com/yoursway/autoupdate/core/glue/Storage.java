package com.yoursway.autoupdate.core.glue;

import java.io.InputStream;
import java.io.OutputStream;

public interface Storage {
    
    InputStream openRead();
    
    OutputStream openWrite();
    
}
