package com.yoursway.autoupdater.tests;

import static com.yoursway.autoupdater.filelibrary.RequestUtils.do_order;
import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.expectLastCall;
import static org.easymock.EasyMock.notNull;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;

import java.io.File;
import java.io.IOException;
import java.util.Collection;

import org.junit.Before;
import org.junit.Test;

import com.yoursway.autoupdater.filelibrary.FileLibrary;
import com.yoursway.autoupdater.filelibrary.FileLibraryImpl;
import com.yoursway.autoupdater.filelibrary.FileLibraryListener;
import com.yoursway.autoupdater.filelibrary.LibraryState;
import com.yoursway.autoupdater.filelibrary.Request;
import com.yoursway.autoupdater.tests.internal.DownloaderMock;
import com.yoursway.utils.YsFileUtils;

public class FileLibraryTests {
    
    private FileLibrary fileLibrary;
    private FileLibraryListener listener;
    private DownloaderMock downloader;
    
    @Before
    public void setUp() throws IOException {
        File place = YsFileUtils.createTempFolder("autoupdater.test.filelibrary", null);
        
        downloader = new DownloaderMock();
        fileLibrary = new FileLibraryImpl(downloader, place);
        
        listener = createMock(FileLibraryListener.class);
    }
    
    @Test
    public void order_libraryChanged() throws IOException {
        listener.libraryChanged((LibraryState) notNull());
        expectLastCall().times(1 + 1 * 3);
        replay(listener);
        
        fileLibrary.events().addListener(listener);
        Collection<Request> requests = do_order(fileLibrary, 3); // libraryChanged
        
        for (Request request : requests)
            downloader.createFile(request); // libraryChanged
        
        verify(listener);
    }
}
