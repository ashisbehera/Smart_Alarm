package com.example.smartalarm;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import com.tomerrosenfeld.customanalogclockview.CustomAnalogClock;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        createNotificationChannel();
        // clock
        CustomAnalogClock customAnalogClock = (CustomAnalogClock) findViewById(R.id.analog_clock);
        // three menu buttons
        ImageView alarm_img = findViewById(R.id.alarm_img_v);
        ImageView world_clock_img = findViewById(R.id.world_clock);
        ImageView stopwatch_img = findViewById(R.id.stopw_img_v);
        // customize clock
        customAnalogClock.setAutoUpdate(true);
        customAnalogClock.setScale(1.25f);
        // alarm clock
        alarm_img.setOnClickListener(v -> {
            Intent alarm_intent = new Intent(MainActivity.this, AlarmActivity.class);
            startActivity(alarm_intent);
        });
        // world clock
        world_clock_img.setOnClickListener(view -> {
            Intent worldClockIntent = new Intent(MainActivity.this, world_clock.class);
            startActivity(worldClockIntent);
        });
        // stopwatch
        stopwatch_img.setOnClickListener(view -> {
            Intent stopwatch_intent = new Intent(MainActivity.this, Stopwatch.class);
            startActivity(stopwatch_intent);
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

    // inflate the menu
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main_activity, menu);
        return true;
    }

    // for hamburger menu
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.about_menu) {
            Intent about_intent = new Intent(MainActivity.this, About.class);
            startActivity(about_intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}