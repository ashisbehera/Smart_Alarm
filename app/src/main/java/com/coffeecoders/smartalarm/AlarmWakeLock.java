package com.coffeecoders.smartalarm;

import android.content.Context;
import android.os.PowerManager;
import android.util.Log;

public class AlarmWakeLock {

    private static final String TAG = "AlarmWakeLock";

    private static PowerManager.WakeLock sCpuWakeLock;

    public static void acquireCpu(Context context) {
        if (sCpuWakeLock != null) {
            return;
        }

        PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
        sCpuWakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "smart_alarm:AlarmWakeLock");
        Log.e(TAG, "acquireCpu: going to acquire" );
        sCpuWakeLock.acquire();
        Log.e(TAG, "acquireCpu:  acquired" );
    }

    public static void acquireScreenCpu(Context context) {
        if (sCpuWakeLock != null) {
            Log.e(TAG, "acquireCpu: already acquire" );
            return;
        }
        PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
        sCpuWakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK
                                             | PowerManager.ACQUIRE_CAUSES_WAKEUP
                                             | PowerManager.ON_AFTER_RELEASE,
                                         "smart_alarm:AlarmWakeLock");
        Log.e(TAG, "acquireCpu: going to acquire" );
        sCpuWakeLock.acquire();
        Log.e(TAG, "acquireCpu:  acquired" );
    }

    public static void releaseCpu() {
        if (sCpuWakeLock != null) {
            sCpuWakeLock.release();
            sCpuWakeLock = null;
        }
    }
}
