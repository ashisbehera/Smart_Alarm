package com.example.smartalarm;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.view.Menu;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.tomerrosenfeld.customanalogclockview.CustomAnalogClock;

public class MainActivity extends AppCompatActivity {
    /**
     * abandoned
     */
    BottomNavigationView bottomNavigationView;
    private Toolbar toolbar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        createNotificationChannel();

        getSupportFragmentManager().beginTransaction().
                replace(R.id.fragment_container , new Alarm_fragment()).commit();
        bottomNavigationView= findViewById(R.id.bottom_nv);
//        bottomNavigationView.setSelectedItemId(R.id.home_nv_bt);
        bottomNavigationView.setSelected(true);
        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                Fragment selectedFragment = null;
                switch (item.getItemId()){
                    case R.id.stopWatch_nv_bt:
                        selectedFragment = new StopWatch_Fragment();
                        break;
                    case R.id.clock_nv_bt:
                        selectedFragment = new WorldClock_Fragment();
                        break;
                    case R.id.alarm_nv_bt:
                       selectedFragment = new Alarm_fragment();
                       break;
                }
                getSupportFragmentManager().beginTransaction().
                        replace(R.id.fragment_container , selectedFragment).commit();
               return true;
            }
        });


    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT > 26) {
            CharSequence name = "Testing Alarm";
            String description = "Alarm";
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel channel = new NotificationChannel("notification_alarm", name, importance);
            channel.setDescription(description);
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }


    @Override
    protected void onPause() {
        super.onPause();

    }

    @Override
    protected void onStart() {
        super.onStart();
//        bottomNavigationView.setSelectedItemId(R.id.home_nv_bt);
        bottomNavigationView.setSelected(true);
    }

    @Override
    protected void onResume() {
        super.onResume();
//        bottomNavigationView.setSelectedItemId(R.id.home_nv_bt);
        bottomNavigationView.setSelected(true);
    }

    @Override
    public void onBackPressed() {
        moveTaskToBack(true);
    }
}