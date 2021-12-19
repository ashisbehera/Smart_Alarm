package com.example.smartalarm;


import android.annotation.SuppressLint;
import android.app.LoaderManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcelable;
import android.os.PersistableBundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.smartalarm.data.AlarmContract.AlarmEntry;
import com.example.smartalarm.data.AlarmDataProvider;
import com.example.smartalarm.data.Alarm_Database;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationBarView;
import com.tomerrosenfeld.customanalogclockview.CustomAnalogClock;

import java.util.ArrayList;
import java.util.LinkedList;

public class AlarmActivity extends AppCompatActivity{

    private final static String TAG = "AlarmActivity";
    private static final int ALARM_LOADER = 0;
    AlarmAdapter aAdapter;
    Parcelable state;
    RecyclerView alarmRecycleView;
    LinkedList<AlarmConstraints> alarms;
    Alarm_Database alarmDatabase;
    BroadcastReceiver broadcastReceiver;
    BottomNavigationView bottomNavigationView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.alarm_activity);
        setTitle("Alarms");
//        finish();
//        overridePendingTransition(0, 0);
//        startActivity(getIntent());
//        overridePendingTransition(0, 0);

        createNotificationChannel();

        bottomNavigationView= findViewById(R.id.bottom_nv);
        bottomNavigationView.setSelectedItemId(R.id.alarm_nv_bt);
        bottomNavigationView.setSelected(true);
        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()){
                    case R.id.stopWatch_nv_bt:
                        Intent intent1 = new Intent(AlarmActivity.this, Stopwatch.class);
                        startActivity(intent1);
                        return true;
                    case R.id.clock_nv_bt:
                        Intent intent2 = new Intent(AlarmActivity.this, WorldClock.class);
                        startActivity(intent2);
                        return true;
                }
                return true;
            }
        });




        /**floating button for @AddAlarm_Activity **/
        FloatingActionButton add_alarm_fab = findViewById(R.id.add_alarm_fb);

        add_alarm_fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent alarm_intent = new Intent
                        (AlarmActivity.this,AddAlarm_Activity.class );
                alarm_intent.setAction("from alarmActivity new");
                startActivity(alarm_intent);
            }
        });

        alarmDatabase= Alarm_Database.getInstance(getApplicationContext());
        alarms = (LinkedList<AlarmConstraints>) alarmDatabase.getAlarmsFromDataBase();
        alarmRecycleView = (RecyclerView) findViewById(R.id.list);

        aAdapter = new AlarmAdapter(alarms , getApplicationContext());

        alarmRecycleView.setLayoutManager(new LinearLayoutManager(this));

        alarmRecycleView.setAdapter(aAdapter);
        aAdapter.notifyDataSetChanged();
        /**
         * receiver for recycleView when stopped notification
         */
        broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (intent.getAction() == "com.example.smartalarm.dataChangeListener"){
                    aAdapter.notifyDataSetChanged();
                }
            }
        };
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


    /** will inflate menu in the activity **/
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_alarmlist, menu);
        return true;
    }

    /** will delete all the alarm form the database **/
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            case R.id.delete_all_alarms:
                deleteAllPets();
                return true;
            case R.id.about_menu:
                Intent about_intent = new Intent(AlarmActivity.this, About.class);
                startActivity(about_intent);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void deleteAllPets() {
        int rowsDeleted = getContentResolver().delete
                (AlarmEntry.CONTENT_URI, null, null);
        Log.v("AlarmActivity", rowsDeleted + " all alarms are deleted ");

        ScheduleService.updateAlarmSchedule(getApplicationContext());
        alarmRecycleView.removeAllViewsInLayout();
        alarms.clear();
        aAdapter.notifyDataSetChanged();
    }

    @Override
    protected void onStart() {
        super.onStart();
        IntentFilter intentFilter = new IntentFilter("com.example.smartalarm.dataChangeListener");
        registerReceiver(broadcastReceiver , intentFilter);
        aAdapter.notifyDataSetChanged();
        bottomNavigationView.setSelectedItemId(R.id.alarm_nv_bt);
        bottomNavigationView.setSelected(true);
    }

    @Override
    protected void onPause() {
        super.onPause();
        aAdapter.notifyDataSetChanged();
    }

    @Override
    protected void onResume() {
        super.onResume();
        aAdapter.notifyDataSetChanged();
        bottomNavigationView.setSelectedItemId(R.id.alarm_nv_bt);
        bottomNavigationView.setSelected(true);
    }

    @Override
    protected void onStop() {
        super.onStop();
        unregisterReceiver(broadcastReceiver);
    }
}