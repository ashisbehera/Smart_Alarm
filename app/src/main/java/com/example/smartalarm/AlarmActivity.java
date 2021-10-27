package com.example.smartalarm;


import android.app.LoaderManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.ContentUris;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;


import androidx.appcompat.app.AppCompatActivity;

import com.example.smartalarm.data.AlarmContract.AlarmEntry;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class AlarmActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final int ALARM_LOADER = 0;

    AlarmAdapter aAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.alarm_activity);

        setTitle("Alarms");



        //floating button for @AddAlarm_Activity
        FloatingActionButton add_alarm_fab = findViewById(R.id.add_alarm_fb);

//        cancelbtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//            cancelAlarm.cancelAlarm(getApplicationContext());
//            }
//        });
        add_alarm_fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent alarm_intent = new Intent
                        (AlarmActivity.this,AddAlarm_Activity.class );
                startActivity(alarm_intent);
            }
        });

        ListView alarmListView = (ListView) findViewById(R.id.list);

        aAdapter = new AlarmAdapter(this, null);

        alarmListView.setAdapter(aAdapter);

        alarmListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                Log.i("onclick listner","clicked");
                Intent intent = new Intent(AlarmActivity.this , AddAlarm_Activity.class);

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
