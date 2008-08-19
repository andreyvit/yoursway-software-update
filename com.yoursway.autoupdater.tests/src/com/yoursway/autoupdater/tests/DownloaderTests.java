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
import org.junit.BeforeClass;
import org.junit.Test;

import com.yoursway.autoupdater.filelibrary.downloader.Downloader;
import com.yoursway.autoupdater.filelibrary.downloader.DownloaderImpl;
import com.yoursway.autoupdater.filelibrary.downloader.DownloaderListener;
import com.yoursway.autoupdater.tests.internal.server.WebServer;
import com.yoursway.utils.annotations.SynchronizedWithMonitorOfThis;

public class DownloaderTests {
    
    private static WebServer server;
    
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
        
        File file = null;
        try {
            server.mount(remotePath, text);
            
            Downloader downloader = new DownloaderImpl();
            DownloaderListenerForTests listener = new DownloaderListenerForTests();
            downloader.events().addListener(listener);
            
            URL url = urlFor(remotePath);
            file = tempFile();
            
            downloader.enqueue(url, file, 0);
            listener.wait_completed();
            
            assertEquals("The file has not been downloaded correctly", text, readAsString(file));
            assertTrue("The task has not been completed", listener.completed());
            assertFalse("The task has been cancelled", listener.cancelled());
            
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
            DownloaderListenerForTests listener = new DownloaderListenerForTests(url);
            downloader.events().addListener(listener);
            
            file = tempFile();
            
            downloader.enqueue(url, file, 0);
            listener.wait_someBytesDownloaded();
            
            boolean ok = downloader.cancel(url);
            assertTrue("Cannot cancel task", ok);
            
            listener.wait_cancelled();
            
            assertFalse("Cancelled file has been downloaded", text.equals(readAsString(file)));
            assertFalse("Cancelled task has been completed", listener.completed());
            assertTrue("The task has not been cancelled", listener.cancelled());
            
        } finally {
            file.delete();
        }
    }
    
    @Test
    public void loading() throws IOException, InterruptedException {
        String remotePath1 = "test";
        String remotePath2 = "myfile";
        String text = bigString();
        
        File file1 = null;
        File file2 = null;
        try {
            server.mount(remotePath1, text);
            server.mount(remotePath2, text);
            
            Downloader downloader = new DownloaderImpl();
            DownloaderListenerForTests listener = new DownloaderListenerForTests();
            downloader.events().addListener(listener);
            
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
            listener.wait_someBytesDownloaded();
            assertTrue(downloader.loading(url1, file1));
            
            downloader.enqueue(url2, file2, 0);
            
            assertTrue(downloader.loading(url2, file2));
            
            downloader.cancel(url1);
            listener.wait_cancelled();
            
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
            DownloaderListenerForTests listener = new DownloaderListenerForTests();
            downloader.events().addListener(listener);
            
            URL url = urlFor(remotePath);
            file = tempFile();
            
            downloader.enqueue(url, file, 0);
            listener.wait_someBytesDownloaded();
            downloader.cancel(url);
            
            listener.wait_cancelled();
            assertFalse(text.equals(readAsString(file)));
            
            long loaded = file.length();
            listener.reset_someBytesDownloaded();
            downloader.enqueue(url, file, loaded);
            
            listener.wait_someBytesDownloaded();
            //! assertTrue(file.length() >= loaded); // WebServer does not support range
            
            listener.wait_completed();
            assertEquals(text, readAsString(file));
            
        } finally {
            file.delete();
        }
        
    }
    
    @AfterClass
    public static void clean() {
        server.dispose();
    }
    
    private final class DownloaderListenerForTests implements DownloaderListener {
        
        private final URL url;
        
        @SynchronizedWithMonitorOfThis
        private boolean completed = false;
        @SynchronizedWithMonitorOfThis
        private boolean cancelled = false;
        @SynchronizedWithMonitorOfThis
        private boolean someBytesDownloaded = false;
        
        public DownloaderListenerForTests() {
            url = null;
        }
        
        public synchronized void reset_someBytesDownloaded() {
            someBytesDownloaded = false;
        }
        
        public synchronized void wait_someBytesDownloaded() throws InterruptedException {
            while (!someBytesDownloaded)
                wait();
        }
        
        public synchronized void wait_cancelled() throws InterruptedException {
            while (!cancelled)
                wait();
        }
        
        public synchronized void wait_completed() throws InterruptedException {
            while (!completed)
                wait();
        }
        
        public synchronized boolean cancelled() {
            return cancelled;
        }
        
        public synchronized boolean completed() {
            return completed;
        }
        
        public DownloaderListenerForTests(URL url) {
            this.url = url;
        }
        
        private boolean matching(URL url) {
            return this.url == null || this.url == url;
        }
        
        public synchronized void someBytesDownloaded(URL url) {
            if (matching(url)) {
                someBytesDownloaded = true;
                notify();
            }
        }
        
        public synchronized void completed(URL url) {
            if (matching(url)) {
                completed = true;
                notify();
            }
        }
        
        public synchronized void cancelled(URL url) {
            if (matching(url)) {
                cancelled = true;
                notify();
            }
        }
    }
    
}
