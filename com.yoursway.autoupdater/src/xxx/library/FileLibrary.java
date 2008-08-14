package xxx.library;

import java.util.Collection;

import xxx.Request;

import com.yoursway.utils.EventSource;
import com.yoursway.utils.broadcaster.Broadcaster;
import com.yoursway.utils.broadcaster.BroadcasterFactory;

public class FileLibrary {
    
    private final Downloader downloader;
    
    private final Broadcaster<FileLibraryListener> broadcaster = BroadcasterFactory
            .newBroadcaster(FileLibraryListener.class);
    
    public FileLibrary(Downloader downloader) {
        this.downloader = downloader;
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
    
}
