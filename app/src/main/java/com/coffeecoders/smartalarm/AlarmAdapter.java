package com.coffeecoders.smartalarm;

import android.annotation.SuppressLint;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;


import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.coffeecoders.smartalarm.data.AlarmContract;
import com.coffeecoders.smartalarm.data.AlarmContract.AlarmEntry;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.TreeMap;

public class AlarmAdapter extends RecyclerView.Adapter<AlarmAdapter.ViewHolder>{
    private LinkedList<AlarmConstraints> alarmList;
    private Context context;
    private Alarm_fragment alarm_fragment;
    private ClickListener clickListener;
    HashMap<Integer , AlarmConstraints> selectedMap = new HashMap<>();
    public AlarmAdapter(LinkedList<AlarmConstraints> alarms,
                        Context context, Alarm_fragment alarm_fragment , ClickListener clickListener) {
        alarmList = alarms;
        this.context = context;
        this.alarm_fragment = alarm_fragment;
        this.clickListener = clickListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.list_alarm ,parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @SuppressLint("ResourceAsColor")
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        AlarmConstraints alarm = alarmList.get(position);
        alarm.setStandardTime(alarm.getAlarmTime());
        StringBuilder repeatDaysSb = new StringBuilder();
        TreeMap<Integer, String> repeatDays = alarm.getRepeatDayMap();
        if (!alarm.isRepeating()){
            holder.nameTextView.setText(alarm.getLabel());
        }
        else if (repeatDays.size() == 7){
            holder.nameTextView.setText(alarm.getLabel() + " (Every day)");
        }else if(alarm.isRepeating()){
            for (Map.Entry<Integer, String> entry : repeatDays.entrySet()){
                repeatDaysSb.append(entry.getValue()+" ");
            }
            holder.nameTextView.setText(alarm.getLabel()+ " ( "+repeatDaysSb.toString()+")");
        }
        StringBuilder standardTime = alarm.getStandardTime();
        holder.timeTextView.setText(standardTime);

        boolean switchStage  = alarm.getToggleOnOff();
        if (switchStage) {
            holder.alarmSwitch.setChecked(true);
            holder.timeTextView.setTextColor(Color.parseColor("#FFFFFF"));
            holder.nameTextView.setTextColor(Color.parseColor("#FFFFFF"));
        }
        else {
            holder.alarmSwitch.setChecked(false);
            holder.timeTextView.setTextColor(Color.parseColor("#6BFFFFFF"));
            holder.nameTextView.setTextColor(Color.parseColor("#6BFFFFFF"));
        }


        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!alarm_fragment.isSelectEnable){
                    Intent intent = new Intent(view.getContext() , AddAlarm_Activity.class);
                    intent.setAction("from alarmActivity");
                    /** send the uri with it id of the alarm database **/
                    Uri editUri = ContentUris.withAppendedId(AlarmEntry.CONTENT_URI, alarm.getPKeyDB());
                    /**  set the uri in the intent **/
                    intent.setData(editUri);
                    view.getContext().startActivity(intent);
                }else {
                    alarm_fragment.isSelectAll = false;
                    if (holder.selectCheck.getVisibility() == View.VISIBLE){
                        holder.alarmSwitch.setVisibility(View.VISIBLE);
                        holder.selectCheck.setVisibility(View.GONE);
                        selectedMap.remove(position);
//                        alarmList.add(position , alarm);
//                        alarm_fragment.modifyData(position , 0 );
                    }else{
                        holder.alarmSwitch.setVisibility(View.GONE);
                        holder.selectCheck.setVisibility(View.VISIBLE);
                        selectedMap.put(position , alarm);
//                        alarmList.remove(position);
//                        alarm_fragment.modifyData(position , 1 );
                    }

                }

            }
        });

        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                if (!alarm_fragment.isSelectEnable){
                    clickListener.onLongClick(holder , position);
                    selectedMap.put(position , alarm);
                    Log.e("TAG", "onLongClick: "+position );
//                    alarm_fragment.modifyData(position , 1);
                }

                return true;
            }
        });
        holder.alarmSwitch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean on = holder.alarmSwitch.isChecked();
                if (on){
                    holder.timeTextView.setTextColor(Color.parseColor("#FFFFFF"));
                    holder.nameTextView.setTextColor(Color.parseColor("#FFFFFF"));

                    long milisec = alarm.convertTimeInMS(alarm.getAlarmTime(),
                            alarm.isRepeating() , alarm.getRepeatDayMap());

                    Toast.makeText(context, "alarm will ring in : "+
                                    String.valueOf(alarm.getDurationBreakdown(milisec)),
                            Toast.LENGTH_SHORT).show();
                    ContentValues values = new ContentValues();
                    values.put(AlarmEntry.ALARM_ACTIVE, 1);
                    Uri currentPetUri = ContentUris.withAppendedId(AlarmEntry.CONTENT_URI ,alarm.getPKeyDB());
                    context.getContentResolver().update(currentPetUri , values, null, null);
                    context.startService(new Intent(context, ScheduleService.class));


                }else {
                    holder.timeTextView.setTextColor(Color.parseColor("#6BFFFFFF"));
                    holder.nameTextView.setTextColor(Color.parseColor("#6BFFFFFF"));
                    ContentValues values = new ContentValues();
                    values.put(AlarmEntry.ALARM_ACTIVE, 0);
                    Log.i("in alarmadapter", "database updated with rowid :"+alarm.getPKeyDB());
                    Uri currentPetUri = ContentUris.withAppendedId(AlarmEntry.CONTENT_URI ,alarm.getPKeyDB());
                    context.getContentResolver().update(currentPetUri , values, null, null);
                    /**
                     * this will stop if there is any pending snooze
                     */
                    alarm.cancelAlarm(context.getApplicationContext() , alarm);
                    context.startService(new Intent(context, ScheduleService.class));
                }
            }
        });


        if (alarm_fragment.isSelectAll){
            holder.alarmSwitch.setVisibility(View.GONE);
            holder.selectCheck.setVisibility(View.VISIBLE);
        }else {
            holder.selectCheck.setVisibility(View.GONE);
            holder.alarmSwitch.setVisibility(View.VISIBLE);
        }

    }

    @Override
    public int getItemCount() {
        return alarmList.size();
    }

    public void deleteAndCancel(){
        for (Map.Entry<Integer,AlarmConstraints> entry : selectedMap.entrySet()) {
            AlarmConstraints offAlarm = entry.getValue();
            offAlarm.cancelAlarm(context, offAlarm);
            Uri deleteUri = ContentUris.withAppendedId(AlarmEntry.CONTENT_URI, offAlarm.getPKeyDB());
            context.getContentResolver().delete(deleteUri, null, null);
            alarmList.remove(entry.getKey());
        }

        context.startService(new Intent(context, ScheduleService.class));

    }

    public void selectAll(){
        int i=0;
        selectedMap.clear();
        for (AlarmConstraints selectAlarm:alarmList){
            selectedMap.put( i++ , selectAlarm );
        }
    }

    public void deSelectedAll(){
        selectedMap.clear();
    }

    public void selectAllToDelete(){
        selectedMap.clear();
        for (AlarmConstraints alarms:alarmList){
            alarms.cancelAlarm(context , alarms);
            int rowsDeleted = context.getContentResolver().delete
                    (AlarmContract.AlarmEntry.CONTENT_URI, null, null);
        }

        context.startService(new Intent(context, ScheduleService.class));

    }

    public class ViewHolder extends RecyclerView.ViewHolder  {

        TextView nameTextView;
        TextView timeTextView;
        Switch alarmSwitch;
        ImageView selectCheck;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            nameTextView = (TextView) itemView.findViewById(R.id.alarm_name);
            timeTextView = (TextView) itemView.findViewById(R.id.alarm_time);
            alarmSwitch = itemView.findViewById(R.id.alarm_active_switch);
            selectCheck = itemView.findViewById(R.id.recycle_checkImg);
        }


    }
}