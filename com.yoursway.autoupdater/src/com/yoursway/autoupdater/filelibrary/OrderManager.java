package com.yoursway.autoupdater.filelibrary;

import static com.google.common.collect.Sets.newHashSet;

import java.util.Set;

public class OrderManager {
    
    private final FileLibrary fileLibrary;
    
    private final Set<LibrarySubscriber> subscribers = newHashSet();
    
    OrderManager(FileLibrary fileLibrary) {
        this.fileLibrary = fileLibrary;
    }
    
    public void register(LibrarySubscriber s) {
        subscribers.add(s);
    }
    
    public void unregister(LibrarySubscriber s) {
        subscribers.remove(s);
    }
    
    public void orderChanged() {
        FileLibraryOrder order = new FileLibraryOrder();
        
        for (LibrarySubscriber s : subscribers)
            order.add(s.requiredFiles());
        
        fileLibrary.order(order);
    }
}
