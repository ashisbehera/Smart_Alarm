package com.example.smartalarm;

import android.content.Context;
import android.database.Cursor;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.Switch;
import android.widget.TextView;


import com.example.smartalarm.data.AlarmContract.AlarmEntry;

public class AlarmAdapter extends CursorAdapter {

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

    }
}
