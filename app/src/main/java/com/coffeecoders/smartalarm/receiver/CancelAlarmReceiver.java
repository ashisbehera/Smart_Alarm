package com.coffeecoders.smartalarm.receiver;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.coffeecoders.smartalarm.AlarmConstraints;
import com.coffeecoders.smartalarm.ControlAlarm;

public class CancelAlarmReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        AlarmConstraints alarm=(AlarmConstraints)intent.getBundleExtra
                (AlarmConstraints.ALARM_KEY).getParcelable(AlarmConstraints.ALARM_KEY);
        ControlAlarm controlAlarm = new ControlAlarm();
        alarm.setTemp_snooze_active(false);
        /**
         * will stop the alarm from cancel alarm activity
         */
        controlAlarm.stopAlarm((AlarmConstraints)alarm , context);
        /**
         * then cancel the notification
         */
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        if (notificationManager != null) notificationManager.cancelAll();
    }
}
