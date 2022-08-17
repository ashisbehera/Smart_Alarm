package com.coffeecoders.smartalarm.calender;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.provider.CalendarContract;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.GridView;

import com.coffeecoders.smartalarm.AlarmConstraints;
import com.coffeecoders.smartalarm.R;
import com.coffeecoders.smartalarm.calender.cal_rec_service.CalReceiver;
import com.coffeecoders.smartalarm.data.AlarmContract.AlarmEntry;
import com.coffeecoders.smartalarm.data.Alarm_Database;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
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
    private Alarm_Database database;
    private Map<String , Integer> monthMap;
    private String cal_table_name = "local_account";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calender);

        toolbar = (androidx.appcompat.widget.Toolbar) findViewById(R.id.my_toolbar);
        toolbar.setTitle("Calender Events");
        setSupportActionBar(toolbar);

        contentResolver = getContentResolver();
        alarmConstraints = new AlarmConstraints();

        monthMap = new HashMap<>();
        monthMap.put("Jan" , 1);monthMap.put("Feb" , 2);monthMap.put("Mar" , 3);
        monthMap.put("Apr" , 4);monthMap.put("May" , 5);monthMap.put("Jun" , 6);
        monthMap.put("Jul" , 7);monthMap.put("Aug" , 8);monthMap.put("Sep" , 9);
        monthMap.put("Oct" , 10);monthMap.put("Nov" , 11);monthMap.put("Dec" , 12);


//        button2 = findViewById(R.id.set_primary_cal);
//        button3 = findViewById(R.id.get_cal);
        cal_recycle_view = findViewById(R.id.calender_rec_view);
        database = new Alarm_Database(getApplicationContext());
        Log.e(TAG, "onCreate: " + "check" );
        readCalender_accounts();
        if(accNames_id_map.containsKey(sel_cal_acc_name)){
            getEvents(accNames_id_map.get(sel_cal_acc_name));
        }

        AlarmManager cal_alarmM = (AlarmManager) getSystemService(ALARM_SERVICE);
        Intent intent = new Intent(Calender_activity.this , CalReceiver.class);
        PendingIntent pendingIntent =  PendingIntent.getBroadcast(Calender_activity.this , 109 , intent , PendingIntent.FLAG_UPDATE_CURRENT);
        cal_alarmM.setInexactRepeating(AlarmManager.RTC_WAKEUP , System.currentTimeMillis()+30000 , 30000 ,pendingIntent);



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
        List<AlarmConstraints> events_list = new LinkedList<>();
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


                        String[] s_time_str = String.valueOf(begin).split("\\s+");
                        String[] e_time_str = String.valueOf(end).split("\\s+");
                        String s_day = s_time_str[0];
                        String s_month = s_time_str[1];
                        String s_date = s_time_str[2];
//                        Log.e(TAG, "getEvents: " + s_date+s_month );

                        Date date = new Date();
                        Calendar calendar = Calendar.getInstance();
                        calendar.setTime(date);
                        int dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);
                        int month = calendar.get(Calendar.MONTH)+1;
//                        Log.e(TAG, "getEvents: "+dayOfMonth+" "+month );
                        String s_time = s_time_str[3];
                        alarmConstraints.setStandardTime(s_time);
                        String s_standard_time =alarmConstraints.getStandardTime().toString();
                        String s_year = s_time_str[5];
                        String e_day = e_time_str[0];
                        String e_month = e_time_str[1];
                        String e_date = e_time_str[2];
                        String e_time = e_time_str[3];
                        alarmConstraints.setStandardTime(e_time);
                        String e_standard_time =alarmConstraints.getStandardTime().toString();
                        String e_year = e_time_str[5];
                        alarmConstraints.setLabel(title);
                        alarmConstraints.setAlarmTime(s_time);
                        alarmConstraints.setTtsString(title);
                        alarmConstraints.setEventDate(s_date+" "+monthMap.get(s_month));
                        alarmConstraints.setEvent_start_full_time(s_day+" "+s_month+" "+s_date+" , "+s_standard_time+" "+s_year);
                        alarmConstraints.setEvent_end_full_time(e_day+" "+e_month+" "+e_date+" , "+e_standard_time+" "+e_year);

                        Alarm_Database db = new Alarm_Database(getApplicationContext());
                        SQLiteDatabase sqldb = db.getWritableDatabase();

                        database.create_cal_table(cal_table_name , sqldb);
                        saveEventsInDataB(alarmConstraints , cal_table_name);

                    }
                    while(eventCursor.moveToNext());

                }
               events_list = database.getAlarmsFromDataBase(cal_table_name);
            }


//            Log.e(TAG, "onCreate: " + events_cl.getLabel() + events_cl.getAlarmTime());
            calenderAdapter = new CalenderAdapter(this, events_list);
            GridLayoutManager gridLayoutManager = new GridLayoutManager
                    (this, 2, GridLayoutManager.VERTICAL, false);
            cal_recycle_view.setLayoutManager(gridLayoutManager);
            cal_recycle_view.setAdapter(calenderAdapter);

    }

    private void saveEventsInDataB(AlarmConstraints cal_alarm , String cal_table_name){
        ContentValues values = new ContentValues();
        values.put(AlarmEntry.ALARM_NAME , cal_alarm.getLabel());
        values.put(AlarmEntry.ALARM_TIME , cal_alarm.getAlarmTime());
        values.put(AlarmEntry.TTS_STRING , cal_alarm.getTtsString());
        values.put(AlarmEntry.CAL_EVENT_DATE , cal_alarm.getEventDate());
        values.put(AlarmEntry.CAL_S_FULL_T , cal_alarm.getEvent_start_full_time());
        values.put(AlarmEntry.CAL_E_FULL_T , cal_alarm.getEvent_end_full_time());

        SQLiteDatabase myDb = database.getWritableDatabase();
        myDb.insert(cal_table_name , null , values);


    }

    @Override
    public void getCalAccName(String st) {
        sel_cal_acc_name = st;
        cal_table_name = "local_account";
        Log.e(TAG, "getCalAccName: "+sel_cal_acc_name);
        int delimiterIndex = sel_cal_acc_name.indexOf("@");
        if (delimiterIndex != -1) {
            cal_table_name = sel_cal_acc_name.substring(0 , delimiterIndex) ;// "name"
            if(Character.isDigit(cal_table_name.charAt(0))){
                cal_table_name = "a"+cal_table_name;
            }
        }
        getEvents(accNames_id_map.get(sel_cal_acc_name));
    }
}