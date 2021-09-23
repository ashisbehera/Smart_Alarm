package com.example.smartalarm;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintsChangedListener;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        createNotificationChannel();
        ImageView alarm_img = findViewById(R.id.alarm_img_v);
        ImageView world_clock_img = findViewById(R.id.world_clock);
        alarm_img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent alarm_intent = new Intent
                        (MainActivity.this, AlarmActivity.class);
                startActivity(alarm_intent);
            }
        });
        world_clock_img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent edit_alarm_intent = new Intent(MainActivity.this, AddAlarm_Activity.class);
                startActivity(edit_alarm_intent);
            }
        });
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT>Build.VERSION_CODES.O){
            CharSequence name = "Testing Alarm";
            String description = "Alarm";
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel channel = new NotificationChannel("notification_alarm",name,importance);
            channel.setDescription(description);

            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }
}