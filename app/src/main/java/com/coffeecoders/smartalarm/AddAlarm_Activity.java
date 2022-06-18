package com.coffeecoders.smartalarm;

import android.annotation.SuppressLint;
import android.app.Fragment;
import android.app.LoaderManager;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentActivity;

import com.coffeecoders.smartalarm.data.AlarmContract.AlarmEntry;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.TreeMap;

public class AddAlarm_Activity extends AppCompatActivity implements
        LoaderManager.LoaderCallbacks<Cursor> {
    private static final String TAG = "AddAlarm_Activity";
    AlarmAdapter adapter;
    private static final int ALARM_LOADER_E = 0;
    private String label;
    private String speechText;
    private boolean tts_checked;
    private boolean ringtone_checked;
    private boolean vibrate_checked;
    private boolean snooze_checked;
    private int timeMinute;
    private int timeHour;
    private String time;
    /**
     * list will contain repeat day list
     */
    private ArrayList<String> dayArrayList;
    private TimePicker timePicker;
    private FloatingActionButton set_alarm , delete_alarm;
    private EditText alarmNameEditText , ttsEditText;
    TextView setRingtone;
    private ImageView repeatAlarmImg;
    private Switch vibrateSwitch,snoozeSwitch;
    private Button customBtn;
    private CheckBox tts_check_bx , ringtone_check_bx;
    private AlarmConstraints newAlarm;
    private LinearLayout ringtoneLayout;
    private String ringtoneUri="";
    private String ringtoneName="";
    private Uri editUri ;
    private Uri defaultRingtoneUri;
    private Intent i;
    public static final String SHARED_PREFS = "sharedPrefs";
    public static final String LABEL = "label";
    public static final String SPEECHTEXT = "speech_text";
    public static final String TIME_M = "minute";
    public static final String TIME_H = "hour";
    public static final String TTS_CHECKBOX = "tts_checkbox";
    public static final String RINGTONE_CHECKBOX = "ringtone_checkbox";
    public static final String VIBRATE_TOGGLE = "vibrate_toggle";
    public static final String SNOOZE_TOGGLE = "snooze_toggle";
    public static final String TIME_AM_PM = "AM_PM";
    private final StringBuilder timeBuilder = new StringBuilder();
    private final StringBuilder daysString = new StringBuilder();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.addalarm_activity);
        /**
         *set alarm button
         */

        alarmNameEditText = findViewById(R.id.label_edt_txt);
        ttsEditText = findViewById(R.id.tts_edt_txt);
        repeatAlarmImg = findViewById(R.id.repeatClickImg);
        vibrateSwitch = findViewById(R.id.vibrate_switch);
        tts_check_bx = findViewById(R.id.tts_ch_bt);
        ringtone_check_bx = findViewById(R.id.ringtone_ch_bt);
        snoozeSwitch = findViewById(R.id.snooze_switch);
        set_alarm = findViewById(R.id.set_alarm);
        ringtoneLayout = findViewById(R.id.ringtoneLayout);
        setRingtone = findViewById(R.id.setRingtone);
//        customBtn = findViewById(R.id.customizeBtn);



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
            if(i.getAction() == "from alarmActivity new"){

                dayArrayList = new ArrayList<String>();
            }
            else if(i.getAction() == "from ringtoneActivity"){
                /**get the alarm back from ringtone activity **/
                dayArrayList = i.getStringArrayListExtra("arrayList");
                /***set ringtone name from the extra*/
                ringtoneName = i.getStringExtra("ringtoneName");
                setRingtone.setText(ringtoneName);
                /**set ringtone uri from the extra **/
                ringtoneUri = i.getStringExtra("ringtoneUri");
                loadData();
                updateViews();
            }
            else if(i.getAction() == "from repeatDayActivity"){
                loadData();
                updateViews();
                /**collect the list from repeatDay activity **/
                dayArrayList = i.getStringArrayListExtra("arrayList");
                ringtoneName = i.getStringExtra("ringtoneName");
                setRingtone.setText(ringtoneName);
                /**set ringtone uri from the extra **/
                ringtoneUri = i.getStringExtra("ringtoneUri");
            }
            else if(i.getAction() == "from calenderActivity"){
                dayArrayList = new ArrayList<String>();
            }
            /** if it is add alarm activity then delete button will invisible **/
            delete_alarm.setVisibility(View.GONE);
        } else {
            /** if the uri has the alarm id then it will be update alarm **/
            setTitle("Edit alarm");
            /**if there are some extra that means we are coming from ringtone activity **/
            if(i.getAction() == "from alarmActivity"){
                dayArrayList = new ArrayList<String>();
            }
            else if (i.getAction()=="from repeatDayActivity"){
                loadData();
                updateViews();
                dayArrayList = i.getStringArrayListExtra("arrayList");
                ringtoneName = i.getStringExtra("ringtoneName");
                setRingtone.setText(ringtoneName);
                /**set ringtone uri from the extra **/
                ringtoneUri = i.getStringExtra("ringtoneUri");
            }
            else if(i.getAction() == "from ringtoneActivity"){
                dayArrayList = i.getStringArrayListExtra("arrayList");
                /**set ringtone name from the extra **/
                ringtoneName = i.getStringExtra("ringtoneName");
                setRingtone.setText(ringtoneName);
                /** set ringtone uri from the extra **/
                ringtoneUri = i.getStringExtra("ringtoneUri");
                loadData();
                updateViews();
            }else if(i.getAction() == "from calenderActivity"){
                dayArrayList = new ArrayList<String>();
            }

            getLoaderManager().initLoader(ALARM_LOADER_E, null, this);

        }
        if (ringtoneName=="" && ringtoneUri=="") {
            defaultRingtoneUri = RingtoneManager.getActualDefaultRingtoneUri(getApplicationContext(), RingtoneManager.TYPE_ALARM);
            ringtoneUri = defaultRingtoneUri.toString();
            android.media.Ringtone ring = RingtoneManager.getRingtone(getApplicationContext(), defaultRingtoneUri);
            ringtoneName = ring.getTitle(getApplicationContext());
            if (setRingtone.getText().toString().isEmpty()) {
                setRingtone.setText(ringtoneName);
            }
        }

        if (!dayArrayList.isEmpty()){
            repeatAlarmImg.setImageResource(R.drawable.baseline_replay_circle_filled_24);
        }

//        customBtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Intent MapIntent = new Intent(AddAlarm_Activity.this, MapsViewActivity.class);
//                startActivity(MapIntent);
//            }
//        });
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

                /**
                 * updating the service
                 */
                getBaseContext().startService(new Intent(getBaseContext(), ScheduleService.class));

                SharedPreferences preferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
                SharedPreferences.Editor editor = preferences.edit();
                editor.clear();
                editor.apply();

                TreeMap<Integer, String> treeMapDays = new TreeMap<>();
                if(!dayArrayList.isEmpty()) {
                    newAlarm.setRepeating(true);
                    if (dayArrayList.contains("Sunday"))
                        treeMapDays.put(0, "sunday");
                    if (dayArrayList.contains("Monday"))
                        treeMapDays.put(1, "monday");
                    if (dayArrayList.contains("Tuesday"))
                        treeMapDays.put(2, "tuesday");
                    if (dayArrayList.contains("Wednesday"))
                        treeMapDays.put(3, "wednesday");
                    if (dayArrayList.contains("Thursday"))
                        treeMapDays.put(4, "thursday");
                    if (dayArrayList.contains("Friday"))
                        treeMapDays.put(5, "friday");
                    if (dayArrayList.contains("Saturday"))
                        treeMapDays.put(6, "saturday");
                }else
                    newAlarm.setRepeating(false);
                newAlarm.setRepeatDayMap(treeMapDays);
                long milisec = newAlarm.convertTimeInMS(time,
                        newAlarm.isRepeating() , newAlarm.getRepeatDayMap());

                Toast.makeText(getApplicationContext(), "alarm will ring in : "+
                                String.valueOf(newAlarm.getDurationBreakdown(milisec)),
                        Toast.LENGTH_SHORT).show();
                /**
                 *will return to the previous activity
                 */
                finish();
            }
        });

        repeatAlarmImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveData();
                Intent intent = new Intent(AddAlarm_Activity.this, RepeatDayActivity.class);
                /** will take the dayArrayList so that we can show or save in RepeatDayActivity **/
                intent.putStringArrayListExtra("arrayList" , dayArrayList);
                intent.putExtra("ringtoneName",ringtoneName);
                intent.putExtra("ringtoneUri",ringtoneUri);
                intent.setData(editUri);
                startActivity(intent);

            }
        });

        /** will delete the alarm from the data base **/
        delete_alarm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                long id = ContentUris.parseId(editUri);
                newAlarm.setPKeyDB((int) id);
                Log.i(TAG, "delete_onClick: id to be delete " + id);
                newAlarm.cancelAlarm(getApplicationContext() ,newAlarm );
                getContentResolver().delete(editUri, null, null);

                getBaseContext().startService(new Intent(getBaseContext(), ScheduleService.class));
                finish();

            }
        });
        /**select ringtone if clicked on ringtone **/
        ringtoneLayout.setOnClickListener(view -> {
            /** save the data before moving onto the ringtone activity **/
            saveData();
            Intent intent = new Intent(AddAlarm_Activity.this, Ringtone.class);
            intent.putStringArrayListExtra("arrayList" , dayArrayList);
            intent.setData(editUri);
            //finish();
            startActivity(intent);
        });

    }

    /** save the entered information **/
    public void saveData() {
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(LABEL, alarmNameEditText.getText().toString());
        editor.putString(SPEECHTEXT, ttsEditText.getText().toString());
        editor.putInt(TIME_H, timePicker.getCurrentHour());
        editor.putInt(TIME_M, timePicker.getCurrentMinute());
        editor.putBoolean(TTS_CHECKBOX, tts_check_bx.isChecked());
        editor.putBoolean(RINGTONE_CHECKBOX, ringtone_check_bx.isChecked());
        editor.putBoolean(VIBRATE_TOGGLE, vibrateSwitch.isChecked());
        editor.putBoolean(SNOOZE_TOGGLE, snoozeSwitch.isChecked());
        editor.apply();
    }

    /** load the saved information **/
    public void loadData() {
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        Calendar timeNow = Calendar.getInstance();
        // if nothing is entered then the string will be blank by default
        label = sharedPreferences.getString(LABEL, "");
        speechText = sharedPreferences.getString(SPEECHTEXT, "");
        timeHour = sharedPreferences.getInt(TIME_H, timeNow.get(Calendar.HOUR));
        timeMinute = sharedPreferences.getInt(TIME_M,timeNow.get(Calendar.MINUTE));
        tts_checked = sharedPreferences.getBoolean(TTS_CHECKBOX, false);
        ringtone_checked = sharedPreferences.getBoolean(RINGTONE_CHECKBOX, false);
        vibrate_checked = sharedPreferences.getBoolean(VIBRATE_TOGGLE, false);
        snooze_checked = sharedPreferences.getBoolean(SNOOZE_TOGGLE, false);
    }

    /** update the saved information to the correct place **/
    public void updateViews() {
        alarmNameEditText.setText(label);
        ttsEditText.setText(speechText);
        timePicker.setCurrentHour(timeHour);
        timePicker.setCurrentMinute(timeMinute);
        tts_check_bx.setChecked(tts_checked);
        ringtone_check_bx.setChecked(ringtone_checked);
        vibrateSwitch.setChecked(vibrate_checked);
        snoozeSwitch.setChecked(snooze_checked);
    }


    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        SharedPreferences preferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.clear();
        editor.apply();
//        Intent intent = new Intent(AddAlarm_Activity.this, AlarmActivity.class);
//        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        finish();
//        startActivity(intent);
    }


    /**
     * saving the alarm to database using all the values
     * @param alarm
     */
    private void saveAlarmToDataBase(AlarmConstraints alarm) {
        String alarmName = alarmNameEditText.getText().toString();
        if (alarmName.isEmpty())
            alarmName = "Alarm";
        time = getPickerTime();
        boolean vibrate = vibrateSwitch.isChecked();
        boolean snooze = snoozeSwitch.isChecked();
        boolean tts_ch_box = tts_check_bx.isChecked();
        boolean ring_ch_box = ringtone_check_bx.isChecked();

        ContentValues values = new ContentValues();
        values.put(AlarmEntry.ALARM_NAME, alarmName);
        /** insert into data base **/
        values.put(AlarmEntry.TTS_STRING, ttsEditText.getText().toString());

        /**
         * convert arrayList to String to save in the data base
         */
        if(!dayArrayList.isEmpty()) {
            for (int i = 0; i < 7 && i<dayArrayList.size(); i++) {
                daysString.append(dayArrayList.get(i));
                daysString.append(",");
            }
            daysString.deleteCharAt(daysString.length()-1);
            values.put(AlarmEntry.ALARM_REPEAT_DAYS , daysString.toString());
            values.put(AlarmEntry.IS_REPEATING, 1 );
        }else{
            values.put(AlarmEntry.ALARM_REPEAT_DAYS , daysString.toString());
            /**if arrayList is empty then No repeating so set  IS_REPEATING 0 **/
            values.put(AlarmEntry.IS_REPEATING, 0);
        }

        Log.i(TAG, "saveAlarmToDataBase: "+dayArrayList.size());

        values.put(AlarmEntry.RINGTONE_STRING , ringtoneUri);
        values.put(AlarmEntry.ALARM_RINGTONE_NAME , setRingtone.getText().toString());
        values.put(AlarmEntry.ALARM_TIME, time);
        values.put(AlarmEntry.ALARM_VIBRATE, vibrate?1:0);
        values.put(AlarmEntry.ALARM_SNOOZE, snooze?1:0);
        values.put(AlarmEntry.TTS_ACTIVE, tts_ch_box?1:0);
        values.put(AlarmEntry.RINGTONE_ACTIVE, ring_ch_box?1:0);
        values.put(AlarmEntry.ALARM_ACTIVE, 1);

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
                AlarmEntry.ALARM_ACTIVE,
                AlarmEntry.ALARM_REPEAT_DAYS};


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
    @SuppressLint("Range")
    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        if (cursor == null || cursor.getCount() < 1) {
            return;
        }
        if(i.getAction() != "from ringtoneActivity" && i.getAction() != "from repeatDayActivity"){ /** if coming from alarm_activity for editing **/
            if (cursor.moveToFirst()) {

                vibrateSwitch.setChecked(cursor.getInt(cursor.getColumnIndex(AlarmEntry.ALARM_VIBRATE)) == 1 ? true : false);
                snoozeSwitch.setChecked(cursor.getInt(cursor.getColumnIndex(AlarmEntry.ALARM_SNOOZE)) == 1 ? true : false);
                tts_check_bx.setChecked(cursor.getInt(cursor.getColumnIndex(AlarmEntry.TTS_ACTIVE)) == 1 ? true : false);
                ringtone_check_bx.setChecked(cursor.getInt(cursor.getColumnIndex(AlarmEntry.RINGTONE_ACTIVE)) == 1 ? true : false);
                // int ringtoneStringCIn = cursor.getColumnIndex(AlarmEntry.RINGTONE_STRING);

                String alarmName = cursor.getString(cursor.getColumnIndex(AlarmEntry.ALARM_NAME));
                String ttsString = cursor.getString(cursor.getColumnIndex(AlarmEntry.TTS_STRING));
                String alarmTime = cursor.getString(cursor.getColumnIndex(AlarmEntry.ALARM_TIME));
                /**
                 * get RepeatDays from the database and save them to arrayList.
                 */
                String alarmRiName = cursor.getString(cursor.getColumnIndex(AlarmEntry.ALARM_RINGTONE_NAME));
                ringtoneName = alarmRiName;
                ringtoneUri = cursor.getString(cursor.getColumnIndex(AlarmEntry.RINGTONE_STRING));
                String RepeatDaysString = cursor.getString(cursor.getColumnIndex(AlarmEntry.ALARM_REPEAT_DAYS));
                Log.i("TAG", "onLoadFinished: "+RepeatDaysString);
                if (RepeatDaysString!=null && !RepeatDaysString.isEmpty()) {
                    String[] array = RepeatDaysString.split(",");
                    for (int i = 0; i < array.length; i++) {
                        dayArrayList.add(array[i]);
                    }
                    repeatAlarmImg.setImageResource(R.drawable.baseline_replay_circle_filled_24);
                }


                alarmNameEditText.setText(alarmName);
                ttsEditText.setText(ttsString);
                /** if we are not coming from ringtone activity then setText as database **/
                if (i.getExtras() == null)
                    setRingtone.setText(alarmRiName);
                String timeArr[] = alarmTime.split(":");
                timePicker.setCurrentHour(Integer.parseInt(timeArr[0]));
                timePicker.setCurrentMinute(Integer.parseInt(timeArr[1]));


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