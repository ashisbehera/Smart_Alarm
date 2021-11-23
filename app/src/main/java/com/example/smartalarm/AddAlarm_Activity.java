package com.example.smartalarm;

import android.app.LoaderManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
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

import java.util.Calendar;
import java.util.GregorianCalendar;

public class AddAlarm_Activity extends AppCompatActivity implements
        LoaderManager.LoaderCallbacks<Cursor>{
    private static final int ALARM_LOADER_E = 0;
    private String label;
    private String speechText;
    private int timeMinute;
    private int timeHour;
    private String timeAMPM;
    //private TimePicker pickedTime;
    private String pickedTime;
    private TimePicker timePicker;
    private FloatingActionButton set_alarm , delete_alarm;
    private EditText alarmNameEditText , ttsEditText;
    TextView setRingtone;
    private Switch vibrateSwitch,snoozeSwitch;
    private CheckBox tts_check_bx , ringtone_check_bx;
    private AlarmConstraints newAlarm;
    private ScheduleService scheduleService;
    private LinearLayout ringtoneLayout;
    private String ringtoneUri="";
    private String ringtoneName="";
    Uri editUri ;
    Intent i;
    public static final String SHARED_PREFS = "sharedPrefs";
    public static final String LABEL = "label";
    public static final String SPEECHTEXT = "speech_text";
    public static final String TIME_M = "minute";
    public static final String TIME_H = "hour";
    public static final String TIME_AM_PM = "AM_PM";
    private final StringBuilder timeBuilder = new StringBuilder();
    // private StringBuilder pickedTime = new StringBuilder();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.addalarm_activity);
        /**
         *set alarm button
         */

        alarmNameEditText = (EditText) findViewById(R.id.label_edt_txt);
        ttsEditText = findViewById(R.id.tts_edt_txt);

        vibrateSwitch = findViewById(R.id.vibrate_switch);
        tts_check_bx = findViewById(R.id.tts_ch_bt);
        ringtone_check_bx = findViewById(R.id.ringtone_ch_bt);
        snoozeSwitch = findViewById(R.id.snooze_switch);

        set_alarm = findViewById(R.id.set_alarm);
        ringtoneLayout = findViewById(R.id.ringtoneLayout);
        setRingtone = findViewById(R.id.setRingtone);

        /**
         *time picker
         */
        timePicker = (TimePicker) findViewById(R.id.timePicker);
        /**
         * get the intent form alarm activity
         */
        i  = getIntent();
        /** extract the data from the intent and save it to uri **/
        editUri = i.getData();

        delete_alarm = findViewById(R.id.delete_alarm);
        /** is the uri is null then it will be add alarm **/
        if (editUri == null) {
            setTitle("Add alarm");
            if(i.getExtras()!=null){
                // set ringtone name from the extra
                ringtoneName = i.getStringExtra("ringtoneName");
                setRingtone.setText(ringtoneName);
                // set ringtone uri from the extra
                ringtoneUri = i.getStringExtra("ringtoneUri");
                loadData();
                updateViews();
            }
            /** if it is add alarm activity then delete button will invisible **/
            delete_alarm.setVisibility(View.GONE);
        } else {
            /** if the uri has the alarm id then it will be update alarm **/
            setTitle("Edit alarm");
            /**if there are some extra that means we are coming from ringtone activity **/
            if(i.getExtras()!=null){
                // set ringtone name from the extra
                ringtoneName = i.getStringExtra("ringtoneName");
                setRingtone.setText(ringtoneName);
                // set ringtone uri from the extra
                ringtoneUri = i.getStringExtra("ringtoneUri");
                loadData();
                updateViews();
            }

            getLoaderManager().initLoader(ALARM_LOADER_E, null, this);
        }


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
                SharedPreferences preferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
                SharedPreferences.Editor editor = preferences.edit();
                editor.clear();
                editor.apply();
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
            // save the data before moving onto the ringtone activity
            saveData();
            Intent intent = new Intent(AddAlarm_Activity.this, Ringtone.class);
            intent.setData(editUri);
            //finish();
            startActivity(intent);
        });

    }

    // save the entered information
    public void saveData() {
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(LABEL, alarmNameEditText.getText().toString());
        editor.putString(SPEECHTEXT, ttsEditText.getText().toString());
        editor.putInt(TIME_H, timePicker.getCurrentHour());
        editor.putInt(TIME_M, timePicker.getCurrentMinute());
        editor.apply();
    }

    // load the saved information
    public void loadData() {
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        Calendar timeNow = Calendar.getInstance();
        // if nothing is entered then the string will be blank by default
        label = sharedPreferences.getString(LABEL, "");
        speechText = sharedPreferences.getString(SPEECHTEXT, "");
        timeHour = sharedPreferences.getInt(TIME_H, timeNow.get(Calendar.HOUR));
        timeMinute = sharedPreferences.getInt(TIME_M,timeNow.get(Calendar.MINUTE));
    }

    // update the saved information to the correct place
    public void updateViews() {
        alarmNameEditText.setText(label);
        ttsEditText.setText(speechText);
        timePicker.setCurrentHour(timeHour);
        timePicker.setCurrentMinute(timeMinute);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        SharedPreferences preferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.clear();
        editor.apply();
        Intent intent = new Intent(AddAlarm_Activity.this, AlarmActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        finish();
        startActivity(intent);
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
        String ringtoneNameToShow = setRingtone.getText().toString();
        /** tts string from edit text **/
        String ttsString = ttsEditText.getText().toString();
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
        boolean tts_ch_box = tts_check_bx.isChecked();
        boolean ring_ch_box = ringtone_check_bx.isChecked();

        ContentValues values = new ContentValues();
        values.put(AlarmEntry.ALARM_NAME, alarmName);
        /** insert into data base **/
        values.put(AlarmEntry.TTS_STRING, ttsString);
        /**if we are coming from alarmActivity and don't want to edit ringtone the
         * put in database
         */
        if(i.getExtras()!=null)
            values.put(AlarmEntry.RINGTONE_STRING , ringtoneUri);
        values.put(AlarmEntry.ALARM_RINGTONE_NAME , ringtoneNameToShow);
        values.put(AlarmEntry.ALARM_TIME, time);
        values.put(AlarmEntry.ALARM_VIBRATE, vibrate_on_off);
        values.put(AlarmEntry.ALARM_SNOOZE, snooze_on_off);
        values.put(AlarmEntry.TTS_ACTIVE, tts_ch_box?1:0);
        values.put(AlarmEntry.RINGTONE_ACTIVE, ring_ch_box?1:0);
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
                AlarmEntry.TTS_STRING,
                /** this string will save in the alarm table **/
                AlarmEntry.RINGTONE_STRING,
                AlarmEntry.ALARM_RINGTONE_NAME,
                AlarmEntry.ALARM_TIME,
                AlarmEntry.ALARM_VIBRATE,
                AlarmEntry.ALARM_SNOOZE,
                AlarmEntry.TTS_ACTIVE,
                AlarmEntry.RINGTONE_ACTIVE,
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
       if(i.getData()!=null && i.getExtras()==null) { /** if coming from alarm_activity for editing **/
           if (cursor.moveToFirst()) {
               int alarmNameCIn = cursor.getColumnIndex(AlarmEntry.ALARM_NAME);
               Log.i("name restored", "alarm name");
               int ttsStringCIn = cursor.getColumnIndex(AlarmEntry.TTS_STRING);
               // int ringtoneStringCIn = cursor.getColumnIndex(AlarmEntry.RINGTONE_STRING);
               int alarmRingtoneNameCIn = cursor.getColumnIndex(AlarmEntry.ALARM_RINGTONE_NAME);
               int alarmTimeCIn = cursor.getColumnIndex(AlarmEntry.ALARM_TIME);
               int VibCIn = cursor.getColumnIndex(AlarmEntry.ALARM_VIBRATE);
               int SnzCIn = cursor.getColumnIndex(AlarmEntry.ALARM_SNOOZE);
               int ttsCIn = cursor.getColumnIndex(AlarmEntry.TTS_ACTIVE);
               int ringCIn = cursor.getColumnIndex(AlarmEntry.RINGTONE_ACTIVE);

               String alarmName = cursor.getString(alarmNameCIn);
               String ttsString = cursor.getString(ttsStringCIn);
               String alarmTime = cursor.getString(alarmTimeCIn);
               //  String ringtoneString = cursor.getString(ringtoneStringCIn);
               String alarmRiName = cursor.getString(alarmRingtoneNameCIn);
               int vib = cursor.getInt(VibCIn);
               int snooze = cursor.getInt(SnzCIn);
               int tts_active = cursor.getInt(ttsCIn);
               int ringtone_active = cursor.getInt(ringCIn);

               alarmNameEditText.setText(alarmName);
               ttsEditText.setText(ttsString);
               /** if we are not coming from ringtone activity then setText as database **/
               if (i.getExtras() == null)
                   setRingtone.setText(alarmRiName);
               String timeArr[] = alarmTime.split(":");
               timePicker.setCurrentHour(Integer.parseInt(timeArr[0]));
               timePicker.setCurrentMinute(Integer.parseInt(timeArr[1]));

               vibrateSwitch.setChecked(vib == 1 ? true : false);
               snoozeSwitch.setChecked(snooze == 1 ? true : false);
               tts_check_bx.setChecked(tts_active == 1 ? true : false);
               ringtone_check_bx.setChecked(ringtone_active == 1 ? true : false);
           }
       }

    }

    /** at the time of return it will reset the each area of the add alarm activity **/
    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        alarmNameEditText.setText("");
        ttsEditText.setText("");
        setRingtone.setText("");
        timePicker.setEnabled(false);
        vibrateSwitch.setChecked(false);
        snoozeSwitch.setChecked(false);
        tts_check_bx.setChecked(false);
        ringtone_check_bx.setChecked(false);
    }
}