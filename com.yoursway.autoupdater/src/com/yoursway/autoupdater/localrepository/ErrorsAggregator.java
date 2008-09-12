package com.yoursway.autoupdater.localrepository;

import static com.yoursway.utils.broadcaster.BroadcasterFactory.newBroadcaster;

import com.yoursway.autoupdater.auxiliary.AutoupdaterException;
import com.yoursway.autoupdater.auxiliary.ErrorsListener;
import com.yoursway.utils.EventSource;
import com.yoursway.utils.broadcaster.Broadcaster;

public class ErrorsAggregator implements EventSource<ErrorsListener>, ErrorsListener {
    
    Broadcaster<ErrorsListener> broadcaster = newBroadcaster(ErrorsListener.class);
    
    public void addListener(ErrorsListener listener) {
        broadcaster.addListener(listener);
    }
    
    public void removeListener(ErrorsListener listener) {
        broadcaster.removeListener(listener);
    }
    
    public void errorOccured(AutoupdaterException e) {
        broadcaster.fire().errorOccured(e);
    }
    
}
