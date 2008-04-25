package com.yoursway.autoupdate.core.glue.checkres;

public class CommunicationErrorCheckResult extends CheckResultImpl {

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
