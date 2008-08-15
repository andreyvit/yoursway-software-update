package com.yoursway.autoupdater.filelibrary;

import static com.google.common.collect.Lists.newLinkedList;
import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.expectLastCall;
import static org.easymock.EasyMock.notNull;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collection;

import org.junit.Before;
import org.junit.Test;

public class FileLibraryTests {
    
    private FileLibrary fileLibrary;
    private FileLibraryListener listener;
    private DownloaderMock downloader;
    
    @Before
    public void setUp() throws IOException {
        File place = File.createTempFile("autoupdater.test.filelibrary", null);
        place.delete();
        place.mkdir();
        
        downloader = new DownloaderMock();
        fileLibrary = new FileLibrary(downloader, place);
        
        listener = createMock(FileLibraryListener.class);
    }
    
    @Test
    public void order_libraryChanged() throws IOException {
        listener.libraryChanged((LibraryState) notNull());
        expectLastCall().times(1 + 2 * 3);
        replay(listener);
        
        Collection<Request> requests = requests(3);
        fileLibrary.events().addListener(listener);
        fileLibrary.order(requests);
        
        for (Request request : requests)
            downloader.createFile(request);
        
        verify(listener);
    }
    
    private Collection<Request> requests(int count) throws MalformedURLException {
        Collection<Request> requests = newLinkedList();
        for (int i = 1; i <= count; i++)
            requests.add(request("url" + i, i * 100, "hash" + i));
        return requests;
    }
    
    private Request request(String filename, int size, String hash) throws MalformedURLException {
        return new Request(new URL("http://localhost/" + filename), size, hash);
    }
}
