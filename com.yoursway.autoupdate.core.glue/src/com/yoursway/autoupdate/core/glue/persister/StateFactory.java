package com.yoursway.autoupdate.core.glue.persister;

public interface StateFactory {
    
    PersistentState createEmptyState();
    
    PersistentState createState(Object memento);
    
}
