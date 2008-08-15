package com.yoursway.autoupdater.filelibrary;

import java.io.File;
import java.net.URL;
import java.util.Collection;
import java.util.LinkedList;

import com.yoursway.autoupdater.filelibrary.downloader.Downloader;
import com.yoursway.autoupdater.filelibrary.downloader.DownloaderListener;
import com.yoursway.utils.EventSource;
import com.yoursway.utils.broadcaster.Broadcaster;
import com.yoursway.utils.broadcaster.BroadcasterFactory;

public class FileLibrary {
    
    private final Downloader downloader;
    
    private final Broadcaster<FileLibraryListener> broadcaster = BroadcasterFactory
            .newBroadcaster(FileLibraryListener.class);
    
    private final OrderManager orderManager;
    
    private final File place;
    
    public FileLibrary(Downloader downloader, File placeDir) {
        if (downloader == null)
            throw new NullPointerException("downloader is null");
        if (placeDir == null)
            throw new NullPointerException("placeDir is null");
        
        this.downloader = downloader;
        place = placeDir;
        
        downloader.events().addListener(new DownloaderListener() {
            public void completed(URL url) {
                changed();
            }
            
            public void someBytesDownloaded(URL url) {
                changed();
            }
        });
        
        orderManager = new OrderManager(this);
    }
    
    public void order(Collection<Request> requests) {
        //> downloader.download files
        for (Request request : requests)
            downloader.enqueue(request.url, new File(place, request.filename()));
        
        changed();
    }
    
    public EventSource<FileLibraryListener> events() {
        return broadcaster;
    }
    
    private void changed() {
        LibraryState state = new LibraryState(new LinkedList<FileState>()); //> get state
        broadcaster.fire().libraryChanged(state); //> fire when 1% of any file loaded  
    }
    
    public OrderManager orderManager() {
        return orderManager;
    }
    
}
