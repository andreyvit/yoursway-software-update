package com.yoursway.autoupdate.core.actions;

import java.net.URL;

public final class RemoteSource {
    
    private final URL url;

    public RemoteSource(URL url) {
        if (url == null)
            throw new NullPointerException("url");
        this.url = url;
    }
    
    public URL url() {
        return url;
    }
    
}
