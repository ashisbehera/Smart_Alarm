package com.example.smartalarm;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Parcelable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.example.smartalarm.data.AlarmContract;
import com.example.smartalarm.data.Alarm_Database;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.HashMap;
import java.util.LinkedList;


public class Alarm_fragment extends Fragment implements ClickListener {

    private final static String TAG = "AlarmActivity";
    private static final int ALARM_LOADER = 0;
    AlarmAdapter aAdapter;
    Parcelable state;
    RecyclerView alarmRecycleView;
    LinkedList<AlarmConstraints> alarms;
    LinkedList<Integer> selected;
    Alarm_Database alarmDatabase;
    BroadcastReceiver broadcastReceiver;
    FloatingActionButton add_alarm_fab;
    private Toolbar toolbar;
    boolean isSelectEnable = false;
    boolean isSelectAll = false;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        selected = new LinkedList<>();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_alarm, container, false);
        toolbar = getActivity().findViewById(R.id.toolbar);
        ((AppCompatActivity)getActivity()).getSupportActionBar().setTitle("Alarms");
        ((AppCompatActivity)getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        ((AppCompatActivity)getActivity()).getSupportActionBar().show();
        /**floating button for @AddAlarm_Activity **/
        add_alarm_fab= view.findViewById(R.id.add_alarm_fb);

        add_alarm_fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent alarm_intent = new Intent
                        (getContext(),AddAlarm_Activity.class );
                alarm_intent.setAction("from alarmActivity new");
                startActivity(alarm_intent);
            }
        });

        alarmDatabase= Alarm_Database.getInstance(getContext().getApplicationContext());
        alarms = (LinkedList<AlarmConstraints>) alarmDatabase.getAlarmsFromDataBase();
        alarmRecycleView = (RecyclerView) view.findViewById(R.id.list);
        aAdapter = new AlarmAdapter(alarms , getContext().getApplicationContext() ,
                  this , this);
        alarmRecycleView.setLayoutManager(new LinearLayoutManager(getContext()));
        alarmRecycleView.setAdapter(aAdapter);

        /**
         * receiver for recycleView when stopped notification
         */
        broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (intent.getAction() == "com.example.smartalarm.dataChangeListener"){
                    aAdapter.notifyDataSetChanged();
                }
            }
        };

        return view;
    }


    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.menu_alarmlist, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()) {

            case R.id.delete_all_alarms:
                deleteAllPets();
                return true;
            case R.id.about_menu:
                Intent about_intent = new Intent(getContext(), About.class);
                startActivity(about_intent);
                return true;
            case R.id.delete:
                if (!isSelectAll){
                    aAdapter.deleteAndCancel();
                }else{
                    aAdapter.selectAllToDelete();
                }

                alarms = (LinkedList<AlarmConstraints>) alarmDatabase.getAlarmsFromDataBase();
                aAdapter = new AlarmAdapter(alarms , getContext().getApplicationContext() ,
                        this , this);
                alarmRecycleView.setLayoutManager(new LinearLayoutManager(getContext()));
                alarmRecycleView.setAdapter(aAdapter);
                replaceToolbar();
                return true;

            case R.id.select_all:
                if (!isSelectAll) {
                    isSelectAll = true;
                    aAdapter.notifyDataSetChanged();
                }else {
                    isSelectAll = false;
                    aAdapter.notifyDataSetChanged();
                }

                return true;

            case android.R.id.home:
                replaceToolbar();
                onStart();


        }
        return super.onOptionsItemSelected(item);
    }


    private void deleteAllPets() {
        for (AlarmConstraints alarm:alarms){
            if (alarm.getToggleOnOff()){
                alarm.cancelAlarm(getContext().getApplicationContext() , alarm);
            }
        }
        int rowsDeleted = getContext().getContentResolver().delete
                (AlarmContract.AlarmEntry.CONTENT_URI, null, null);
        Log.v("AlarmActivity", rowsDeleted + " all alarms are deleted ");

        ScheduleService.updateAlarmSchedule(getContext().getApplicationContext());
        alarmRecycleView.removeAllViewsInLayout();
        alarms.clear();
        aAdapter.notifyDataSetChanged();
    }

    private void replaceToolbar(){
        isSelectEnable = false;
        isSelectAll = false;
        toolbar.getMenu().clear();
        toolbar.inflateMenu(R.menu.menu_alarmlist);
        ((AppCompatActivity)getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        ((AppCompatActivity)getActivity()).getSupportActionBar().setTitle("Alarms");
        add_alarm_fab.setVisibility(View.VISIBLE);
    }


    @Override
    public void onStart() {
        super.onStart();
        IntentFilter intentFilter = new IntentFilter("com.example.smartalarm.dataChangeListener");
        getContext().registerReceiver(broadcastReceiver , intentFilter);
        aAdapter.notifyDataSetChanged();
    }

    @Override
    public void onPause() {
        super.onPause();
        aAdapter.notifyDataSetChanged();
        replaceToolbar();
    }

    @Override
    public void onStop() {
        super.onStop();
        getContext().unregisterReceiver(broadcastReceiver);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onResume() {
        super.onResume();
        aAdapter.notifyDataSetChanged();
        replaceToolbar();
    }


    @Override
    public void onLongClick(AlarmAdapter.ViewHolder holder , int position) {
        isSelectEnable = true;
        toolbar.getMenu().clear();
        toolbar.inflateMenu(R.menu.multidelete_menu);
        toolbar.setTitle("select");
        ((AppCompatActivity)getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        holder.alarmSwitch.setVisibility(View.GONE);
        holder.selectCheck.setVisibility(View.VISIBLE);
        add_alarm_fab.setVisibility(View.GONE);
//        aAdapter.notifyDataSetChanged();
    }

    @Override
    public void onclick() {

    }

}
