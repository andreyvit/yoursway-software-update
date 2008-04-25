/**
 * 
 */
package com.yoursway.autoupdate.core.glue.tests;

import com.yoursway.autoupdate.core.glue.ext.Clock;

class MockClock implements Clock {
    
    public long now = 100;
    
    public long now() {
        return now;
    }
    
}