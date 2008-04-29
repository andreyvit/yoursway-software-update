package com.yoursway.autoupdate.core.checkres;

public class CommunicationErrorCheckResult extends CheckResultImpl {

    private final Throwable throwable;

    public CommunicationErrorCheckResult(Throwable throwable) {
        if (throwable == null)
            throw new NullPointerException("throwable is null");
        this.throwable = throwable;
    }
    
    public Throwable getThrowable() {
        return throwable;
    }

    @Override
    public String toString() {
        return "COMMUNICATION_ERROR";
    }

    public boolean isSuccess() {
        return false;
    }

    public void accept(CheckResultVisitor visitor) {
        visitor.communicationError(this);
    }
    
}
