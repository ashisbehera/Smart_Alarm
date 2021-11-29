package com.example.smartalarm;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.media.MediaPlayer;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Vibrator;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.example.smartalarm.receiver.SnoozeReceiver;
import com.example.smartalarm.receiver.StopReceiver;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;

public class AlarmReceiver extends BroadcastReceiver {
    ArrayList<File> songs;
    int position;
    MediaPlayer mediaPlayer;


    @RequiresApi(api = Build.VERSION_CODES.P)
    @SuppressLint("LongLogTag")
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.i("AlarmReceiver", "Intent received");
        /**
         * getting bundle from intent
         */
        Bundle bundle = intent.getBundleExtra(AlarmConstraints.ALARM_KEY);
        AlarmConstraints alarm=(AlarmConstraints)intent.getBundleExtra
                (AlarmConstraints.ALARM_KEY).getParcelable(AlarmConstraints.ALARM_KEY);


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


        NotificationCompat.Builder builder =
                new NotificationCompat.Builder(context, "notification_alarm")
                .setSmallIcon(R.drawable.baseline_access_alarms_24)
                .setContentTitle("Smart Alarm Manager")
                .setContentText("Notification")
                .setAutoCancel(true)
                .setDefaults(NotificationCompat.DEFAULT_ALL)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .addAction(StopAction)
                .addAction(SnoozeAction)
                .setContentIntent(pendingIntent);
        NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(context);

//        context.startActivity(newIntent);
        try {
            controlAlarm.playAlarm(alarm , context.getApplicationContext());
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (notificationManagerCompat!=null)
            notificationManagerCompat.notify(1, builder.build());

    }
}