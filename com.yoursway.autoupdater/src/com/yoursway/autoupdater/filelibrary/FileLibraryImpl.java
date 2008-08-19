package com.yoursway.autoupdater.filelibrary;

import static com.google.common.collect.Lists.newLinkedList;
import static com.google.common.collect.Maps.newHashMap;

import java.io.File;
import java.net.URL;
import java.util.List;
import java.util.Map;

import com.yoursway.autoupdater.filelibrary.downloader.Downloader;
import com.yoursway.autoupdater.filelibrary.downloader.DownloaderListener;
import com.yoursway.utils.EventSource;
import com.yoursway.utils.broadcaster.Broadcaster;
import com.yoursway.utils.broadcaster.BroadcasterFactory;

public class FileLibraryImpl implements FileLibrary {
    
    private final Downloader downloader;
    
    private final Broadcaster<FileLibraryListener> broadcaster = BroadcasterFactory
            .newBroadcaster(FileLibraryListener.class);
    
    private final OrderManager orderManager;
    
    private final File place;
    
    private final Map<URL, LibraryFile> files = newHashMap();
    
    public FileLibraryImpl(Downloader downloader, File placeDir) {
        if (downloader == null)
            throw new NullPointerException("downloader is null");
        if (placeDir == null)
            throw new NullPointerException("placeDir is null");
        
        this.downloader = downloader;
        place = placeDir;
        
        //> restore files list
        
        downloader.events().addListener(new DownloaderListener() {
            public void completed(URL url) {
                changed(false);
            }
            
            public void someBytesDownloaded(URL url) {
                changed(false);
            }
            
            public void cancelled(URL url) {
                // nothing
            }
        });
        
        orderManager = new OrderManager(this);
    }
    
    public void order(FileLibraryOrder order) {
        for (LibraryFile file : files.values())
            if (!order.contains(file))
                downloader.cancel(file.url);
        
        for (Request request : order) {
            URL url = request.url;
            
            LibraryFile file = files.get(url);
            if (file == null) {
                File localFile = new File(place, request.filename());
                file = new LibraryFile(url, request.size, localFile);
                files.put(url, file);
            }
            
            File localFile = file.localFile;
            if (!file.isDone())
                downloader.enqueue(url, localFile, file.doneSize());
        }
        
        changed(true);
    }
    
    public EventSource<FileLibraryListener> events() {
        return broadcaster;
    }
    
    private void changed(boolean forced) {
        List<FileState> fileStates = newLinkedList();
        boolean significantly = false;
        for (LibraryFile file : files.values()) {
            FileState state = file.state();
            fileStates.add(state);
            if (state.significantlyChanged())
                significantly = true;
        }
        
        if (significantly || forced)
            broadcaster.fire().libraryChanged(new LibraryState(fileStates));
    }
    
    public OrderManager orderManager() {
        return orderManager;
    }
    
}
