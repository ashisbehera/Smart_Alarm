package com.coffeecoders.smartalarm;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.coffeecoders.smartalarm.data.AlarmContract;
import com.coffeecoders.smartalarm.data.Alarm_Database;

import java.util.List;

public class ScheduleService extends Service {
   private final static String TAG = "scheduleService";
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


    @SuppressLint("LongLogTag")
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        boolean isEmpty = true;
        Log.i(TAG + " service received" , "now");
        try {

            Alarm_Database alarmDatabase = Alarm_Database.getInstance(getApplicationContext());

            List<AlarmConstraints> alarms = alarmDatabase.getAlarmsFromDataBase(AlarmContract.AlarmEntry.TABLE_NAME);
            for (AlarmConstraints alarm : alarms) {
                if (alarm.isAlarmOn()) {
                    isEmpty = false;
                    alarm.scheduleAlarm(getApplicationContext(),
                            alarm.getAlarmTime(), alarm.isRepeating(),
                            alarm.getRepeatDayMap());
                    Log.i(TAG, "onStartCommand: alarm set for pkeyid" + alarm.getPKeyDB());

                }
            }

            if (isEmpty) {
                Toast.makeText(getApplicationContext(), "no active alarms", Toast.LENGTH_LONG).show();
            } else{
                Log.e(TAG, "onStartCommand: scheduled for all");
                Log.w(TAG, "onStartCommand: scheduled for all");
           }
        }
        catch (Exception e)
        {
            Log.i("error while sending service to scheduleAlarm", "failed to send service");
        }

        /**
         * START_NOT_STICKY says that, after returning from onStartCreated(),
         * if the process is killed with no remaining start commands to deliver,
         * then the service will be stopped instead of restarted.
         */
        return START_NOT_STICKY;
    }
}
