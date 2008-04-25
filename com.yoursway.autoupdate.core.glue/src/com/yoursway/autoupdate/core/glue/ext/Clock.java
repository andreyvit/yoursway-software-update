package com.yoursway.autoupdate.core.glue.ext;

public interface Clock {
    
    public final static long ANYTIME = -1;
    
    public final static long NEVER = -2;
    
    public final static long MUST_NOT_BE_USED = -3;
    
    long now();
    
}
