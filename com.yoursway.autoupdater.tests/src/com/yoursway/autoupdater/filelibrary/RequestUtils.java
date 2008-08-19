package com.yoursway.autoupdater.filelibrary;

import static com.google.common.collect.Lists.newLinkedList;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collection;

import com.yoursway.autoupdater.tests.internal.server.WebServer;

public class RequestUtils {
    
    static Request request(String filename, int size, String hash) throws MalformedURLException {
        return new Request(new URL("http://localhost:8744/" + filename), size, hash);
    }
    
    static FileLibraryOrder order(int requestsCount) throws MalformedURLException {
        return new FileLibraryOrder(requests(1, requestsCount));
    }
    
    public static Collection<Request> requests(int first, int last) throws MalformedURLException {
        Collection<Request> requests = newLinkedList();
        for (int i = first; i <= last; i++)
            requests.add(request("url" + i, i * 100, "hash" + i));
        return requests;
    }
    
    public static void mount(WebServer server, Collection<Request> requests) {
        for (Request request : requests) {
            String path = request.url.getPath().substring(1);
            server.mount(path, "file contents");
        }
    }
}
