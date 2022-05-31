package com.coffeecoders.smartalarm.calender;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.CalendarContract;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ListView;

import com.coffeecoders.smartalarm.R;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;

public class Calender_activity extends AppCompatActivity {
    private static final String TAG = "Calender_activity";
    GridView gridView;
    private Button button1 , button2 , button3;
    private ArrayList<Events> events_list = new ArrayList<>();
    private CalenderAdapter calenderAdapter;
    private RecyclerView cal_recycle_view;
    private HashSet<Integer> calendarIds = new HashSet<>();
    private HashSet<String> accountNames = new HashSet<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calender);

        button1 = findViewById(R.id.get_calender);
//        button2 = findViewById(R.id.set_primary_cal);
//        button3 = findViewById(R.id.get_cal);
        cal_recycle_view = findViewById(R.id.calender_rec_view);


        readCalender_events();
        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                getCalendars();;
                Calender_dialogBox calender_dialogBox =
                        new Calender_dialogBox(new ArrayList<>(accountNames));
                calender_dialogBox.show(getSupportFragmentManager() , "cal_dialog");
            }
        });
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
     * read events
     */
    public void readCalender_events(){

        ContentResolver contentResolver = getContentResolver();

        // Fetch a list of all calendars synced with the device, their display names and whether the

        Cursor cursor = contentResolver.query(Uri.parse("content://com.android.calendar/calendars"),
                (new String[] { "_id","account_name"}), null, null, null);



        try
        {
            System.out.println("Count="+cursor.getCount());
            if(cursor.getCount() > 0)
            {
                System.out.println("the control is just inside of the cursor.count loop");
                while (cursor.moveToNext()) {

                    String _id = cursor.getString(0);
                    String name = cursor.getString(1);
                    Log.e(TAG, "readCalender_events: account name :" + name);
                    Log.e(TAG, "readCalender_events: "+_id);
                    calendarIds.add(Integer.parseInt(_id));
                    accountNames.add(name);
                }
            }
        }
        catch(AssertionError ex)
        {
            ex.printStackTrace();
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }

        events_list.clear();
        // For each calendar, display all the events from the previous week to the end of next week.
        for (int id : calendarIds) {
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
                        newEvent.setEvent_s_time(String.valueOf(begin));
                        newEvent.setEvent_e_time(String.valueOf(end));

                        events_list.add(newEvent);
                    }
                    while(eventCursor.moveToNext());

                }
            }

        }

            calenderAdapter = new CalenderAdapter(this, events_list);
            GridLayoutManager gridLayoutManager = new GridLayoutManager
                    (this, 2, GridLayoutManager.VERTICAL, false);
            cal_recycle_view.setLayoutManager(gridLayoutManager);
            cal_recycle_view.setAdapter(calenderAdapter);

    }

}