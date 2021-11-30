package com.example.smartalarm;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import java.sql.Time;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

public class world_clock extends AppCompatActivity {
    Calendar current;
    Spinner spinner;
    TextView timeZone, textTimeZoneTime, textCurrentTime;
    long milliseconds;
    ArrayAdapter<String> adapter;
    SimpleDateFormat simpleDateFormat;
    Date resultDate;

    @SuppressLint("SimpleDateFormat")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_world_clock);
        // find ids
        spinner = findViewById(R.id.spinner);
        timeZone = findViewById(R.id.timeZone);
        textCurrentTime = findViewById(R.id.textCurrentTime);
        textTimeZoneTime = findViewById(R.id.textTimeZone);
        // array to fetch all the timezones
        String[] array = TimeZone.getAvailableIDs();
        // date format
        simpleDateFormat = new SimpleDateFormat("EEEE, dd MMMM yy HH:mm:ss");
        // dropdown menu for timezones
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, array);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        getGMTTime();
        // calculate and set the time according to the timezone
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                getGMTTime();
                String selectedID = (String) (adapterView.getItemAtPosition(i));
                TimeZone tz = TimeZone.getTimeZone(selectedID);
                String timeZoneName = tz.getDisplayName();
                int timeZoneOffset = tz.getRawOffset() / (60 * 1000);
                int hrs = timeZoneOffset / 60;
                int mins = timeZoneOffset % 60;
                milliseconds += tz.getRawOffset();
                resultDate = new Date(milliseconds);
                System.out.println(simpleDateFormat.format(resultDate));
                timeZone.setText(timeZoneName + " GMT " + hrs + "." + mins);
                textTimeZoneTime.setText("" + simpleDateFormat.format(resultDate));
                milliseconds = 0;
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    // function to fetch current time
    @SuppressLint("SetTextI18n")
    private void getGMTTime() {
        current = Calendar.getInstance();
        textCurrentTime.setText("" + current.getTime());
        milliseconds = current.getTimeInMillis();
        TimeZone timeZoneCurrent = current.getTimeZone();
        int offset = timeZoneCurrent.getRawOffset();
        if (timeZoneCurrent.inDaylightTime(new Date())) {
            offset += timeZoneCurrent.getDSTSavings();
        }
        milliseconds -= offset;
        resultDate = new Date(milliseconds);
        System.out.println(simpleDateFormat.format(resultDate));
    }
}