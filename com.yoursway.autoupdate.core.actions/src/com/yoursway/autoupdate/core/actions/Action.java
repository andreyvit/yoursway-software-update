package com.yoursway.autoupdate.core.actions;

import java.io.IOException;
import java.io.Serializable;

public interface Action extends Serializable {
    
    void execute(Executor executor) throws IOException;
    
}
