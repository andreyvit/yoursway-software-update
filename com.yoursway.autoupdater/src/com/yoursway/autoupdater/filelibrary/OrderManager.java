package com.yoursway.autoupdater.filelibrary;

import java.util.Collection;


public class OrderManager {
    
    private FileLibrary fileLibrary;
    
    OrderManager(FileLibrary fileLibrary2) {
        // TODO Auto-generated constructor stub
    }
    
    public void orderChanged() {
        Collection<Request> requests = null; //> collect requests
        
        fileLibrary.order(requests);
    }
}
