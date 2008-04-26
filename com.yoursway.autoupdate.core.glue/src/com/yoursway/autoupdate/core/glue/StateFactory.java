package com.yoursway.autoupdate.core.glue;

public interface StateFactory {
    
    PersistentState createEmptyState();
    
    PersistentState createState(Object memento);
    
}
