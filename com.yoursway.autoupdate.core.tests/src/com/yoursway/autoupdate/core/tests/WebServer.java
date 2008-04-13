package com.yoursway.autoupdate.core.tests;

import java.io.IOException;
import java.io.InputStream;

import com.yoursway.autoupdate.core.tests.internal.Activator;
import com.yoursway.autoupdate.core.tests.internal.SimpleHttpServer;
import com.yoursway.autoupdate.core.tests.internal.SimpleServlet;

public class WebServer {
    
    private final static int PORT = 8744;
    private SimpleHttpServer server;
 
    public WebServer() {
       SimpleServlet servlet = new SimpleServlet() {
            
            public void log(String s2) {
                System.out.println(s2);
            }
            
            public InputStream openFile(String path) throws IOException {
                return Activator.openResource("tests/integration/" + path);
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
    
}
