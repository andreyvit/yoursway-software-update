package com.yoursway.autoupdate.core.tests.internal;

import java.io.IOException;
import java.io.InputStream;

public interface SimpleServlet {

    void log(String s2);

    InputStream openFile(String path) throws IOException;
    
}
