package com.yoursway.autoupdater.auxiliary;

import static com.yoursway.utils.broadcaster.BroadcasterFactory.newBroadcaster;

import com.yoursway.utils.EventSource;
import com.yoursway.utils.broadcaster.Broadcaster;

public class ErrorsAggregator implements EventSource<ErrorsListener>, ErrorsListener {
    
    private final Broadcaster<ErrorsListener> broadcaster = newBroadcaster(ErrorsListener.class);
    private int listenersCount = 0;
    
    public void addListener(ErrorsListener listener) {
        listenersCount++;
        broadcaster.addListener(listener);
    }
    
    public void removeListener(ErrorsListener listener) {
        listenersCount--;
        broadcaster.removeListener(listener);
    }
    
    public void errorOccured(AutoupdaterException e) {
        if (listenersCount > 0)
            broadcaster.fire().errorOccured(e);
        else
            throw new RuntimeException("Errors aggregator has no listeners");
    }
    
}
