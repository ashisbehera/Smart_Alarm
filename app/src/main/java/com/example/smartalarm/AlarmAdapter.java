package com.example.smartalarm;

import android.annotation.SuppressLint;
import android.app.LoaderManager;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
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


import com.example.smartalarm.data.AlarmContract.AlarmEntry;

public class AlarmAdapter extends CursorAdapter{

    LayoutInflater layoutInflater;
    Context context;

    public AlarmAdapter(Context context, Cursor c) {
        super(context, c,0);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup viewGroup) {
         return LayoutInflater.from(context).
                 inflate(R.layout.list_alarm, viewGroup, false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        AlarmConstraints alarm = new AlarmConstraints();
        TextView nameTextView = (TextView) view.findViewById(R.id.alarm_name);
        TextView timeTextView = (TextView) view.findViewById(R.id.alarm_time);
        Switch alarmSwitch = view.findViewById(R.id.alarm_active_switch);

        @SuppressLint("Range")
        int rowId = cursor.getInt(cursor.getColumnIndex(AlarmEntry._ID));
        int nameColumnIndex = cursor.getColumnIndex(AlarmEntry.ALARM_NAME);
        int timeColumnIndex = cursor.getColumnIndex(AlarmEntry.ALARM_TIME);
        int switchColumnIndex = cursor.getColumnIndex(AlarmEntry.ALARM_ACTIVE);

        String alarmName = cursor.getString(nameColumnIndex);
        String alarmTime = cursor.getString(timeColumnIndex);
        /**
         * convert the alarm time to standard time
         */
        alarm.setStandardTime(alarmTime);
        StringBuilder standardTime = alarm.getStandardTime();

        int switchStage = cursor.getInt(switchColumnIndex);

        if (TextUtils.isEmpty(alarmName)) {
            alarmName = context.getString(R.string.default_alarm_name);
        }

        if(switchStage==0){
            alarmSwitch.setChecked(false);
        }else
            alarmSwitch.setChecked(true);

        nameTextView.setText(alarmName);
        timeTextView.setText(standardTime);

        /**this will on/off alarm thorough toggle button **/
        alarmSwitch.setOnCheckedChangeListener(new Switch.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                Log.i("in alarmadapter", "toggle clicked");
                boolean on = alarmSwitch.isChecked();
                /**if the toggle button is turned on then update the data base with the specified rowid
                 * with value 1 which is on.
                 */
                if(on){
                    ContentValues values = new ContentValues();
                    values.put(AlarmEntry.ALARM_ACTIVE, 1);
                    Log.i("in alarmadapter", "database updated with rowid :"+rowId);
                    Uri currentPetUri = ContentUris.withAppendedId(AlarmEntry.CONTENT_URI ,rowId);
                    context.getContentResolver().update(currentPetUri , values, null, null);
                    ScheduleService.updateAlarmSchedule(context);
                }else{
                    /**if the toggle button is turned off then update the data base with the specified rowid
                     * with value 0 which is off.
                     */
                    ContentValues values = new ContentValues();
                    values.put(AlarmEntry.ALARM_ACTIVE, 0);
                    Log.i("in alarmadapter", "database updated with rowid :"+rowId);
                    Uri currentPetUri = ContentUris.withAppendedId(AlarmEntry.CONTENT_URI ,rowId);
                    context.getContentResolver().update(currentPetUri , values, null, null);
                    ScheduleService.updateAlarmSchedule(context);
                }
            }
        });

    }

}
