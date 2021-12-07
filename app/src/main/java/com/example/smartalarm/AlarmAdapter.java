package com.example.smartalarm;

import android.annotation.SuppressLint;
import android.app.LoaderManager;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.CursorAdapter;
import android.widget.RadioGroup;
import android.widget.Switch;
import android.widget.TextView;


import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.smartalarm.data.AlarmContract.AlarmEntry;

import java.util.LinkedList;

public class AlarmAdapter extends RecyclerView.Adapter<AlarmAdapter.ViewHolder>{
   LinkedList<AlarmConstraints> alarmList;
   Context context;
    public AlarmAdapter(LinkedList<AlarmConstraints> alarms , Context context) {
        alarmList = alarms;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.list_alarm ,parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        AlarmConstraints alarm = alarmList.get(position);
        alarm.setStandardTime(alarm.getAlarmTime());
        StringBuilder standardTime = alarm.getStandardTime();
       holder.timeTextView.setText(standardTime);
       holder.nameTextView.setText(alarm.getLabel());
       boolean switchStage  = alarm.getToggleOnOff();
       if (switchStage)
       holder.alarmSwitch.setChecked(true);
       else
       holder.alarmSwitch.setChecked(false);

       holder.itemView.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View view) {
               Intent intent = new Intent(view.getContext() , AddAlarm_Activity.class);
                intent.setAction("from alarmActivity");
                /** send the uri with it id of the alarm database **/
                Uri editUri = ContentUris.withAppendedId(AlarmEntry.CONTENT_URI, alarm.getPKeyDB());
                /**  set the uri in the intent **/
                intent.setData(editUri);
                view.getContext().startActivity(intent);
           }
       });

        holder.alarmSwitch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean on = holder.alarmSwitch.isChecked();
                if (on){
                    ContentValues values = new ContentValues();
                    values.put(AlarmEntry.ALARM_ACTIVE, 1);
                    Uri currentPetUri = ContentUris.withAppendedId(AlarmEntry.CONTENT_URI ,alarm.getPKeyDB());
                    context.getContentResolver().update(currentPetUri , values, null, null);
                    ScheduleService.updateAlarmSchedule(context);
                }else {
                    ContentValues values = new ContentValues();
                    values.put(AlarmEntry.ALARM_ACTIVE, 0);
                    Log.i("in alarmadapter", "database updated with rowid :"+alarm.getPKeyDB());
                    Uri currentPetUri = ContentUris.withAppendedId(AlarmEntry.CONTENT_URI ,alarm.getPKeyDB());
                    context.getContentResolver().update(currentPetUri , values, null, null);
                    /**
                     * this will stop if there is any pending snooze
                     */
                    alarm.cancelSnoozeAlarm(context.getApplicationContext() , alarm.getPKeyDB());
                    ScheduleService.updateAlarmSchedule(context);
                }
            }
        });



    }

    @Override
    public int getItemCount() {
        return alarmList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder  {

        TextView nameTextView;
        TextView timeTextView;
        Switch alarmSwitch;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);

             nameTextView = (TextView) itemView.findViewById(R.id.alarm_name);
             timeTextView = (TextView) itemView.findViewById(R.id.alarm_time);
             alarmSwitch = itemView.findViewById(R.id.alarm_active_switch);

        }


    }
}