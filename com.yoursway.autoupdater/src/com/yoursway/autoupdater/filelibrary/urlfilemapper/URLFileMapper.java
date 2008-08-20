package com.yoursway.autoupdater.filelibrary.urlfilemapper;

import java.io.File;
import java.net.URL;
import java.util.Map;

import com.yoursway.autoupdater.filelibrary.LibraryFile;

public class URLFileMapper {
    
    private final File place;
    private final Map<URL, LibraryFile> files;
    
    public URLFileMapper(File placeDir, Map<URL, LibraryFile> files) {
        this.place = placeDir;
        this.files = files;
    }
    
    public URLFileMapping mappingFor(URL url) {
        LibraryFile file = files.get(url);
        File localFile = (file != null ? file.localFile() : new File(place, filename(url)));
        
        //> check if the file exists already
        
        return new URLFileMapping(url, localFile);
    }
    
    private static String filename(URL url) {
        String path = url.getPath();
        int slash = path.lastIndexOf('/');
        if (slash >= 0 && slash < path.length() - 1)
            return path.substring(slash + 1);
        else
            throw new AssertionError("URL doesn't contain filename.");
    }
    
}
