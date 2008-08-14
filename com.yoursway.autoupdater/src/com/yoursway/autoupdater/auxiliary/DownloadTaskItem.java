package com.yoursway.autoupdater.auxiliary;

public class DownloadTaskItem {
    
    private final String url;
    
    public DownloadTaskItem(String url) {
        this.url = url;
    }
    
    public String url() {
        return url;
    }
    
    public String filename() {
        int slash = url.lastIndexOf('/');
        if (slash >= 0 && slash < url.length() - 1)
            return url.substring(slash + 1);
        else
            return "noname"; //!
    }
    
    public int filesize() {
        throw new UnsupportedOperationException();
    }
    
}
