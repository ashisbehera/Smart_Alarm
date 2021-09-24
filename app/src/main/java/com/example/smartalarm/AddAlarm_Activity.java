package com.example.smartalarm;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TimePicker;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.smartalarm.databinding.ActivityMainBinding;

public class AddAlarm_Activity extends AppCompatActivity {
    private TimePicker timePicker;
    private Button cancel_alarm;
    private Button set_alarm;
    private AlarmConstraints newAlarm;
    private StringBuilder timeBuilder = new StringBuilder();
    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.addalarm_activity);
        createNotificationChannel();
        setTitle("Add alarm");
        /**
         *set alarm button
         */
        set_alarm = findViewById(R.id.set_alarm);
        /**
         *time picker
         */
        timePicker = (TimePicker) findViewById(R.id.timePicker);/**
         *initialing the alarmcontraints button
         */
        newAlarm = new AlarmConstraints();
        set_alarm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                /**
                 *sending the alarm to setalarmtime method
                 */
                newAlarm.setAlarmTime(getpickerTime());
                newAlarm.scheduleAlarm(getApplicationContext());
                /**
                 *will return to the previous activity
                 */
                finish();
            }
        });
    }

    /**
     * collect the time from the time picker
     */
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
}