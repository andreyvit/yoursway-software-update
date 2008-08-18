package com.yoursway.autoupdater.tests;

import static com.yoursway.utils.YsFileUtils.readAsString;
import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;
import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.expectLastCall;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;
import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import com.yoursway.autoupdater.filelibrary.downloader.Downloader;
import com.yoursway.autoupdater.filelibrary.downloader.DownloaderImpl;
import com.yoursway.autoupdater.filelibrary.downloader.DownloaderListener;
import com.yoursway.autoupdater.tests.internal.server.WebServer;

public class DownloaderTests {
    
    private static WebServer server;
    
    private boolean completed;
    
    @BeforeClass
    public static void setup() {
        server = new WebServer();
    }
    
    private URL urlFor(String remotePath) throws MalformedURLException {
        return new URL("http://localhost:" + server.getPort() + "/" + remotePath);
    }
    
    private File tempFile() throws IOException {
        return File.createTempFile("autoupdater.test", null);
    }
    
    private String bigString() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 2000; i++)
            sb.append("file contents ");
        return sb.toString();
    }
    
    @Test
    public void connection() throws IOException, InterruptedException {
        String remotePath = "test";
        String text = "Hello world!\nOK\n";
        completed = false;
        
        File file = null;
        try {
            server.mount(remotePath, text);
            
            Downloader downloader = new DownloaderImpl();
            downloader.events().addListener(new DownloaderListener() {
                
                public void someBytesDownloaded(URL url) {
                    // nothing
                }
                
                public void completed(URL url) {
                    synchronized (DownloaderTests.this) {
                        completed = true;
                        DownloaderTests.this.notify();
                    }
                }
            });
            
            URL url = urlFor(remotePath);
            file = tempFile();
            
            synchronized (this) {
                downloader.enqueue(url, file, 0);
                wait(1000);
            }
            
            assertEquals(text, readAsString(file));
            assertTrue(completed);
            
        } finally {
            file.delete();
        }
    }
    
    @Test
    public void cancelling() throws IOException, InterruptedException {
        String remotePath = "test";
        String text = bigString();
        
        File file = null;
        try {
            server.mount(remotePath, text);
            
            Downloader downloader = new DownloaderImpl();
            DownloaderListener listener = createMock(DownloaderListener.class);
            downloader.events().addListener(listener);
            
            final URL url = urlFor(remotePath);
            listener.someBytesDownloaded(url);
            expectLastCall().atLeastOnce();
            
            replay(listener);
            
            file = tempFile();
            downloader.enqueue(url, file, 0);
            
            Thread.sleep(100);
            
            boolean cancelled = downloader.cancel(url);
            assertTrue("Cannot cancel task", cancelled);
            
            Thread.sleep(1000);
            
            assertFalse("Cancelled file downloaded", text.equals(readAsString(file)));
            
            verify(listener);
        } finally {
            file.delete();
        }
    }
    
    @Test
    public void loading() throws IOException, InterruptedException {
        String remotePath1 = "test";
        String remotePath2 = "myfile";
        String text = bigString();
        completed = false;
        
        File file1 = null;
        File file2 = null;
        try {
            server.mount(remotePath1, text);
            server.mount(remotePath2, text);
            
            Downloader downloader = new DownloaderImpl();
            
            URL url1 = urlFor(remotePath1);
            URL url2 = urlFor(remotePath2);
            file1 = tempFile();
            file2 = tempFile();
            
            assertFalse(downloader.loading(url1, file1));
            
            downloader.enqueue(url1, file1, 0);
            
            assertTrue(downloader.loading(url1, file1));
            assertFalse(downloader.loading(url1, file2));
            assertFalse(downloader.loading(url2, file1));
            assertFalse(downloader.loading(url2, file2));
            Thread.sleep(100);
            assertTrue(downloader.loading(url1, file1));
            
            downloader.enqueue(url2, file2, 0);
            
            assertTrue(downloader.loading(url2, file2));
            
            downloader.cancel(url1);
            
            Thread.sleep(100);
            assertFalse(downloader.loading(url1, file1));
            assertTrue(downloader.loading(url2, file2));
            
        } finally {
            try {
                file2.delete();
            } finally {
                file1.delete();
            }
        }
    }
    
    @Test
    public void range() throws IOException, InterruptedException {
        String remotePath = "test";
        String text = bigString();
        
        File file = null;
        try {
            server.mount(remotePath, text);
            
            Downloader downloader = new DownloaderImpl();
            
            URL url = urlFor(remotePath);
            file = tempFile();
            
            downloader.enqueue(url, file, 0);
            Thread.sleep(100);
            downloader.cancel(url);
            
            Thread.sleep(100);
            assertFalse(text.equals(readAsString(file)));
            
            long loaded = file.length();
            downloader.enqueue(url, file, loaded);
            
            Thread.sleep(100);
            //! assertTrue(file.length() >= loaded); // WebServer does not support range
            
            Thread.sleep(1000);
            assertEquals(text, readAsString(file));
            
        } finally {
            file.delete();
        }
        
    }
    
    @AfterClass
    public static void clean() {
        server.dispose();
    }
    
}
