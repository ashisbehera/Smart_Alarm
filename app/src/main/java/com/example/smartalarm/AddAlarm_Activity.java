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
    private Button cancel_alarm;
    private Button set_alarm;
    private EditText editHour;
    private EditText editMinute;
    PendingIntent pendingIntent;
    AlarmManager alarmManager;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.addalarm_activity);
        setTitle("Add alarm");

        editHour = findViewById(R.id.editHour);
        editMinute = findViewById(R.id.editMinute);
        set_alarm = findViewById(R.id.set_alarm);

        set_alarm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                long time;
                Calendar calendar = Calendar.getInstance();
                time = (calendar.getTimeInMillis() - (calendar.getTimeInMillis() % 60000));
                if (System.currentTimeMillis() > time) {
                    // setting time as AM and PM
                    if (calendar.AM_PM == 0)
                        time = time + (1000 * 60 * 60 * 12);
                    else
                        time = time + (1000 * 60 * 60 * 24);
                }
                Intent intent = new Intent(AddAlarm_Activity.this, AlarmReceiver.class);
                pendingIntent = PendingIntent.getBroadcast(AddAlarm_Activity.this, 0, intent, 0);
                alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, time, 10000, pendingIntent);
                /*int hour = Integer.parseInt(editHour.getText().toString());
                int minute = Integer.parseInt(editMinute.getText().toString());
                Intent intent = new Intent(AlarmClock.ACTION_SET_ALARM);
                intent.putExtra(AlarmClock.EXTRA_HOUR, hour);
                intent.putExtra(AlarmClock.EXTRA_MINUTES, minute);
                if (hour <= 24 && minute <= 60) {
                    startActivity(intent);
                }*/
            }
        });
    }
}
