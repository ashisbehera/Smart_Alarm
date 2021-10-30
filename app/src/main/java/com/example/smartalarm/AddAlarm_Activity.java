package com.example.smartalarm;

import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.TimePicker;

import androidx.appcompat.app.AppCompatActivity;

import com.example.smartalarm.data.AlarmContract.AlarmEntry;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.Locale;

public class AddAlarm_Activity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {
    // data members
    private TimePicker timePicker;
    private FloatingActionButton set_alarm;
    private FloatingActionButton delete_alarm;
    private EditText alarmNameEditText;
    private Switch vibrateSwitch;
    private Switch snoozeSwitch;
    private AlarmConstraints newAlarm;
    private ScheduleService scheduleService;
    private LinearLayout ringtoneLayout;
    private TextToSpeech textToSpeech;
    // just to check whether tts is working
    private TextView buttonTTS;
    private String label;
    Bundle savedState = new Bundle();
    Uri editUri;

    // final data members
    private static final int ALARM_LOADER_E = 0;
    private final StringBuilder timeBuilder = new StringBuilder();
    public static final String SHARED_PREFS = "sharedPrefs";
    public static final String LABEL = "label";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.addalarm_activity);

        // find ids
        delete_alarm = findViewById(R.id.delete_alarm);
        alarmNameEditText = findViewById(R.id.label_edt_txt);
        vibrateSwitch = findViewById(R.id.vibrate_switch);
        snoozeSwitch = findViewById(R.id.snooze_switch);
        set_alarm = findViewById(R.id.set_alarm);
        ringtoneLayout = findViewById(R.id.ringtoneLayout);
        // just to check whether tts is working
        buttonTTS = findViewById(R.id.label);
        TextView setRingtone = findViewById(R.id.setRingtone);
        timePicker = (TimePicker) findViewById(R.id.timePicker);

        // get the intent from alarm activity
        Intent i = getIntent();

        // extract the data from the intent and save it to uri
        editUri = i.getData();

        // if the uri is null(visiting the activity first time), then it'll be add alarm or else it'll be edit alarm
        if (editUri == null) {
            setTitle("Add alarm");
            // if it is add alarm activity, then delete button will be invisible
            delete_alarm.setVisibility(View.GONE);
        } else {
            setTitle("Edit alarm");
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
                // clear the stored data upon logout
                SharedPreferences preferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
                SharedPreferences.Editor editor = preferences.edit();
                editor.clear();
                editor.apply();
                // return to previous activity
                finish();
            }
        });

        /** will delete the alarm from the data base **/
        delete_alarm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (editUri != null) {
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
            startActivity(intent);
        });

        // set ringtone name from the music
        String songName = i.getStringExtra("currentMusic");
        setRingtone.setText(songName);

        // text-to-speech
        textToSpeech = new TextToSpeech(this, status -> {
            // check if working properly
            if (status == TextToSpeech.SUCCESS) {
                // set language
                int result = textToSpeech.setLanguage(Locale.US);
                // check if the language is available
                if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                    Log.e("TTS", "Language not supported");
                } else {
                    // if TTS is working then we can use the button
                    buttonTTS.setEnabled(true);
                }
            } else {
                // if not working
                Log.e("TTS", "Initialization failed");
            }
        });

        // button for TTS
        buttonTTS.setOnClickListener(view -> speak());

        // calling loadData() and updateViews() to restore and set the previously saved data
        loadData();
        updateViews();
    }

    // convert text to speech
    private void speak() {
        String text = alarmNameEditText.getText().toString();
        // QUEUE_FLUSH cancels the current text to speak the new one
        textToSpeech.speak(text, TextToSpeech.QUEUE_FLUSH, null);
    }

    // save the entered information
    public void saveData() {
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(LABEL, alarmNameEditText.getText().toString());
        editor.apply();
    }

    // load the saved information
    public void loadData() {
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        // if nothing is entered then the string will be blank by default
        label = sharedPreferences.getString(LABEL, "");
    }

    // update the saved information to the correct place
    public void updateViews() {
        alarmNameEditText.setText(label);
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
        /** if the uri is null then insert new alarm to the database **/
        if (editUri == null) {
            Uri newUri = getContentResolver().insert(AlarmEntry.CONTENT_URI, values);
        } else
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

    /**
     * in background thread cursor will project the specific row id
     **/
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

    /**
     * when cursor will fetch the data then it will update the data in specific area of the
     * add alarm activity in the background thread
     *
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

    /**
     * at the time of return it will reset the each area of the add alarm activity
     **/
    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        alarmNameEditText.setText("");
        timePicker.setEnabled(false);
        vibrateSwitch.setChecked(false);
        snoozeSwitch.setChecked(false);
    }
    /*
    @Override
    protected void onPause() {
        super.onPause();
        String label = alarmNameEditText.getText().toString();
        savedState.putString("label", label);
    }

    @Override
    protected void onResume() {
        super.onResume();
        alarmNameEditText.setText(savedState.getString("label"), label);
    }
    */
    /*
    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        EditText alarmName = findViewById(R.id.label_edt_txt);
        String label = alarmName.getText().toString();
        outState.putString("labelName", label);
    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        String storedLabel = savedInstanceState.getString("labelName");
        EditText labelEditText = findViewById(R.id.label_edt_txt);
        labelEditText.setText(storedLabel);
    }
     */
}