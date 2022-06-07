package com.coffeecoders.smartalarm.receiver;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import com.coffeecoders.smartalarm.AlarmConstraints;
import com.coffeecoders.smartalarm.data.AlarmContract;
import com.coffeecoders.smartalarm.data.Alarm_Database;

import java.util.List;

/**
 * will receive after the phone boot up
 */
public class BootUpReceiver extends BroadcastReceiver {
    private static final String TAG = "BootUpReceiver";

    @SuppressLint("LongLogTag")
    @Override
    public void onReceive(Context context, Intent intent) {

        if (Intent.ACTION_BOOT_COMPLETED.equals(intent.getAction()) ||
                intent.getAction() == "android.intent.action.QUICKBOOT_POWERON"){
            refreshAlarm(context);
        }

        if (intent.getAction() == "smart alarm refresh alarm"){
            refreshAlarm(context);
        }


    }

    private void refreshAlarm(Context context){
        boolean isEmpty = true;
        try {
            Alarm_Database alarmDatabase= Alarm_Database.getInstance(context.getApplicationContext());
            List<AlarmConstraints> alarms = alarmDatabase.getAlarmsFromDataBase(AlarmContract.AlarmEntry.TABLE_NAME);
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

            if (isEmpty) {
                Toast.makeText(context, "no active alarms", Toast.LENGTH_LONG).show();
            }
        }
        catch (Exception e)
        {
        }
    }
}
