package com.example.smartalarm;

import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.smartalarm.data.AlarmContract.AlarmEntry;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class AlarmActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {
    private AlarmConstraints cancelAlarm;
    private static final int ALARM_LOADER = 0;
    AlarmAdapter aAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.alarm_activity);
        Intent intent = getIntent();
        setTitle("Alarms");
        // floating button for AddAlarm_Activity
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
                Intent alarm_intent = new Intent(AlarmActivity.this, AddAlarm_Activity.class);
                startActivity(alarm_intent);
            }
        });
        ListView alarmListView = (ListView) findViewById(R.id.list);
        aAdapter = new AlarmAdapter(this, null);
        alarmListView.setAdapter(aAdapter);
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
