package com.yoursway.autoupdater.filelibrary;

import static com.yoursway.autoupdater.filelibrary.RequestUtils.order;
import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.expectLastCall;
import static org.easymock.EasyMock.notNull;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;

import java.io.File;
import java.io.IOException;

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
        fileLibrary = new FileLibraryImpl(downloader, place);
        
        listener = createMock(FileLibraryListener.class);
    }
    
    @Test
    public void order_libraryChanged() throws IOException {
        listener.libraryChanged((LibraryState) notNull());
        expectLastCall().times(1 + 2 * 3);
        replay(listener);
        
        FileLibraryOrder order = order(3);
        fileLibrary.events().addListener(listener);
        fileLibrary.order(order);
        
        for (Request request : order)
            downloader.createFile(request);
        
        verify(listener);
    }
    
}
