package com.yoursway.autoupdate.core.checkres;

public class ShutdownOccuredCheckResult extends CheckResultImpl {

    @Override
    public String toString() {
        return "SHUTDOWN_OCCURED";
    }

    public boolean isSuccess() {
        return false;
    }

    public void accept(CheckResultVisitor visitor) {
        visitor.shutdownOccured();
    }
   
}
