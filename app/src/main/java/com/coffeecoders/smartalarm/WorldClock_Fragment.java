package com.coffeecoders.smartalarm;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

public class WorldClock_Fragment extends Fragment {
    Calendar current;
    Spinner spinner;
    TextView timeZone, textTimeZoneTime, world_clock_current_date;
    long milliseconds;
    ArrayAdapter<String> adapter;
    SimpleDateFormat simpleDateFormat, formatter;
    Date resultDate;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_world_clock, container, false);
        ((AppCompatActivity)getActivity()).getSupportActionBar().setTitle("World clock");
        ((AppCompatActivity)getActivity()).getSupportActionBar().show();
        spinner = view.findViewById(R.id.spinner);
        timeZone = view.findViewById(R.id.timeZone);
        textTimeZoneTime = view.findViewById(R.id.textTimeZone);
        world_clock_current_date = view.findViewById(R.id.world_clock_current_date);
        // array to fetch all the timezones
        String[] array = TimeZone.getAvailableIDs();
        formatter = new SimpleDateFormat("dd/MM/yyyy");
        setDate();
        // date format
        simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy  HH:mm");
        // dropdown menu for timezones
        adapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_spinner_item, array){
            @NonNull
            @Override
            public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
                View view = super.getView(position , convertView , parent);
                ((TextView) view).setTextSize(10);
                ((TextView) view).setTextColor(Color.parseColor("#ffffff"));
                return view;
            }
        };
        adapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
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
                int timeZoneOffset = tz.getRawOffset() / (60 * 1000);
                int hrs = timeZoneOffset / 60;
                int mins = timeZoneOffset % 60;
                milliseconds += tz.getRawOffset();
                resultDate = new Date(milliseconds);
                System.out.println(simpleDateFormat.format(resultDate));
                timeZone.setText(tz.getDisplayName());
                textTimeZoneTime.setText("" + simpleDateFormat.format(resultDate) + "   GMT" + hrs + "." + mins);
                milliseconds = 0;
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        return view;
    }


    // function to fetch gmt time
    @SuppressLint("SetTextI18n")
    private void getGMTTime() {
        current = Calendar.getInstance();
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

    public void setDate() {
        Date today = Calendar.getInstance().getTime();

        String date = formatter.format(today);
        world_clock_current_date.setText(date);
    }
}