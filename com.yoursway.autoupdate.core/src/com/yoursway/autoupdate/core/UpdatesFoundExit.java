package com.yoursway.autoupdate.core;

public class UpdatesFoundExit extends Exception {

    private static final long serialVersionUID = 1L;

    public UpdatesFoundExit() {
        super();
    }

    public UpdatesFoundExit(String message) {
        super(message);
    }

}
