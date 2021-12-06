package com.example.smartalarm;


import android.annotation.SuppressLint;
import android.app.LoaderManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcelable;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.TextView;


import androidx.appcompat.app.AppCompatActivity;

import com.example.smartalarm.data.AlarmContract.AlarmEntry;
import com.example.smartalarm.data.Alarm_Database;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.LinkedList;

public class AlarmActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    private final static String TAG = "AlarmActivity";
    private static final int ALARM_LOADER = 0;
    AlarmAdapter aAdapter;
    Parcelable state;
    ListView alarmListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.alarm_activity);
        setTitle("Alarms");

        /**floating button for @AddAlarm_Activity **/
        FloatingActionButton add_alarm_fab = findViewById(R.id.add_alarm_fb);

        add_alarm_fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent alarm_intent = new Intent
                        (AlarmActivity.this,AddAlarm_Activity.class );
                alarm_intent.setAction("from alarmActivity new");
                startActivity(alarm_intent);
            }
        });

        alarmListView = (ListView) findViewById(R.id.list);

        aAdapter = new AlarmAdapter(this, null);

        alarmListView.setAdapter(aAdapter);
        if (state != null)
            alarmListView.onRestoreInstanceState(state);

        alarmListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                Log.i("onclick listner","clicked");
                Intent intent = new Intent(AlarmActivity.this , AddAlarm_Activity.class);
                intent.setAction("from alarmActivity");
                /** send the uri with it id of the alarm database **/
                Uri editUri = ContentUris.withAppendedId(AlarmEntry.CONTENT_URI, id);
                /**  set the uri in the intent **/
                intent.setData(editUri);
                startActivity(intent);
            }
        });
        getLoaderManager().initLoader(ALARM_LOADER, null, this);

    }



    /** will inflate menu in the activity **/
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_alarmlist, menu);
        return true;
    }

    /** will delete all the alarm form the database **/
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            case R.id.delete_all_alarms:
                deleteAllPets();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void deleteAllPets() {
        int rowsDeleted = getContentResolver().delete
                (AlarmEntry.CONTENT_URI, null, null);
        Log.v("AlarmActivity", rowsDeleted + " all alarms are deleted ");
         ScheduleService.updateAlarmSchedule(getApplicationContext());
    }

    @Override
    protected void onStart() {
        super.onStart();
        getLoaderManager().initLoader(ALARM_LOADER, null, this);
    }

    @Override
    protected void onPause() {
        state = alarmListView.onSaveInstanceState();
        super.onPause();
        getLoaderManager().initLoader(ALARM_LOADER, null, this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        getLoaderManager().initLoader(ALARM_LOADER, null, this);
    }


    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        String[] projection = {
                AlarmEntry._ID,
                AlarmEntry.ALARM_NAME,
                AlarmEntry.ALARM_TIME,
                AlarmEntry.ALARM_ACTIVE};


        return new CursorLoader(this,
                AlarmEntry.CONTENT_URI,
                projection,
                null,
                null,
                null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor d) {
          aAdapter.swapCursor(d);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        aAdapter.swapCursor(null);
    }
}
