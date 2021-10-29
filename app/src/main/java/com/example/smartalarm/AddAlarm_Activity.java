package com.example.smartalarm;

import android.app.LoaderManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.ContentValues;
import android.content.CursorLoader;
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
        LoaderManager.LoaderCallbacks<Cursor>{
    private static final int ALARM_LOADER_E = 0;

    private TimePicker timePicker;
    private FloatingActionButton set_alarm , delete_alarm;
    private EditText alarmNameEditText;
    private Switch vibrateSwitch,snoozeSwitch;
    private AlarmConstraints newAlarm;
    private ScheduleService scheduleService;
    private LinearLayout ringtoneLayout;
    Uri editUri ;
    private final StringBuilder timeBuilder = new StringBuilder();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.addalarm_activity);
        /**
         * get the intent form alarm activity
         */
        //
        Intent i  = getIntent();
        /** extract the data from the intent and save it to uri **/
        editUri = i.getData();

        delete_alarm = findViewById(R.id.delete_alarm);
        /** is the uri is null then it will be add alarm **/
        if (editUri == null) {
            setTitle("Add alarm");
            /** if it is add alarm activity then delete button will invisible **/
            delete_alarm.setVisibility(View.GONE);
        } else {
            /** if the uri has the alarm id then it will be update alarm **/
            setTitle("Edit alarm");
            getLoaderManager().initLoader(ALARM_LOADER_E, null, this);
        }

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

        /** will delete the alarm from the data base **/
        delete_alarm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (editUri!=null){
                    getContentResolver().delete(editUri, null, null);
                    ScheduleService.updateAlarmSchedule(getApplicationContext());
                    finish();
                }
            }
        });
        // select ringtone if clicked on ringtone
        ringtoneLayout.setOnClickListener(view -> {
            Intent intent = new Intent(AddAlarm_Activity.this, Ringtone.class);
            startActivity(intent);
        });
        // set ringtone name from the music
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
        values.put(AlarmEntry.ALARM_ACTIVE, alarm.isAlarmOn()?1:0);
        /** if the uri is null then insert new alarm to the database **/
        if (editUri==null){
            Uri newUri = getContentResolver().insert(AlarmEntry.CONTENT_URI, values);
        }else
            /**else update the data in the data base **/
            getContentResolver().update(editUri, values, null, null);


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

    /** in background thread cursor will project the specific row id **/
    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        String[] projection = {
                AlarmEntry._ID,
                AlarmEntry.ALARM_NAME,
                AlarmEntry.ALARM_TIME,
                AlarmEntry.ALARM_VIBRATE,
                AlarmEntry.ALARM_SNOOZE,
                AlarmEntry.ALARM_ACTIVE};


        return new CursorLoader(this,
                editUri, /** this row id will be project **/
                projection,
                null,
                null,
                null);
    }

    /** when cursor will fetch the data then it will update the data in specific area of the
     * add alarm activity in the background thread
     * @param loader
     * @param cursor
     */
    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        if (cursor == null || cursor.getCount() < 1) {
            return;
        }

        if (cursor.moveToFirst()) {
            int alarmNameCIn = cursor.getColumnIndex(AlarmEntry.ALARM_NAME);
            Log.i("name restored", "alarm name");
            int alarmTimeCIn = cursor.getColumnIndex(AlarmEntry.ALARM_TIME);
            int VibCIn = cursor.getColumnIndex(AlarmEntry.ALARM_VIBRATE);
            int SnzCIn = cursor.getColumnIndex(AlarmEntry.ALARM_SNOOZE);

            String alarmName = cursor.getString(alarmNameCIn);
            String alarmTime = cursor.getString(alarmTimeCIn);
            int vib = cursor.getInt(VibCIn);
            int snooze = cursor.getInt(SnzCIn);

            alarmNameEditText.setText(alarmName);
            String timeArr[] = alarmTime.split(":");
            timePicker.setCurrentHour(Integer.parseInt(timeArr[0]));
            timePicker.setCurrentMinute(Integer.parseInt(timeArr[1]));

            vibrateSwitch.setChecked(vib == 1 ? true : false);
            snoozeSwitch.setChecked(snooze == 1 ? true : false);
        }
    }

    /** at the time of return it will reset the each area of the add alarm activity **/
    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        alarmNameEditText.setText("");
        timePicker.setEnabled(false);
        vibrateSwitch.setChecked(false);
        snoozeSwitch.setChecked(false);
    }
}