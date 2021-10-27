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
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Vibrator;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

public class AlarmReceiver extends BroadcastReceiver {


    @SuppressLint("LongLogTag")
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.i("AlarmReceiver","Intent received");
        /**
         * getting bundle from intent
         */
        Bundle bundle= intent.getBundleExtra(AlarmConstraints.ALARM_KEY);
        Log.i("bundle ","bundle received from receiver");
        /**
         * checking if bundle or intent extra null
         */
        if(bundle==null || bundle.getParcelable(AlarmConstraints.ALARM_KEY)==null )
        {
            Log.i("receiver","bundle and intent extra is null");
            return;
        }
        else
        {
            Log.i("receiver","found the bundle and intent extra with value");
        }

       // AlarmWakeLock.acquireCpuWakeLock(context);
        /**
         * intent to cancelAlarm activity
         */
        Intent newIntent=new Intent(context,CancelAlarm.class);
        newIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        newIntent.putExtra(AlarmConstraints.ALARM_KEY,bundle);

        context.startActivity(newIntent);
    }
}