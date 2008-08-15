package com.yoursway.autoupdater.filelibrary;

public class RequiredFile {
    
    private final String url;
    
    public RequiredFile(String url) {
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
