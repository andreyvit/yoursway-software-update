package com.yoursway.autoupdater.tests;

import static com.yoursway.utils.YsFileUtils.readAsString;
import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;
import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.yoursway.autoupdater.filelibrary.downloader.Downloader;
import com.yoursway.autoupdater.filelibrary.downloader.DownloaderImpl;
import com.yoursway.autoupdater.filelibrary.downloader.DownloaderListener;
import com.yoursway.autoupdater.tests.internal.server.WebServer;

public class DownloaderTests {
    
    private static WebServer server;
    
    private volatile boolean completed;
    private volatile boolean cancelled;
    private volatile boolean someBytesDownloaded;
    
    @BeforeClass
    public static void setup() {
        server = new WebServer();
    }
    
    @Before
    public void setupEach() {
        completed = false;
        cancelled = false;
        someBytesDownloaded = false;
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
                
                public void cancelled(URL url) {
                    cancelled = true;
                }
            });
            
            URL url = urlFor(remotePath);
            file = tempFile();
            
            synchronized (this) {
                downloader.enqueue(url, file, 0);
                wait();
            }
            
            assertEquals("The file has not been downloaded correctly", text, readAsString(file));
            assertTrue("The task has not been completed", completed);
            assertFalse("The task has been cancelled", cancelled);
            
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
            
            final URL url = urlFor(remotePath);
            
            Downloader downloader = new DownloaderImpl();
            downloader.events().addListener(new DownloaderListener() {
                public void someBytesDownloaded(URL url) {
                    synchronized (DownloaderTests.this) {
                        DownloaderTests.this.notify();
                    }
                }
                
                public void completed(URL url) {
                    completed = true;
                }
                
                public void cancelled(URL _url) {
                    if (_url == url) {
                        synchronized (DownloaderTests.this) {
                            cancelled = true;
                            DownloaderTests.this.notify();
                        }
                    }
                }
            });
            
            file = tempFile();
            
            synchronized (this) {
                downloader.enqueue(url, file, 0);
                wait();
            }
            
            synchronized (this) {
                boolean ok = downloader.cancel(url);
                assertTrue("Cannot cancel task", ok);
                
                while (!cancelled)
                    wait();
            }
            
            assertFalse("Cancelled file has been downloaded", text.equals(readAsString(file)));
            assertFalse("Cancelled task has been completed", completed);
            assertTrue("The task has not been cancelled", cancelled);
            
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
            downloader.events().addListener(new DownloaderListener() {
                public void someBytesDownloaded(URL url) {
                    synchronized (DownloaderTests.this) {
                        someBytesDownloaded = true;
                        DownloaderTests.this.notify();
                    }
                }
                
                public void completed(URL url) {
                    // nothing
                }
                
                public void cancelled(URL url) {
                    synchronized (DownloaderTests.this) {
                        cancelled = true;
                        DownloaderTests.this.notify();
                    }
                }
            });
            
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
            
            synchronized (this) {
                while (!someBytesDownloaded)
                    wait();
            }
            
            assertTrue(downloader.loading(url1, file1));
            
            downloader.enqueue(url2, file2, 0);
            
            assertTrue(downloader.loading(url2, file2));
            
            downloader.cancel(url1);
            
            synchronized (this) {
                while (!cancelled)
                    wait();
            }
            
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
            downloader.events().addListener(new DownloaderListener() {
                public void someBytesDownloaded(URL url) {
                    synchronized (DownloaderTests.this) {
                        someBytesDownloaded = true;
                        DownloaderTests.this.notify();
                    }
                }
                
                public void completed(URL url) {
                    synchronized (DownloaderTests.this) {
                        completed = true;
                        DownloaderTests.this.notify();
                    }
                }
                
                public void cancelled(URL url) {
                    synchronized (DownloaderTests.this) {
                        cancelled = true;
                        DownloaderTests.this.notify();
                    }
                }
            });
            
            URL url = urlFor(remotePath);
            file = tempFile();
            
            downloader.enqueue(url, file, 0);
            synchronized (this) {
                while (!someBytesDownloaded)
                    wait();
            }
            downloader.cancel(url);
            
            synchronized (this) {
                while (!cancelled)
                    wait();
            }
            assertFalse(text.equals(readAsString(file)));
            
            long loaded = file.length();
            someBytesDownloaded = false;
            downloader.enqueue(url, file, loaded);
            
            synchronized (this) {
                while (!someBytesDownloaded)
                    wait();
            }
            //! assertTrue(file.length() >= loaded); // WebServer does not support range
            
            synchronized (this) {
                while (!completed)
                    wait();
            }
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
