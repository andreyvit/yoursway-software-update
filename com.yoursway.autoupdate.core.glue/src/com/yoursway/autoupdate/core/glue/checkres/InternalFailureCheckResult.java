package com.yoursway.autoupdate.core.glue.checkres;

public class InternalFailureCheckResult extends CheckResultImpl {

    private final Throwable throwable;

    public InternalFailureCheckResult(Throwable throwable) {
        if (throwable == null)
            throw new NullPointerException("throwable is null");
        this.throwable = throwable;
    }

    @Override
    public String toString() {
        return "INTERNAL_FAILURE(" + throwable.getClass().getSimpleName() + ")";
    }

    public boolean isSuccess() {
        return false;
    }

    public void accept(CheckResultVisitor visitor) {
        visitor.internalFailure(this);
    }
   
}
