package com.yoursway.autoupdate.core.glue.unused;
//
//import com.yoursway.autoupdate.core.glue.state.schedule.Schedule;
//
//public class Warnings {
//    
//    private void checkForCriticalThreshold() {
//        if (nextScheduledUpdate >= 0 && !timing.isUpdateFarEnoughToGiveWarnings(nextScheduledUpdate - now()))
//            return;
//        long now = now();
//        if (checkCriticalWarning(now))
//            return;
//        checkRegularWarning(now);
//    }
//    
//    private boolean checkCriticalWarning(long now) {
//        long criticalWarningTime = -1;
//        if (userConfig.isCriticalWarningEnabled())
//            criticalWarningTime = timing.criticalWarningTime(schedule, lastSuccessfulCheckTime,
//                    firstFailedCheckAfterLastSuccessfulCheckTime, lastCheckAttempTime, firstRunTime);
//        if (criticalWarningTime > 0 && criticalWarningTime <= now) {
//            long lastWarning = userConfig.getLastCriticalWarningTime();
//            if (lastSuccessfulCheckTime > 0 && lastWarning < lastSuccessfulCheckTime)
//                lastWarning = -1;
//            if (lastWarning >= 0)
//                criticalWarningTime = Math.max(criticalWarningTime, timing
//                        .delayRepeatedCriticalWarning(lastWarning));
//            if (criticalWarningTime <= now) {
//                callback.issueCriticalWarning();
//                userConfig.setLastCriticalWarningTime(now());
//                return true;
//            }
//        }
//        return false;
//    }
//    
//    private void checkRegularWarning(long now) {
//        long regularWarningTime = -1;
//        if (userConfig.isRegularWarningEnabled() && schedule != Schedule.MANUAL
//                && lastCheckAttempTime > lastSuccessfulCheckTime)
//            regularWarningTime = timing.regularWarningTime(schedule, lastSuccessfulCheckTime,
//                    firstFailedCheckAfterLastSuccessfulCheckTime, lastCheckAttempTime);
//        if (regularWarningTime > 0 && regularWarningTime < now) {
//            long lastWarning = userConfig.getLastRegularWarningTime();
//            if (lastSuccessfulCheckTime > 0 && lastWarning < lastSuccessfulCheckTime)
//                lastWarning = -1;
//            if (lastWarning >= 0)
//                regularWarningTime = Math.max(regularWarningTime, timing.delayRepeatedRegularWarning(
//                        schedule, lastWarning));
//            if (regularWarningTime <= now) {
//                callback.issueRegularWarning();
//                userConfig.setLastRegularWarningTime(now());
//                return;
//            }
//        }
//    }
//       
//}
