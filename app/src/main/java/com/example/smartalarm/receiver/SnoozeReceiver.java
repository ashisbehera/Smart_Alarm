package com.example.smartalarm.receiver;

import android.app.AlarmManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.icu.util.TimeUnit;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.example.smartalarm.AlarmConstraints;
import com.example.smartalarm.AlarmReceiver;
import com.example.smartalarm.CancelAlarm;
import com.example.smartalarm.ControlAlarm;

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
        alarm.setSnooze_active(true);
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
