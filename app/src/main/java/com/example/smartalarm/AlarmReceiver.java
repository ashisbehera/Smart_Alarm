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

import java.io.File;
import java.util.ArrayList;

public class AlarmReceiver extends BroadcastReceiver {
    ArrayList<File> songs;
    int position;
    MediaPlayer mediaPlayer;


    @SuppressLint("LongLogTag")
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.i("AlarmReceiver", "Intent received");
        /**
         * getting bundle from intent
         */
        Bundle bundle = intent.getBundleExtra(AlarmConstraints.ALARM_KEY);
        /*
        Bundle bundleSong = intent.getExtras();
        songs = (ArrayList) bundleSong.getParcelableArrayList("songList");
        position = intent.getIntExtra("position", 0);
        Uri uri = Uri.parse(songs.get(position).toString());
        mediaPlayer = MediaPlayer.create(context, uri);
        mediaPlayer.start();
        */

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

        // AlarmWakeLock.acquireCpuWakeLock(context);
        /**
         * intent to cancelAlarm activity
         */
        Intent newIntent = new Intent(context, CancelAlarm.class);
        newIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        newIntent.putExtra(AlarmConstraints.ALARM_KEY, bundle);


//        Intent fullScreenIntent = new Intent(this, CallActivity.class);
//        PendingIntent fullScreenPendingIntent = PendingIntent.getActivity(context, 0,
//                fullScreenIntent, PendingIntent.FLAG_UPDATE_CURRENT);
//
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, newIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, "notification_alarm")
                .setSmallIcon(R.drawable.baseline_access_alarms_24)
                .setContentTitle("Smart Alarm Manager")
                .setContentText("Notification")
                .setAutoCancel(true)
                .setDefaults(NotificationCompat.DEFAULT_ALL)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setContentIntent(pendingIntent);
        NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(context);
        notificationManagerCompat.notify(1, builder.build());
//        context.startActivity(newIntent);

    }
}