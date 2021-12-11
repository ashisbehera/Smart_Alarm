package com.example.smartalarm;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.example.smartalarm.data.Alarm_Database;

import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

public class ScheduleService extends Service {
   private final static String TAG = "scheduleService";
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    /**
     *
     * @param context
     *this method will be called when ever we need to update the service
     * for example: when one alarm will be fired then next alarm will come to server
     * for that we need to update the service
     */
    public static void updateAlarmSchedule(Context context)
    {
        Log.i(TAG, "updateAlarmSchedule: inside schedule service");
        Intent intent=new Intent(context,ScheduleService.class);
        context.startService(intent);
    }

    @SuppressLint("LongLogTag")
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        boolean isEmpty = true;
        Log.i("service received" , "now");
        try {

            Alarm_Database alarmDatabase= Alarm_Database.getInstance(getApplicationContext());

            List<AlarmConstraints> alarms = alarmDatabase.getAlarmsFromDataBase();
            for(AlarmConstraints alarm : alarms)
            {
                if(alarm.isAlarmOn())
                {
                    isEmpty = false;
                    alarm.scheduleAlarm(getApplicationContext() ,
                            alarm.getAlarmTime() , alarm.isRepeating() ,
                            alarm.getRepeatDayMap());
                    Log.i(TAG, "onStartCommand: alarm set for pkeyid" + alarm.getPKeyDB());

                }
            }

            if (isEmpty) {
                Toast.makeText(getApplicationContext(), "no active alarms", Toast.LENGTH_LONG).show();
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
