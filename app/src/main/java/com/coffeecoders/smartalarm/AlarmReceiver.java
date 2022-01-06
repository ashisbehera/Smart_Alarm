package com.coffeecoders.smartalarm;

import android.annotation.SuppressLint;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.PowerManager;
import android.util.Log;
import android.widget.RemoteViews;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.coffeecoders.smartalarm.receiver.SnoozeReceiver;
import com.coffeecoders.smartalarm.receiver.StopReceiver;

import java.io.IOException;

public class AlarmReceiver extends BroadcastReceiver {

    private static final String TAG = "AlarmReceiver";
    private RemoteViews largeRemoteViews , smallRemoteView;
    private PowerManager powerManager;
    @SuppressLint("LongLogTag")
    @Override
    public void onReceive(Context context, Intent intent) {
        try {
            AlarmWakeLock.acquireCpu(context);
            Log.e(TAG, "onReceive: wakelock acquired");
        }catch (Exception e){
            e.printStackTrace();
        }

        Log.i("AlarmReceiver", "Intent received");
        /**
         * getting bundle from intent
         */
//        Bundle bundle = intent.getBundleExtra(AlarmConstraints.ALARM_KEY);
        AlarmConstraints alarm=(AlarmConstraints)intent.getBundleExtra
                (AlarmConstraints.ALARM_KEY).getParcelable(AlarmConstraints.ALARM_KEY);
        alarm.isPlayed = false;
        alarm.isVibrated = false;
        Bundle bundle=new Bundle();
        bundle.putParcelable(alarm.ALARM_KEY,alarm);


        Log.i("bundle ", "bundle received from receiver");
        /**
         * checking if bundle or intent extra null
         */
        if (bundle == null || bundle.getParcelable(AlarmConstraints.ALARM_KEY) == null) {
            Log.i("receiver", "bundle and intent extra is null");
            return;
        } else {
            Log.i("receiver", "found the bundle and intent extra with value");
        }

        /**
         * intent to cancelAlarm activity
         */

        ControlAlarm controlAlarm = new ControlAlarm();

        /**
         * intent for cancel activity
         */
        Intent newIntent = new Intent(context, CancelAlarm.class);
        newIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        newIntent.putExtra(AlarmConstraints.ALARM_KEY, bundle);
        PendingIntent pendingIntent = PendingIntent.getActivity
                (context, 0, newIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        /**
         * intent for StopReceiver
         */
        Intent stopIntent = new Intent(context , StopReceiver.class);
        stopIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        stopIntent.putExtra(AlarmConstraints.ALARM_KEY, bundle);
        stopIntent.setAction("stop alarm");
        PendingIntent stopPendingIntent = PendingIntent.getBroadcast(context, 0,
                stopIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        /**
         * intent for SnoozeReceiver
         */
        Intent snoozeIntent = new Intent(context , SnoozeReceiver.class);
        snoozeIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        snoozeIntent.putExtra(AlarmConstraints.ALARM_KEY, bundle);
        snoozeIntent.setAction("snooze alarm");
        PendingIntent snoozePendingIntent = PendingIntent.getBroadcast(context, 0,
                snoozeIntent, PendingIntent.FLAG_UPDATE_CURRENT);


        NotificationCompat.Action StopAction = new NotificationCompat.Action.Builder
                        (null,
                        context.getString(R.string.Stop_alarm_notification),
                        stopPendingIntent).build();
        NotificationCompat.Action SnoozeAction = new NotificationCompat.Action.Builder
                        (null,
                        context.getString(R.string.Snooze_alarm_notification),
                        snoozePendingIntent).build();

        largeRemoteViews = new RemoteViews("com.coffeecoders.smartalarm" ,
                                          R.layout.notification_layout);


            largeRemoteViews.setTextViewText(R.id.noti_alarm_name , alarm.getLabel());
            alarm.setStandardTime(alarm.getAlarmTime());
            StringBuilder standardTime = alarm.getStandardTime();
            largeRemoteViews.setTextViewText(R.id.noti_alarm_time , standardTime);
            largeRemoteViews.setOnClickPendingIntent(R.id.noti_snooze_button , snoozePendingIntent);
            largeRemoteViews.setOnClickPendingIntent(R.id.noti_stop_button , stopPendingIntent);

            smallRemoteView = new RemoteViews("com.coffeecoders.smartalarm" ,
                    R.layout.small_notification_layout);
            smallRemoteView.setTextViewText(R.id.small_noti_alarm_name , alarm.getLabel());
            smallRemoteView.setTextViewText(R.id.small_noti_alarm_time , standardTime);
            smallRemoteView.setOnClickPendingIntent(R.id.small_noti_snooze_button , snoozePendingIntent);
            smallRemoteView.setOnClickPendingIntent(R.id.small_noti_stop_button , stopPendingIntent);

        if(android.os.Build.VERSION.SDK_INT >29){
            NotificationCompat.Builder builder =
                    new NotificationCompat.Builder(context, "notification_alarm")
                            .setSmallIcon(R.drawable.baseline_access_alarms_24)
//                .setContentTitle("Smart Alarm Manager")
//                .setContentText("Notification")
                            .setAutoCancel(true)
                            .setDefaults(NotificationCompat.DEFAULT_ALL)
                            .setPriority(NotificationCompat.PRIORITY_HIGH)
                            .setCategory(NotificationCompat.CATEGORY_CALL)
                            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                            .setLocalOnly(true)
                            .setOngoing(true)
//                .addAction(SnoozeAction)
//                .addAction(StopAction)
                            .setCustomContentView(largeRemoteViews)
                            .setCustomBigContentView(largeRemoteViews)
                            .setFullScreenIntent(pendingIntent , true)
                            .setContentIntent(pendingIntent);
            NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(context);

            if (notificationManagerCompat!=null)
                notificationManagerCompat.notify(1, builder.build());
        }else {
            NotificationCompat.Builder builder =
                    new NotificationCompat.Builder(context, "notification_alarm")
                            .setSmallIcon(R.drawable.baseline_access_alarms_24)
                            .setContentTitle(standardTime)
                            .setContentText(alarm.getLabel())
                            .setAutoCancel(true)
                            .setDefaults(NotificationCompat.DEFAULT_ALL)
                            .setPriority(NotificationCompat.PRIORITY_HIGH)
                            .setCategory(NotificationCompat.CATEGORY_CALL)
                            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                            .setLocalOnly(true)
                            .setOngoing(true)
                            .addAction(SnoozeAction)
                            .addAction(StopAction)
                            .setFullScreenIntent(pendingIntent , true)
                            .setContentIntent(pendingIntent);
            NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(context);

            if (notificationManagerCompat!=null)
                notificationManagerCompat.notify(1, builder.build());
        }


        try {
            controlAlarm.playAlarm(alarm , context.getApplicationContext());
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            AlarmWakeLock.releaseCpu();
            Log.e(TAG, "onReceive: wakelock released" );
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}