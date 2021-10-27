package com.example.smartalarm;

import android.content.Context;
import android.os.PowerManager;
import android.util.Log;

public class AlarmWakeLock {
    /** for notification use **/

    private static PowerManager.WakeLock sCpuWakeLock;
    static void acquireCpuWakeLock(Context context) {
        Log.i("Acquiring cpu wake lock" , "wakeLock");
        if (sCpuWakeLock != null) {
            return;
        }
        PowerManager pm =
                (PowerManager) context.getSystemService(Context.POWER_SERVICE);
        sCpuWakeLock = pm.newWakeLock(
                PowerManager.PARTIAL_WAKE_LOCK |
                        PowerManager.ACQUIRE_CAUSES_WAKEUP |
                        PowerManager.ON_AFTER_RELEASE, String.valueOf(Log.i("alarm" , "wakeLock")));
        sCpuWakeLock.acquire();
    }
    static void releaseCpuLock() {
        Log.i("Releasing cpu wake lock" , "wakeLock");
        if (sCpuWakeLock != null) {
            sCpuWakeLock.release();
            sCpuWakeLock = null;
        }
    }
}
