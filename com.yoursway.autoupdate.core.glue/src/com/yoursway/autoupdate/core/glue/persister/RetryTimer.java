package com.yoursway.autoupdate.core.glue.persister;

/**
 * Thread safety: <code>isActive</code> is safe for calls from any thread; all
 * other methods require external synchronization for multithreaded usage.
 */
public class RetryTimer {
    
    private volatile long nextAttempTime;
    
    private static final int delays[] = new int[] { 1, 2, 3, 5, 8, 13, 21, 34, 55, 89, 144, 233, 377 };
    
    private int nextDelayIndex;
    
    public RetryTimer() {
        successfulAttemp();
    }
    
    public void successfulAttemp() {
        nextAttempTime = -1;
        nextDelayIndex = 0;
    }
    
    public void failedAttemp() {
        nextAttempTime = System.currentTimeMillis() + 1000 * delays[nextDelayIndex];
        if (nextDelayIndex + 1 < delays.length)
            nextDelayIndex += 1;
    }
    
    public boolean isActive() {
        return nextAttempTime != -1;
    }
    
    public long delayUntilNextAttemp() {
        if (!isActive())
            throw new IllegalStateException(getClass().getSimpleName()
                    + ": delayUntilNextAttemp can only be called when isActive is true");
        return nextAttempTime - System.currentTimeMillis();
    }
    
    public boolean shouldMakeNextAttemp() {
        return delayUntilNextAttemp() <= 0;
    }

    public int delayUntilNextAttempOrZero() {
        return (int) (isActive() ? delayUntilNextAttemp() : 0);
    }
    
}
