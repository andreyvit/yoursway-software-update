package com.yoursway.autoupdate.core.glue;

public class PersisterNonOperational extends Exception {

    private static final long serialVersionUID = 1L;

    public PersisterNonOperational() {
        super();
    }

    public PersisterNonOperational(String message, Throwable cause) {
        super(message, cause);
    }

    public PersisterNonOperational(String message) {
        super(message);
    }

    public PersisterNonOperational(Throwable cause) {
        super(cause);
    }

}
