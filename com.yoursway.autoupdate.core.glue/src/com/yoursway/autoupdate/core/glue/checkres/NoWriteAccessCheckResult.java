package com.yoursway.autoupdate.core.glue.checkres;

public class NoWriteAccessCheckResult extends CheckResultImpl {

    @Override
    public String toString() {
        return "NO_WRITE_ACCESS";
    }

    public boolean isSuccess() {
        return false;
    }
    
    public boolean isNoWriteAccessResult() {
        return true;
    }

    public void accept(CheckResultVisitor visitor) {
        visitor.noWriteAccess();
    }

}
