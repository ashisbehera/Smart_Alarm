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
        Intent intent=new Intent(context,ScheduleService.class);
        context.startService(intent);
    }

    @SuppressLint("LongLogTag")
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {


        Log.i("service received" , "now");
        try {
            /**
             * getting alarm from database
             */
            AlarmConstraints alarm = getAlarm();
            /**
             * if no alarm then nothing to do else schedule the alarm
             */
            if (alarm != null) {
                Log.i("the pkey in scheduleservice",String.valueOf(alarm.getPKeyDB()));

                alarm.scheduleAlarm(getApplicationContext() , alarm.getAlarmTime());
                  Log.i("scheduleAlarm","alarm schedule in time");
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



    @SuppressLint("LongLogTag")
    public AlarmConstraints getAlarm()
    {
        /**
         * getting database instance
         */
        Alarm_Database alarmDatabase= Alarm_Database.getInstance(getApplicationContext());
         Log.i("database instance received","received");

        /**
         * this set will arrange the alarms by the time difference
         */
        Set<AlarmConstraints> qu_as_theTime_diff = new TreeSet<>(new Comparator<AlarmConstraints>() {
            @Override
            public int compare(AlarmConstraints lhs, AlarmConstraints rhs) {
                int result = 0;
                long diff = lhs.getMillisecondTime(lhs.getAlarmTime()) - rhs.getMillisecondTime(rhs.getAlarmTime());

                if(diff > 0){
                    return 1;
                }else if (diff < 0) {
                    return -1;
                }
                return result;
            }
        });
       /**
       * will get the list of alarms from the database
       */
        List<AlarmConstraints> alarms = alarmDatabase.getAlarmsFromDataBase();


        /**
         * then we add the alarms to the set if the alarm is on
         */
        for(AlarmConstraints alarm : alarms)
        {
            if(alarm.isAlarmOn())
            {
                qu_as_theTime_diff.add(alarm);
            }
        }

        /**
         * then iterate one by one and return that
         */
        if(qu_as_theTime_diff.iterator().hasNext())
        {
            return qu_as_theTime_diff.iterator().next();
        }
        else
        {
            Log.i("active on/off", "no active alarm");
            Toast.makeText(getApplicationContext(),"no active alarms",Toast.LENGTH_LONG).show();
            return null;
        }

    }
}
