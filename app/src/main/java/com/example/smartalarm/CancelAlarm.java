package com.example.smartalarm;
import android.annotation.SuppressLint;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import com.example.smartalarm.receiver.CancelAlarmReceiver;


public class CancelAlarm extends AppCompatActivity {

    /**
     *initialing the vibrator , ringtone and uri for ringtone
    */

    Bundle bundle;

    @RequiresApi(api = Build.VERSION_CODES.P)
    @SuppressLint({"LongLogTag", "ServiceCast"})
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.cancel_alarm);


        Button cancelb = findViewById(R.id.cancel_button);


        /**
         * getting intent from pending intent
         */
        Intent intent=getIntent();
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
            Intent cancelIntent = new Intent(getApplicationContext() , CancelAlarmReceiver.class);
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
            Log.i("on cancelb" , "successfully canceled alarm");
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
}
