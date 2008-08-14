package xxx.library;

import java.io.File;
import java.net.URL;

import com.yoursway.utils.annotations.Immutable;

@Immutable
public class FileState {
    
    final URL url;
    private final long size;
    private final long doneSize;
    private final File localFile;
    
    FileState(URL url, long size, long doneSize, File localFile) {
        if (url == null)
            throw new NullPointerException("url is null");
        if (localFile == null)
            throw new NullPointerException("localFile is null");
        
        this.url = url;
        this.size = size;
        this.doneSize = doneSize;
        this.localFile = localFile;
    }
    
    public boolean isDone() {
        return size == doneSize;
    }
    
    public double progress() {
        return size == 0 ? 1.0 : (doneSize * 1.0 / size);
    }
    
    public File getLocalFile() {
        if (!isDone())
            throw new IllegalStateException("The file has not yet been downloaded.");
        
        return localFile;
    }
    
}
