package com.coffeecoders.smartalarm;

import android.Manifest;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.os.Build;
import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

import java.util.List;

import pub.devrel.easypermissions.EasyPermissions;

public class MainActivity extends AppCompatActivity implements EasyPermissions.PermissionCallbacks {
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
        request_permission();
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

    @Override
    public void onPermissionsGranted(int requestCode, @NonNull List<String> perms) {

    }

    @Override
    public void onPermissionsDenied(int requestCode, @NonNull List<String> perms) {

    }

    public void request_permission(){
        String[] permission = new String[]{
            Manifest.permission.READ_EXTERNAL_STORAGE , Manifest.permission.READ_CALENDAR
        };

        if(EasyPermissions.hasPermissions(this , permission)){

        }else{
            EasyPermissions.requestPermissions(this , "we need permission"
                    , 144 , permission);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        EasyPermissions.onRequestPermissionsResult(144 , permissions ,
                grantResults , this);
    }
}