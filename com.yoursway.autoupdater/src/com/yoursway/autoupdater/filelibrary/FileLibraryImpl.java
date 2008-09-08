package com.yoursway.autoupdater.filelibrary;

import static com.google.common.collect.Lists.newLinkedList;
import static com.google.common.collect.Maps.newHashMap;

import java.io.File;
import java.net.URL;
import java.util.List;
import java.util.Map;

import com.yoursway.autoupdater.filelibrary.downloader.Downloader;
import com.yoursway.autoupdater.filelibrary.downloader.DownloaderListener;
import com.yoursway.autoupdater.filelibrary.urlfilemapper.URLFileMapper;
import com.yoursway.autoupdater.filelibrary.urlfilemapper.URLFileMapping;
import com.yoursway.utils.EventSource;
import com.yoursway.utils.broadcaster.Broadcaster;
import com.yoursway.utils.broadcaster.BroadcasterFactory;
import com.yoursway.utils.log.Log;

public class FileLibraryImpl implements FileLibrary {
    
    private final Downloader downloader;
    
    private final Broadcaster<FileLibraryListener> broadcaster = BroadcasterFactory
            .newBroadcaster(FileLibraryListener.class);
    
    private final OrderManager orderManager;
    
    private final URLFileMapper urlFileMapper;
    private final Map<URL, LibraryFile> files = newHashMap();
    
    public FileLibraryImpl(Downloader downloader, File placeDir) {
        if (downloader == null)
            throw new NullPointerException("downloader is null");
        if (placeDir == null)
            throw new NullPointerException("placeDir is null");
        
        placeDir.mkdirs();
        
        this.downloader = downloader;
        urlFileMapper = new URLFileMapper(placeDir, files);
        
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
        
        Log.write("FileLibrary has been created in " + placeDir + ".");
    }
    
    public void order(FileLibraryOrder order) {
        for (LibraryFile file : files.values())
            if (!order.contains(file))
                downloader.cancel(file.url);
        
        for (Request request : order) {
            URL url = request.url();
            URLFileMapping mapping = urlFileMapper.mappingFor(url);
            
            LibraryFile file = files.get(url);
            if (file == null) {
                file = new LibraryFile(url, request.size, mapping.file());
                files.put(url, file);
            }
            
            if (!file.isDone())
                downloader.enqueue(mapping, file.doneSize());
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
