package com.example.smartalarm;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.provider.AlarmClock;
import android.text.NoCopySpan;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;

import java.util.Calendar;

public class AddAlarm_Activity extends AppCompatActivity {
    private TimePicker timePicker;
    private Button cancel_alarm;
    private Button set_alarm;
    private AlarmConstraints newAlarm;
    private StringBuilder timeBuilder=new StringBuilder();


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.addalarm_activity);
        setTitle("Add alarm");
        /**
         *set alarm button
         */
        set_alarm = findViewById(R.id.set_alarm);
        /**
         *time picker
         */
        timePicker=(TimePicker) findViewById(R.id.timePicker);/**
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
     *collect the time from the time picker
     */
    private String getpickerTime()
    {
        timeBuilder.append(String.valueOf(timePicker.getCurrentHour()));
        timeBuilder.append(":");
        String minute=timePicker.getCurrentMinute().toString();
        if(minute.length()==1)
        {
            timeBuilder.append("0").append(minute);
        }
        else
        {
            timeBuilder.append(minute);
        }

        return timeBuilder.toString();
    }
}
