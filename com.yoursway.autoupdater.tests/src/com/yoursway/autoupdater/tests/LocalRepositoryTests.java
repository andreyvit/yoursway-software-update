package com.yoursway.autoupdater.tests;

import static com.yoursway.autoupdater.filelibrary.RequestUtils.requests;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.Iterator;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.yoursway.autoupdater.auxiliary.Product;
import com.yoursway.autoupdater.auxiliary.ProductVersion;
import com.yoursway.autoupdater.filelibrary.Request;
import com.yoursway.autoupdater.filelibrary.RequestUtils;
import com.yoursway.autoupdater.installer.Installer;
import com.yoursway.autoupdater.localrepository.LocalRepository;
import com.yoursway.autoupdater.tests.internal.server.WebServer;

public class LocalRepositoryTests {
    
    private boolean installed;
    private WebServer server;
    
    @Before
    public void setup() {
        installed = false;
        server = new WebServer();
    }
    
    @Test
    public void integration_simple() throws IOException, InterruptedException {
        final int first = 5;
        final int last = 12;
        
        Collection<Request> requests = requests(first, last);
        RequestUtils.mount(server, requests);
        
        Product product = new Product();
        ProductVersion version = new ProductVersion(product, requests);
        
        LocalRepository repo = new LocalRepository(new Installer() {
            public void install(Collection<File> localPacks) {
                System.out.println("Installation started!");
                
                Iterator<File> it = localPacks.iterator();
                for (int i = first; i <= last; i++) {
                    File file = it.next();
                    assertEquals(RequestUtils.sizeOf(i), file.length());
                    System.out.println(file.getPath() + " - size: " + file.length());
                }
                
                System.out.println("Installation finished!");
                installed = true;
                
                synchronized (LocalRepositoryTests.this) {
                    LocalRepositoryTests.this.notify();
                }
            }
        });
        
        synchronized (this) {
            repo.startUpdating(version);
            wait();
        }
        
        assertTrue("The product version has not been installed.", installed);
    }
    
    @After
    public void clean() {
        server.dispose();
    }
    
}
