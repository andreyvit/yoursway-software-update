package com.yoursway.autoupdate.core;

public class UpdatesFoundExit extends Exception {

    private static final long serialVersionUID = 1L;
    
    private final boolean shouldRestart;

    public UpdatesFoundExit(boolean shouldRestart) {
        this.shouldRestart = shouldRestart;
    }
    
    public boolean shouldRestart() {
        return shouldRestart;
    }

}
