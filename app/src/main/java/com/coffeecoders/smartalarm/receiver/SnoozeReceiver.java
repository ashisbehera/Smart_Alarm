package com.coffeecoders.smartalarm.receiver;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.coffeecoders.smartalarm.AlarmConstraints;
import com.coffeecoders.smartalarm.ControlAlarm;

public class SnoozeReceiver extends BroadcastReceiver {
    
    private final static String TAG ="snoozeReceiver";
    @Override
    public void onReceive(Context context, Intent intent) {

        AlarmConstraints alarm=(AlarmConstraints)intent.getBundleExtra
                (AlarmConstraints.ALARM_KEY).getParcelable(AlarmConstraints.ALARM_KEY);
        ControlAlarm controlAlarm = new ControlAlarm();
        Log.i(TAG, "onReceive: before stoping the alarm");

        /**
         * active the snooze before cancelling the main alarm so that we won't turn off the
         * switch button
         */
        alarm.setTemp_snooze_active(true);
        /**
         * then stop the main alarm
         */
        controlAlarm.stopAlarm((AlarmConstraints)alarm , context);
        Log.i(TAG, "onReceive: after stoping the alarm");
        /**
         * after stopping the main alarm then snooze the alarm
         */
        alarm.scheduleSnoozeAlarm(context , alarm);

        Intent cancelAlarmActivityIntent = new Intent("com.example.smartalarm.cancelAlarm");
        context.sendBroadcast(cancelAlarmActivityIntent);
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        if (notificationManager != null) notificationManager.cancelAll();
    }
}
