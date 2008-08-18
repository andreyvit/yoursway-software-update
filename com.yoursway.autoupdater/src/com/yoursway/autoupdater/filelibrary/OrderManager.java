package com.yoursway.autoupdater.filelibrary;

public class OrderManager {
    
    private FileLibrary fileLibrary;
    
    OrderManager(FileLibrary fileLibrary2) {
        // TODO Auto-generated constructor stub
    }
    
    public void orderChanged() {
        FileLibraryOrder order = new FileLibraryOrder();
        //> collect requests
        fileLibrary.order(order);
    }
}
