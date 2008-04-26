package com.yoursway.autoupdate.core.glue;

public class TransactionalStorageNonOperational extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public TransactionalStorageNonOperational() {
        super();
    }

    public TransactionalStorageNonOperational(String message, Throwable cause) {
        super(message, cause);
    }

    public TransactionalStorageNonOperational(String message) {
        super(message);
    }

    public TransactionalStorageNonOperational(Throwable cause) {
        super(cause);
    }
    
}
