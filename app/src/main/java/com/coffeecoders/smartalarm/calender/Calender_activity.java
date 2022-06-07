package com.coffeecoders.smartalarm.calender;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.CalendarContract;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ListView;
import android.widget.Toolbar;

import com.coffeecoders.smartalarm.AlarmConstraints;
import com.coffeecoders.smartalarm.R;
import com.coffeecoders.smartalarm.data.AlarmContract.AlarmEntry;
import com.coffeecoders.smartalarm.data.Alarm_Database;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

public class Calender_activity extends AppCompatActivity implements Calender_dialogBox.Get_Cal_accName {
    private static final String TAG = "Calender_activity";
    GridView gridView;
    private Button button1 , button2 , button3;
    private ArrayList<Events> events_list = new ArrayList<>();
    private CalenderAdapter calenderAdapter;
    private RecyclerView cal_recycle_view;
    private Map<String , Integer> accNames_id_map = new HashMap<>();
    private String sel_cal_acc_name = "local account";
    private ContentResolver contentResolver;
    private androidx.appcompat.widget.Toolbar toolbar;
    private AlarmConstraints alarmConstraints;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calender);

        toolbar = (androidx.appcompat.widget.Toolbar) findViewById(R.id.my_toolbar);
        toolbar.setTitle("Calender Events");
        setSupportActionBar(toolbar);

        contentResolver = getContentResolver();
        alarmConstraints = new AlarmConstraints();
//        button2 = findViewById(R.id.set_primary_cal);
//        button3 = findViewById(R.id.get_cal);
        cal_recycle_view = findViewById(R.id.calender_rec_view);

        Log.e(TAG, "onCreate: " + "check" );
        readCalender_accounts();
        if(accNames_id_map.containsKey(sel_cal_acc_name)){
            getEvents(accNames_id_map.get(sel_cal_acc_name));
        }

        Alarm_Database database = new Alarm_Database(getApplicationContext());
        List<AlarmConstraints> events_list = database.getAlarmsFromDataBase(AlarmEntry.CAL_EVENTS_TABLE_NAME);

        for (AlarmConstraints events_cl:events_list){
            Log.e(TAG, "onCreate: " + events_cl.getLabel() + events_cl.getAlarmTime());
        }
//
//        button2.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
////                getPrimaryCalendar();
//            }
//        });
//
//        button3.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//
//            }
//        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu ) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.choose_account, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.choose_acc:
                Calender_dialogBox calender_dialogBox =
                        new Calender_dialogBox(new ArrayList<>(accNames_id_map.keySet()) , sel_cal_acc_name);
                calender_dialogBox.show(getSupportFragmentManager() , "cal_dialog");
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

/**
     * read all calender
     */
//    public void getCalendars() {
//
//
//        // Projection array. Creating indices for this array instead of doing dynamic lookups improves performance.
//        final String[] EVENT_PROJECTION = new String[] {
//                CalendarContract.Calendars._ID,                           // 0
//                CalendarContract.Calendars.ACCOUNT_NAME,                  // 1
//                CalendarContract.Calendars.CALENDAR_DISPLAY_NAME,         // 2
//                CalendarContract.Calendars.OWNER_ACCOUNT                  // 3
//        };
//
//        // The indices for the projection array above.
//        final int PROJECTION_ID_INDEX = 0;
//        final int PROJECTION_ACCOUNT_NAME_INDEX = 1;
//        final int PROJECTION_DISPLAY_NAME_INDEX = 2;
//        final int PROJECTION_OWNER_ACCOUNT_INDEX = 3;
//
//
//        ContentResolver contentResolver = getContentResolver();
//        Cursor cur = contentResolver.query(CalendarContract.Calendars.CONTENT_URI, EVENT_PROJECTION, null, null, null);
//
//        events_list.clear();
//        Log.e("Calendar", "calender added:  ");
//        while (cur.moveToNext()) {
//            Log.e("Calendar", "calender added:  ");
//            long calID = 0;
//            String displayName = null;
//            String accountName = null;
//            String ownerName = null;
//
//            // Get the field values
//            calID = cur.getLong(PROJECTION_ID_INDEX);
//            displayName = cur.getString(PROJECTION_DISPLAY_NAME_INDEX);
//            accountName = cur.getString(PROJECTION_ACCOUNT_NAME_INDEX);
//            ownerName = cur.getString(PROJECTION_OWNER_ACCOUNT_INDEX);
//
//            String calendarInfo = String.format("Calendar ID: %s\nDisplay Name: %s\nAccount Name: %s\nOwner Name: %s", calID, displayName, accountName, ownerName);
//            calendarInfos.add(calendarInfo);
//        }
//
//        ArrayAdapter<String> stringArrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, android.R.id.text1, calendarInfos);
//        gridView.setAdapter(stringArrayAdapter);
//    }
//
//    /**
//     * read primary calender
//     */
//    public void getPrimaryCalendar() {
//
//
//        // Projection array. Creating indices for this array instead of doing dynamic lookups improves performance.
//        final String[] EVENT_PROJECTION = new String[] {
//                CalendarContract.Calendars._ID,                           // 0
//                CalendarContract.Calendars.ACCOUNT_NAME,                  // 1
//                CalendarContract.Calendars.CALENDAR_DISPLAY_NAME,         // 2
//                CalendarContract.Calendars.OWNER_ACCOUNT                  // 3
//        };
//
//        // The indices for the projection array above.
//        final int PROJECTION_ID_INDEX = 0;
//        final int PROJECTION_ACCOUNT_NAME_INDEX = 1;
//        final int PROJECTION_DISPLAY_NAME_INDEX = 2;
//        final int PROJECTION_OWNER_ACCOUNT_INDEX = 3;
//
//        ContentResolver contentResolver = getContentResolver();
//        String selection = CalendarContract.Calendars.VISIBLE + " = 1 AND "  + CalendarContract.Calendars.IS_PRIMARY + "=1";
//        Cursor cur = contentResolver.query(CalendarContract.Calendars.CONTENT_URI, EVENT_PROJECTION, selection, null, null);
//
//        events_list.clear();
//        while (cur.moveToNext()) {
//            long calID = 0;
//            String displayName = null;
//            String accountName = null;
//            String ownerName = null;
//
//            // Get the field values
//            calID = cur.getLong(PROJECTION_ID_INDEX);
//            displayName = cur.getString(PROJECTION_DISPLAY_NAME_INDEX);
//            accountName = cur.getString(PROJECTION_ACCOUNT_NAME_INDEX);
//            ownerName = cur.getString(PROJECTION_OWNER_ACCOUNT_INDEX);
//
//            String calendarInfo = String.format("Calendar ID: %s\nDisplay Name: %s\nAccount Name: %s\nOwner Name: %s", calID, displayName, accountName, ownerName);
//            calendarInfos.add(calendarInfo);
//        }
//        Log.e("list size :" , " "+ calendarInfos.size());
//        ArrayAdapter<String> stringArrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, android.R.id.text1, calendarInfos);
//        gridView.setAdapter(stringArrayAdapter);
//    }


    /**
     * read calender Accounts
     */
    public void readCalender_accounts() {

        // Fetch a list of all calendars synced with the device, their display names and whether the

        Cursor cursor = contentResolver.query(Uri.parse("content://com.android.calendar/calendars"),
                (new String[]{"_id", "account_name"}), null, null, null);


        try {
            System.out.println("Count=" + cursor.getCount());
            if (cursor.getCount() > 0) {
                System.out.println("the control is just inside of the cursor.count loop");
                while (cursor.moveToNext()) {

                    String _id = cursor.getString(0);
                    String name = cursor.getString(1);
                    Log.e(TAG, "readCalender_events: account name :" + name);
                    Log.e(TAG, "readCalender_events: " + _id);
                    accNames_id_map.put(name , Integer.parseInt(_id));
                }
            }
        } catch (AssertionError ex) {
            ex.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void getEvents(int id){
        events_list.clear();
        // For each calendar, display all the events from the previous week to the end of next week.

            Log.e(TAG, "readCalender_events: "+id );
            Uri.Builder builder = Uri.parse("content://com.android.calendar/instances/when").buildUpon();
            //Uri.Builder builder = Uri.parse("content://com.android.calendar/calendars").buildUpon();
            long now = new Date().getTime();

            ContentUris.appendId(builder, now - DateUtils.DAY_IN_MILLIS * 10000);
            ContentUris.appendId(builder, now + DateUtils.DAY_IN_MILLIS * 10000);

            Cursor eventCursor = contentResolver.query(builder.build(),
                    new String[]  { "title", "begin", "end", "allDay"},
                    CalendarContract.Instances.CALENDAR_ID+"="+ id,
                    null, "startDay ASC, startMinute ASC");

            Log.e(TAG, "readCalender_events: " +id);
            if(eventCursor.getCount()>0)
            {
                Log.e(TAG, "readCalender_events: inside " +id);
                if(eventCursor.moveToFirst())
                {
                    do
                    {
                        Object mbeg_date,beg_date,beg_time,end_date,end_time;

                        final String title = eventCursor.getString(0);
                        final Date begin = new Date(eventCursor.getLong(1));
                        final Date end = new Date(eventCursor.getLong(2));
                        final Boolean allDay = !eventCursor.getString(3).equals("0");

                        Events newEvent = new Events();
                        newEvent.setEvent_name(title);


                        String[] s_time_str = String.valueOf(begin).split("\\s+");
                        String[] e_time_str = String.valueOf(begin).split("\\s+");
                        String s_day = s_time_str[0];
                        String s_month = s_time_str[1];
                        String s_date = s_time_str[2];
                        String s_time = s_time_str[3];
                        alarmConstraints.setStandardTime(s_time);
                        String s_standard_time =alarmConstraints.getStandardTime().toString();
                        String s_year = s_time_str[5];
                        newEvent.setEvent_s_time(s_day+" "+s_month+" "+s_date+" , "+s_standard_time+" "+s_year);
                        String e_day = e_time_str[0];
                        String e_month = e_time_str[1];
                        String e_date = e_time_str[2];
                        String e_time = e_time_str[3];
                        alarmConstraints.setStandardTime(e_time);
                        String e_standard_time =alarmConstraints.getStandardTime().toString();
                        String e_year = e_time_str[5];
                        newEvent.setEvent_e_time(e_day+" "+e_month+" "+e_date+" , "+e_standard_time+" "+e_year);
                        events_list.add(newEvent);
                        alarmConstraints.setLabel(title);
                        alarmConstraints.setAlarmTime(s_time);
                        alarmConstraints.setTtsString(title);
                        saveEventsInDataB(alarmConstraints);

                    }
                    while(eventCursor.moveToNext());

                }
            }



            calenderAdapter = new CalenderAdapter(this, events_list);
            GridLayoutManager gridLayoutManager = new GridLayoutManager
                    (this, 2, GridLayoutManager.VERTICAL, false);
            cal_recycle_view.setLayoutManager(gridLayoutManager);
            cal_recycle_view.setAdapter(calenderAdapter);

    }

    private void saveEventsInDataB(AlarmConstraints cal_alarm){
        ContentValues values = new ContentValues();
        values.put(AlarmEntry.ALARM_NAME , cal_alarm.getLabel());
        values.put(AlarmEntry.ALARM_TIME , cal_alarm.getAlarmTime());
        values.put(AlarmEntry.TTS_STRING , cal_alarm.getTtsString());

        Uri newUri = getContentResolver().insert(AlarmEntry.CAL_EVENTS_CONTENT_URI, values);

    }

    @Override
    public void getCalAccName(String st) {
        sel_cal_acc_name = st;
        Log.e(TAG, "getCalAccName: "+sel_cal_acc_name);
        getEvents(accNames_id_map.get(sel_cal_acc_name));
    }
}