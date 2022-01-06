package com.coffeecoders.smartalarm.receiver;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.coffeecoders.smartalarm.AlarmConstraints;
import com.coffeecoders.smartalarm.ControlAlarm;

public class StopReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        AlarmConstraints alarm=(AlarmConstraints)intent.getBundleExtra
                (AlarmConstraints.ALARM_KEY).getParcelable(AlarmConstraints.ALARM_KEY);
        ControlAlarm controlAlarm = new ControlAlarm();
        /**
         * ir previously snooze activated then stop it because it is final cancel
         */
        alarm.setTemp_snooze_active(false);
        controlAlarm.stopAlarm((AlarmConstraints)alarm , context);
        /**
         * sending broadcast for recycleView
         */
        Intent dataChangeIntent = new Intent("com.example.smartalarm.dataChangeListener");
        context.sendBroadcast(dataChangeIntent);
        // Dismiss notification

        Intent cancelAlarmActivityIntent = new Intent("com.example.smartalarm.cancelAlarm");
        context.sendBroadcast(cancelAlarmActivityIntent);
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        if (notificationManager != null) notificationManager.cancelAll();
    }
}
