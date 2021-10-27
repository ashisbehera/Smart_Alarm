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

import com.tomerrosenfeld.customanalogclockview.CustomAnalogClock;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // createNotificationChannel();
        ImageView alarm_img = findViewById(R.id.alarm_img_v);
        ImageView world_clock_img = findViewById(R.id.world_clock);
        CustomAnalogClock customAnalogClock = (CustomAnalogClock) findViewById(R.id.analog_clock);
        customAnalogClock.setAutoUpdate(true);
        customAnalogClock.setScale(1.25f);
        alarm_img.setOnClickListener(v -> {
            Intent alarm_intent = new Intent(MainActivity.this, AlarmActivity.class);
            startActivity(alarm_intent);
        });
        world_clock_img.setOnClickListener(view -> {
            Intent edit_alarm_intent = new Intent(MainActivity.this, Ringtone.class);
            startActivity(edit_alarm_intent);
        });
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.O) {
            CharSequence name = "Testing Alarm";
            String description = "Alarm";
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel channel = new NotificationChannel("notification_alarm", name, importance);
            channel.setDescription(description);

            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }
}