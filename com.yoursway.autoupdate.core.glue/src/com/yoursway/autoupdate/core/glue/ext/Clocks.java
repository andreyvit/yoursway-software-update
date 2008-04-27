package com.yoursway.autoupdate.core.glue.ext;

import static com.yoursway.autoupdate.core.glue.ext.Clock.ANYTIME;
import static com.yoursway.autoupdate.core.glue.ext.Clock.MUST_NOT_BE_USED;
import static com.yoursway.autoupdate.core.glue.ext.Clock.NEVER;

public class Clocks {
    
    public static long max(long a, long b) {
        if (a == Clock.MUST_NOT_BE_USED)
            throw new IllegalArgumentException("max: a MUST_NOT_BE_USED");
        if (b == Clock.MUST_NOT_BE_USED)
            throw new IllegalArgumentException("max: b MUST_NOT_BE_USED");
        if (a == Clock.NEVER)
            return NEVER;
        if (b == Clock.NEVER)
            return NEVER;
        if (a == Clock.ANYTIME)
            return b;
        if (b == Clock.ANYTIME)
            return a;
        return Math.max(a, b);
    }
    
    public static long add(long a, long b) {
        if (a == Clock.MUST_NOT_BE_USED)
            throw new IllegalArgumentException("add: a MUST_NOT_BE_USED");
        if (b == Clock.MUST_NOT_BE_USED)
            throw new IllegalArgumentException("add: b MUST_NOT_BE_USED");
        if (a == Clock.NEVER || b == Clock.NEVER)
            return NEVER;
        if (a == Clock.ANYTIME)
            throw new IllegalArgumentException("add: a is ANYTIME");
        if (b == Clock.ANYTIME)
            throw new IllegalArgumentException("add: b is ANYTIME");
        return a + b;
    }
    
    public static boolean isConcrete(long time) {
        return time > 0;
    }
    
    public static boolean isNotConcrete(long time) {
        return time < 0;
    }
    
    public static boolean isAfter(long a, long b) {
        mustBeConcrete(a, b);
        return a > b;
    }

    private static void mustBeConcrete(long a, long b) {
        if (a == MUST_NOT_BE_USED)
            throw new IllegalArgumentException("add: a MUST_NOT_BE_USED");
        if (b == MUST_NOT_BE_USED)
            throw new IllegalArgumentException("add: b MUST_NOT_BE_USED");
        if (a == NEVER)
            throw new IllegalArgumentException("add: a NEVER");
        if (b == Clock.NEVER)
            throw new IllegalArgumentException("add: b NEVER");
        if (a == ANYTIME)
            throw new IllegalArgumentException("add: a is ANYTIME");
        if (b == Clock.ANYTIME)
            throw new IllegalArgumentException("add: b is ANYTIME");
    }

    public static String toString(long time) {
        if (time == MUST_NOT_BE_USED)
            return "MUST_NOT_BE_USED";
        if (time == NEVER)
            return "NEVER";
        if (time == ANYTIME)
            return "ANYTIME";
        return "" + time;
    }

    public static long sub(long a, long b) {
        mustBeConcrete(a, b);
        return a - b;
    }
    
}
