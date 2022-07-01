package com.coffeecoders.smartalarm.calender.cal_rec_service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.coffeecoders.smartalarm.AlarmConstraints;
import com.coffeecoders.smartalarm.data.AlarmContract;
import com.coffeecoders.smartalarm.data.Alarm_Database;

import java.security.cert.Extension;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class CalService extends Service {
    private static final String TAG = "CalService";
    private Alarm_Database database;
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.e(TAG, "onStartCommand: service started" );
        try{
            database = Alarm_Database.getInstance(getApplicationContext());
            List<AlarmConstraints> events_list = database.getAlarmsFromDataBase(AlarmContract.AlarmEntry.CAL_EVENTS_TABLE_NAME);
            Toast.makeText(getApplicationContext() , "data collected " , Toast.LENGTH_SHORT).show();
            Log.e(TAG, "onReceive: data collected" );
            SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
            Date date = new Date();
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(date);
            int dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);
            String day = null;
            if(dayOfMonth<10){
                day = "0"+dayOfMonth;
            }else
                day = ""+dayOfMonth;
            int month = calendar.get(Calendar.MONTH)+1;
            String date_month = day+" "+String.valueOf(month);
            Toast.makeText(getApplicationContext() , "date_month "+date_month , Toast.LENGTH_LONG).show();
            for(AlarmConstraints calEvents:events_list){
//            if(calEvents.isAlarmOn()){

                Log.e(TAG, "onReceive: " + calEvents.getLabel() );
                if(calEvents.getEventDate().equals(date_month)){
                    /**
                     * turn on the alarm now
                     */
                    Toast.makeText(getApplicationContext() , "alarm for "+ calEvents.getLabel() , Toast.LENGTH_LONG).show();
                }
//            }
            }
        }catch (Exception e){
            Log.e(TAG, "onStartCommand: error while fetching data " + e );
        }


        return START_NOT_STICKY;
    }
}
