package com.yoursway.autoupdate.core.glue;

public interface PersistentState {
    
    void addListener(StateListener listener);
    
    void removeListener(StateListener listener);
    
    Object createMemento();
    
}