package com.yoursway.autoupdater.tests;

import static com.yoursway.autoupdater.filelibrary.RequestUtils.requests;

import java.io.IOException;
import java.util.Collection;

import junit.framework.Assert;

import org.junit.Test;

import com.yoursway.autoupdater.auxiliary.Product;
import com.yoursway.autoupdater.auxiliary.ProductVersion;
import com.yoursway.autoupdater.filelibrary.Request;
import com.yoursway.autoupdater.filelibrary.RequestUtils;
import com.yoursway.autoupdater.localrepository.LocalRepository;
import com.yoursway.autoupdater.tests.internal.server.WebServer;

public class LocalRepositoryTests {
    
    @Test
    public void integration() throws IOException, InterruptedException {
        WebServer server = new WebServer();
        
        Collection<Request> requests = requests(5, 12);
        RequestUtils.mount(server, requests);
        
        Product product = new Product();
        ProductVersion version = new ProductVersion(product, requests);
        
        LocalRepository repo = new LocalRepository();
        repo.startUpdating(version);
        
        Thread.sleep(5000);
        
        Assert.fail("Test has not yet been completed.");
    }
    
}
