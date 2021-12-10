package com.example.smartalarm;

import android.annotation.SuppressLint;
import android.app.KeyguardManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.PowerManager;
import android.util.Log;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.example.smartalarm.receiver.CancelAlarmReceiver;

import java.util.concurrent.TimeUnit;


public class CancelAlarm extends AppCompatActivity {

    /**
     * initialing the vibrator , ringtone and uri for ringtone
     */

    Bundle bundle;
    private PowerManager.WakeLock sCpuWakeLock;

    @RequiresApi(api = Build.VERSION_CODES.P)
    @SuppressLint({"LongLogTag", "ServiceCast"})
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // wake lock
        PowerManager pm = (PowerManager) this.getSystemService(Context.POWER_SERVICE);
        sCpuWakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK |
                PowerManager.ACQUIRE_CAUSES_WAKEUP | PowerManager.ON_AFTER_RELEASE,
                "SmartAlarm:cpu wake");
        sCpuWakeLock.acquire(10 * 60 * 1000L /*10 minutes*/);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1) {
            setShowWhenLocked(true);
            setTurnScreenOn(true);
            KeyguardManager keyguardManager = (KeyguardManager) getSystemService(Context.KEYGUARD_SERVICE);
            // show over lock screen
            if (keyguardManager != null)
                keyguardManager.requestDismissKeyguard(this, null);
        } else {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD | WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED | WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);
        }
        setContentView(R.layout.cancel_alarm);


        ImageView cancelb = findViewById(R.id.cancel_button);


        /**
         * getting intent from pending intent
         */
        Intent intent = getIntent();
        /**
         * getting bundle from from the intent
         */
        bundle = intent.getBundleExtra(AlarmConstraints.ALARM_KEY);

        /**
         * cancel the alarm
         */
        cancelb.setOnClickListener(view -> {
            /**
             * pending intent for cancelAlarmReceiver to stop the alarm.
             */
            Intent cancelIntent = new Intent(getApplicationContext(), CancelAlarmReceiver.class);
            cancelIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            cancelIntent.putExtra(AlarmConstraints.ALARM_KEY, bundle);
            cancelIntent.setAction("cancel alarm");
            PendingIntent cancelPendingIntent = PendingIntent.getBroadcast(getApplicationContext(), 0,
                    cancelIntent, PendingIntent.FLAG_UPDATE_CURRENT);
            try {
                cancelPendingIntent.send();
            } catch (PendingIntent.CanceledException e) {
                e.printStackTrace();
            }
            Log.i("on cancelb", "successfully canceled alarm");
            finish();
        });

    }


    @RequiresApi(api = Build.VERSION_CODES.P)
    @SuppressLint("MissingSuperCall")
    @Override
    public void onNewIntent(Intent intent) {
        // super.onNewIntent(intent);
        Log.i("onNewIntent", "beforeKeyCheck");

        if (intent.hasExtra(AlarmConstraints.ALARM_KEY)) {

            bundle = intent.getBundleExtra(AlarmConstraints.ALARM_KEY);
            if (bundle == null)
                return;

            Log.i("onNewIntent", "called");

        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        sCpuWakeLock.acquire(10 * 60 * 1000L /*10 minutes*/);
    }

    @Override
    protected void onStart() {
        super.onStart();
        sCpuWakeLock.acquire(10 * 60 * 1000L /*10 minutes*/);
    }
}
