package com.yoursway.autoupdate.core.checkres;

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
