package com.example.smartalarm.receiver;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.example.smartalarm.AlarmConstraints;
import com.example.smartalarm.ControlAlarm;

public class StopReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        AlarmConstraints alarm=(AlarmConstraints)intent.getBundleExtra
                (AlarmConstraints.ALARM_KEY).getParcelable(AlarmConstraints.ALARM_KEY);
        ControlAlarm controlAlarm = new ControlAlarm();
        /**
         * ir previously snooze activated then stop it because it is final cancel
         */
        alarm.setSnooze_active(false);
        controlAlarm.stopAlarm((AlarmConstraints)alarm , context);
        /**
         * sending broadcast for recycleView
         */
        Intent dataChangeIntent = new Intent("com.example.smartalarm.dataChangeListener");
        context.sendBroadcast(dataChangeIntent);
        // Dismiss notification
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        if (notificationManager != null) notificationManager.cancelAll();
    }
}
