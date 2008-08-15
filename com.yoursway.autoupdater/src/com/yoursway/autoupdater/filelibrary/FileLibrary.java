package com.yoursway.autoupdater.filelibrary;

import java.util.Collection;

import com.yoursway.autoupdater.filelibrary.downloader.Downloader;
import com.yoursway.utils.EventSource;
import com.yoursway.utils.broadcaster.Broadcaster;
import com.yoursway.utils.broadcaster.BroadcasterFactory;

public class FileLibrary {
    
    private final Downloader downloader;
    
    private final Broadcaster<FileLibraryListener> broadcaster = BroadcasterFactory
            .newBroadcaster(FileLibraryListener.class);
    
    private final OrderManager orderManager;
    
    public FileLibrary(Downloader downloader) {
        if (downloader == null)
            throw new NullPointerException("downloader is null");
        
        this.downloader = downloader;
        
        orderManager = new OrderManager(this);
    }
    
    public void order(Collection<Request> requests) {
        //> downloader.download files
    }
    
    public EventSource<FileLibraryListener> events() {
        return broadcaster;
    }
    
    private void a() {
        LibraryState state = null; //!
        broadcaster.fire().libraryChanged(state); //> fire when 1% of any file loaded  
    }
    
    public OrderManager orderManager() {
        return orderManager;
    }
    
}
