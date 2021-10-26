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
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.TimePicker;
import android.content.Context;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.smartalarm.data.AlarmContract.AlarmEntry;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class AddAlarm_Activity extends AppCompatActivity implements
        LoaderManager.LoaderCallbacks<Cursor> {
    private static final int ALARM_LOADER = 0;

    private TimePicker timePicker;
    private FloatingActionButton set_alarm;
    private EditText alarmNameEditText;
    private Switch vibrateSwitch, snoozeSwitch;
    private AlarmConstraints newAlarm;
    private ScheduleService scheduleService;
    private LinearLayout ringtoneLayout;
    private final StringBuilder timeBuilder = new StringBuilder();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.addalarm_activity);
        setTitle("Add alarm");
        Intent i = getIntent();
        /**
         *set alarm button
         */

        alarmNameEditText = (EditText) findViewById(R.id.label_edt_txt);
        vibrateSwitch = findViewById(R.id.vibrate_switch);
        snoozeSwitch = findViewById(R.id.snooze_switch);
        set_alarm = findViewById(R.id.set_alarm);
        ringtoneLayout = findViewById(R.id.ringtoneLayout);
        TextView setRingtone = findViewById(R.id.setRingtone);
        /**
         *time picker
         */
        timePicker = (TimePicker) findViewById(R.id.timePicker);
        /**
         *initialing the alarmcontraints button
         */
        newAlarm = new AlarmConstraints();
        set_alarm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                newAlarm.setToggleOnOff(true);
                /**
                 *saving alarm to database
                 */
                saveAlarmToDataBase(newAlarm);
                Log.i("data saved to data base", "database created");
                newAlarm.setAlarmTime(getPickerTime());
                /**
                 * updating the service
                 */
                scheduleService.updateAlarmSchedule(getBaseContext());
                /**
                 *will return to the previous activity
                 */
                finish();
            }
        });
        // select ringtone if clicked on ringtone
        ringtoneLayout.setOnClickListener(view -> {
            Intent intent = new Intent(AddAlarm_Activity.this, Ringtone.class);
            startActivity(intent);
        });
        String songName = i.getStringExtra("currentMusic");
        setRingtone.setText(songName);
    }

//    @Override
//    protected void onPause() {
//        super.onPause();
//        ScheduleService.updateAlarmSchedule(getBaseContext());
//
//    }

    /**
     * saving the alarm to database using all the values
     *
     * @param alarm
     */
    private void saveAlarmToDataBase(AlarmConstraints alarm) {
        String alarmName = alarmNameEditText.getText().toString();
        String time = getPickerTime();
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

        ContentValues values = new ContentValues();
        values.put(AlarmEntry.ALARM_NAME, alarmName);
        values.put(AlarmEntry.ALARM_TIME, time);
        values.put(AlarmEntry.ALARM_VIBRATE, vibrate_on_off);
        values.put(AlarmEntry.ALARM_SNOOZE, snooze_on_off);
        values.put(AlarmEntry.ALARM_ACTIVE, alarm.isAlarmOn() ? 1 : 0);

        Uri newUri = getContentResolver().insert(AlarmEntry.CONTENT_URI, values);

    }

    /**
     * collect the time from the time picker
     */
    private String getPickerTime() {
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