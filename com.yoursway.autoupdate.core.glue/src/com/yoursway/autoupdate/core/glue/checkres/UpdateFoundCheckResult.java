package com.yoursway.autoupdate.core.glue.checkres;

public class UpdateFoundCheckResult extends CheckResultImpl {

    @Override
    public String toString() {
        return "UPDATE_FOUND";
    }

    public boolean isSuccess() {
        return true;
    }

    public void accept(CheckResultVisitor visitor) {
        visitor.updateFound(this);
    }
    
    public boolean updatesFound() {
        return true;
    }

}
