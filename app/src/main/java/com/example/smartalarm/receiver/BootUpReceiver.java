package com.example.smartalarm.receiver;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import com.example.smartalarm.AlarmConstraints;
import com.example.smartalarm.ScheduleService;
import com.example.smartalarm.data.Alarm_Database;

import java.util.List;

/**
 * will receive after the phone boot up
 */
public class BootUpReceiver extends BroadcastReceiver {
    private static final String TAG = "BootUpReceiver";

    @SuppressLint("LongLogTag")
    @Override
    public void onReceive(Context context, Intent intent) {
        if (Intent.ACTION_BOOT_COMPLETED.equals(intent.getAction())){
            Toast.makeText(context, "broadcast received",
                    Toast.LENGTH_SHORT).show();

            boolean isEmpty = true;
            try {
            Alarm_Database alarmDatabase= Alarm_Database.getInstance(context.getApplicationContext());
            List<AlarmConstraints> alarms = alarmDatabase.getAlarmsFromDataBase();
            for(AlarmConstraints alarm : alarms)
            {
                if(alarm.isAlarmOn())
                {
                    isEmpty = false;
                    alarm.scheduleAlarm(context.getApplicationContext() ,
                            alarm.getAlarmTime() , alarm.isRepeating() ,
                            alarm.getRepeatDayMap());

                }
            }
                Toast.makeText(context, "alarm scheduled",
                        Toast.LENGTH_SHORT).show();
            if (isEmpty) {
                Toast.makeText(context, "no active alarms", Toast.LENGTH_LONG).show();
            }
        }
        catch (Exception e)
        {
            Toast.makeText(context, "error scheduling alarm", Toast.LENGTH_LONG).show();
        }
        }

    }
}
