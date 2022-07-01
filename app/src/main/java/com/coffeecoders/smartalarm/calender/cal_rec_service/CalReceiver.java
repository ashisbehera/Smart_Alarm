package com.coffeecoders.smartalarm.calender.cal_rec_service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import com.coffeecoders.smartalarm.AlarmConstraints;
import com.coffeecoders.smartalarm.ScheduleService;
import com.coffeecoders.smartalarm.data.AlarmContract.AlarmEntry;
import com.coffeecoders.smartalarm.data.Alarm_Database;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class CalReceiver extends BroadcastReceiver {
    private static final String TAG = "CalReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {
        Toast.makeText(context , "bcast recived " , Toast.LENGTH_SHORT).show();
        context.startService(new Intent(context, CalService.class));
    }
}
