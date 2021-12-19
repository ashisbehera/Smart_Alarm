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
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.airbnb.lottie.LottieAnimationView;
import com.example.smartalarm.databinding.CancelAlarmBinding;
import com.example.smartalarm.receiver.CancelAlarmReceiver;
import com.example.smartalarm.receiver.SnoozeReceiver;

import java.util.concurrent.TimeUnit;


public class CancelAlarm extends AppCompatActivity {
    private final static String TAG = "CancelAlarm";

    /**
     * initialing the vibrator , ringtone and uri for ringtone
     */

    private Bundle bundle;
    private LottieAnimationView cancelAnimation;
    private TextView alarmTimeTxt , alarmLblTxt , snoozeTxt;

    @SuppressLint({"LongLogTag", "ServiceCast"})
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().addFlags(
                WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD
                | WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
        );
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            KeyguardManager keyguardManager = (KeyguardManager) this.getSystemService(KEYGUARD_SERVICE);
            keyguardManager.requestDismissKeyguard(this, new KeyguardManager.KeyguardDismissCallback() {});

        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1) {
            setShowWhenLocked(true);
            setTurnScreenOn(true);


        } else {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON |
                                 WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD |
                                 WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED |
                                 WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);
        }
        try
        {
            this.getSupportActionBar().hide();
        }
        catch (NullPointerException e){}
        setContentView(R.layout.cancel_alarm);



        cancelAnimation = findViewById(R.id.animationView);
        alarmTimeTxt = findViewById(R.id.time_txt_cancel_ac);
        alarmLblTxt = findViewById(R.id.label_txt_cancel_ac);
        snoozeTxt = findViewById(R.id.snooze_txt_cancel_ac);


        /**
         * getting intent from pending intent
         */
        Intent intent = getIntent();
        /**
         * getting bundle from from the intent
         */
        bundle = intent.getBundleExtra(AlarmConstraints.ALARM_KEY);
        AlarmConstraints alarm=(AlarmConstraints)intent.getBundleExtra
                (AlarmConstraints.ALARM_KEY).getParcelable(AlarmConstraints.ALARM_KEY);
        alarm.setStandardTime(alarm.getAlarmTime());
        StringBuilder standardTime = alarm.getStandardTime();

        /**
         * cancel the alarm
         */

        alarmTimeTxt.setText(standardTime);
        alarmLblTxt.setText(alarm.getLabel());

        cancelAnimation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
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
            }
        });

        snoozeTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent snoozeIntent = new Intent(getApplicationContext(), SnoozeReceiver.class);
                snoozeIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                snoozeIntent.putExtra(AlarmConstraints.ALARM_KEY, bundle);
                snoozeIntent.setAction("snooze Alarm");
                PendingIntent snoozePendingIntent = PendingIntent.getBroadcast(getApplicationContext(), 0,
                        snoozeIntent, PendingIntent.FLAG_UPDATE_CURRENT);
                try {
                    snoozePendingIntent.send();
                } catch (PendingIntent.CanceledException e) {
                    e.printStackTrace();
                }
                Log.i(TAG, "onClick: successfully snoozed");
                finish();
            }
        });

    }



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
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
