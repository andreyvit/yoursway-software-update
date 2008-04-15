package com.yoursway.autoupdate.core.tests;

import static com.google.common.collect.Lists.newArrayList;
import static com.google.common.collect.Maps.newHashMap;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.Map;

import com.yoursway.autoupdate.core.tests.internal.SimpleHttpServer;
import com.yoursway.autoupdate.core.tests.internal.SimpleServlet;
import com.yoursway.utils.StringInputStream;

public class WebServer {
    
    private final static int PORT = 8744;
    private SimpleHttpServer server;
    
    private Map<String, String> mountedStrings = newHashMap();
    
    private Map<String, byte[]> mountedBytes = newHashMap();
    
    private Collection<String> notFoundPaths = newArrayList();
 
    public WebServer() {
       SimpleServlet servlet = new SimpleServlet() {
            
            public void log(String s2) {
                System.out.println(s2);
            }
            
            public InputStream openFile(String path) throws IOException {
                String value = mountedStrings.get(path);
                if (value != null)
                    return new StringInputStream(value);
                byte [] bytes = mountedBytes.get(path);
                if (bytes != null)
                    return new ByteArrayInputStream(bytes);
                notFoundPaths.add(path);
                throw new IOException("404, blya: " + path);
            }
            
        };
        server = new SimpleHttpServer(PORT, servlet);
     }
    
    @SuppressWarnings("deprecation")
    public void dispose() {
        server.stop();
    }

    public int getPort() {
        return PORT;
    }

    public void mount(String path, String value) {
        mountedStrings.put(path, value);
    }

    public void mount(String path, byte[] value) {
        mountedBytes.put(path, value);
    }
    
    public boolean requestDetected(String subpath) {
        for (String path : notFoundPaths)
            if (path.contains(subpath))
                return true;
        return false;
    }
    
}
