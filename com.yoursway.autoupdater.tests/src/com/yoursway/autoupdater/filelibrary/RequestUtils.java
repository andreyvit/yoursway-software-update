package com.yoursway.autoupdater.filelibrary;

import static com.google.common.collect.Lists.newLinkedList;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collection;

import com.yoursway.autoupdater.tests.internal.server.WebServer;

public class RequestUtils {
    
    static Request request(String filename, int size, String hash) throws MalformedURLException {
        return new Request(new URL("http://localhost:" + WebServer.PORT + "/" + filename), size, hash);
    }
    
    public static Collection<Request> do_order(FileLibrary fileLibrary, int requestsCount)
            throws MalformedURLException {
        Collection<Request> requests = requests(1, requestsCount);
        fileLibrary.order(new FileLibraryOrder(requests));
        return requests;
    }
    
    public static Collection<Request> requests(int first, int last) throws MalformedURLException {
        Collection<Request> requests = newLinkedList();
        for (int i = first; i <= last; i++)
            requests.add(request("url" + i, sizeOf(i), "hash" + i));
        return requests;
    }
    
    public static void mount(WebServer server, Collection<Request> requests) {
        for (Request request : requests) {
            String path = request.url.getPath().substring(1);
            server.mount(path, fileContents((int) request.size));
        }
    }
    
    public static String fileContents(long length) {
        StringBuilder sb = new StringBuilder();
        long rest = length;
        while (rest > 20) {
            String s = "file contents ";
            sb.append(s);
            rest -= s.length();
        }
        while (rest > 0) {
            String s = "a";
            sb.append(s);
            rest -= s.length();
        }
        return sb.toString();
    }
    
    public static int sizeOf(int i) {
        return i * 100;
    }
    
    public static URL url(Request request) {
        return request.url;
    }
    
    public static long size(Request request) {
        return request.size;
    }
    
}
