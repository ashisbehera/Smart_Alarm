package com.example.smartalarm;

import android.app.LoaderManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.ContentValues;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.TimePicker;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.smartalarm.data.AlarmContract.AlarmEntry;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.File;
import java.util.ArrayList;

public class AddAlarm_Activity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {
    private static final int ALARM_LOADER = 0;
    private TimePicker timePicker;
    private FloatingActionButton set_alarm;
    private EditText alarmNameEditText;
    private Switch vibrateSwitch, snoozeSwitch;
    private AlarmConstraints newAlarm;
    private TextView ringtone_name;
    private final StringBuilder timeBuilder = new StringBuilder();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.addalarm_activity);
        createNotificationChannel();
        setTitle("Add alarm");
        alarmNameEditText = findViewById(R.id.label_edt_txt);
        vibrateSwitch = findViewById(R.id.vibrate_switch);
        snoozeSwitch = findViewById(R.id.snooze_switch);
        set_alarm = findViewById(R.id.set_alarm);
        timePicker = findViewById(R.id.timePicker);
        ringtone_name = findViewById(R.id.ringtone_name);
        ImageView dropDown = findViewById(R.id.drop_down);
        Intent intent = getIntent();
        newAlarm = new AlarmConstraints();
        // set alarm
        set_alarm.setOnClickListener(view -> {
            // sending the alarm to setAlarmTime method
            saveAlarmToDataBase();
            newAlarm.setAlarmTime(getpickerTime());
            newAlarm.scheduleAlarm(getApplicationContext());
            // return to previous activity
            Intent i = new Intent(AddAlarm_Activity.this, AlarmActivity.class);
            startActivity(i);
        });
        // choose ringtone
        dropDown.setOnClickListener(view -> {
            Intent i = new Intent(AddAlarm_Activity.this, Ringtone.class);
            startActivity(i);
        });
        String songName = intent.getStringExtra("currentMusic");
        ringtone_name.setText(songName);
    }

    private void saveAlarmToDataBase() {
        String alarmName = alarmNameEditText.getText().toString();
        String time = getpickerTime();
        int vibrate_on_off;
        boolean vibrate = vibrateSwitch.isChecked();
        if (vibrate) {
            vibrate_on_off = 1;
        } else vibrate_on_off = 0;
        int snooze_on_off;
        boolean snooze = snoozeSwitch.isChecked();
        if (snooze) {
            snooze_on_off = 1;
        } else snooze_on_off = 0;
        int alarm_on_off = 1;
        ContentValues values = new ContentValues();
        values.put(AlarmEntry.ALARM_NAME, alarmName);
        values.put(AlarmEntry.ALARM_TIME, time);
        values.put(AlarmEntry.ALARM_VIBRATE, vibrate_on_off);
        values.put(AlarmEntry.ALARM_SNOOZE, snooze_on_off);
        values.put(AlarmEntry.ALARM_ACTIVE, alarm_on_off);
        Uri newUri = getContentResolver().insert(AlarmEntry.CONTENT_URI, values);
    }

    // collect time from getPickerTime
    private String getpickerTime() {
        timeBuilder.append(String.valueOf(timePicker.getCurrentHour()));
        timeBuilder.append(":");
        String minute = timePicker.getCurrentMinute().toString();
        if (minute.length() == 1) {
            timeBuilder.append("0").append(minute);
        } else {
            timeBuilder.append(minute);
        }
        return timeBuilder.toString();
    }

    // notification on alarm
    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "Reminder channel";
            String description = "Channel for alarm manager";
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel channel = new NotificationChannel("notification_alarm", name, importance);
            channel.setDescription(description);
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        return null;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
    }
}