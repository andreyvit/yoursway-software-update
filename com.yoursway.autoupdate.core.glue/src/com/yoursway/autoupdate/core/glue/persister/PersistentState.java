package com.yoursway.autoupdate.core.glue.persister;

public interface PersistentState {
    
    void addListener(StateListener listener);
    
    void removeListener(StateListener listener);
    
    Object createMemento();
    
}