package com.example.smartalarm;


import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;


import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class AlarmActivity extends AppCompatActivity {
    private AlarmConstraints cancelAlarm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.alarm_activity);

        Intent intent = getIntent();
        setTitle("Alarms");
        /**
         *will cancel the alarm
         */
        Button cancelbtn = findViewById(R.id.cancel);
        //floating button for @AddAlarm_Activity
        FloatingActionButton add_alarm_fab = findViewById(R.id.add_alarm_fb);

        cancelbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            cancelAlarm.cancelAlarm(getApplicationContext());
            }
        });
        add_alarm_fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent alarm_intent = new Intent
                        (AlarmActivity.this,AddAlarm_Activity.class );
                startActivity(alarm_intent);
            }
        });

    }



}
