package com.coffeecoders.smartalarm.calender;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.coffeecoders.smartalarm.AlarmConstraints;
import com.coffeecoders.smartalarm.data.AlarmContract.AlarmEntry;
import com.coffeecoders.smartalarm.data.Alarm_Database;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class CalReceiver extends BroadcastReceiver {
    private Alarm_Database database;
    @Override
    public void onReceive(Context context, Intent intent) {
        database = new Alarm_Database(context.getApplicationContext());
        List<AlarmConstraints> events_list = database.getAlarmsFromDataBase(AlarmEntry.CAL_EVENTS_TABLE_NAME);

        for(AlarmConstraints calEvents:events_list){
            if(calEvents.isAlarmOn()){
                SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
                Date date = new Date();
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(date);
                int dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);
                int month = calendar.get(Calendar.MONTH);
                if(calEvents.getEventDate().equals(String.valueOf(dayOfMonth)+" "+String.valueOf(month))){
                    /**
                     * turn on the alarm now
                     */
                }
            }
        }
    }
}
