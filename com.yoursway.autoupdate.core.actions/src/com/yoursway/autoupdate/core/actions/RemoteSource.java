package com.yoursway.autoupdate.core.actions;

import java.io.IOException;
import java.net.URL;

import com.yoursway.utils.XmlWriter;

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

    public void writeTo(XmlWriter w) throws IOException {
        w.attr("path", url.toString());
    }
    
}
