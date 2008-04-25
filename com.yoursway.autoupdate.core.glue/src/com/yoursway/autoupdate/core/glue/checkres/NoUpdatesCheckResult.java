package com.yoursway.autoupdate.core.glue.checkres;

public class NoUpdatesCheckResult extends CheckResultImpl {

    @Override
    public String toString() {
        return "NO_UPDATES";
    }

    public boolean isSuccess() {
        return true;
    }

    public void accept(CheckResultVisitor visitor) {
        visitor.noUpdatesFound();
    }

}
