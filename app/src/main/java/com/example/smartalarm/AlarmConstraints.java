package com.example.smartalarm;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import java.util.Calendar;

import static android.os.Build.VERSION.SDK_INT;

public class AlarmConstraints {
    /**
     * this will be alarmtime
     */
    private String alarmTime;
    /**
     * will store the time
     * for future use
     */
    private StringBuilder standardTime;
    /**
     * this time will got to alarm manager
     */
    private long alarmTimeinMilliseconds = 0;
    /**
     * intent for broadcast receiver
     */
    private Intent intent;
    /**
     * calender to get the time
     */
    private Calendar calendar = Calendar.getInstance();

    /**
     * will get the calender time
     * for future use
     */
    public String getTime() {
        String hour = String.valueOf(calendar.get(Calendar.HOUR_OF_DAY));
        String min = String.valueOf(calendar.get(Calendar.MINUTE));
        return alarmTime = hour + ":" + min;
    }

    /**
     * will set the time from the timepicker to alarmtime
     */
    public void setAlarmTime(String alarmTime) {
        this.alarmTime = alarmTime;
        setStandardTime(alarmTime);
    }

    /**
     * will extract the time from the alarmtime string
     * and will set the calender time for calculation
     */
    private void setStandardTime(String time) {
        standardTime = new StringBuilder();
        String[] splitTime = time.split(":");
        /**
         *extracting hour and min
         */
        int hour = Integer.parseInt(splitTime[0]);
        int min = Integer.parseInt(splitTime[1]);
        /**
         *setting to callender
         */
        calendar.set(Calendar.HOUR_OF_DAY, hour);
        calendar.set(Calendar.MINUTE, min);
        calendar.set(Calendar.SECOND, 0);
        String format;

        if (hour == 0) {
            hour += 12;
            format = "AM";
        } else if (hour == 12) {
            format = "PM";
        } else if (hour > 12) {
            hour -= 12;
            format = "PM";
        } else {
            format = "AM";
        }
        /**
         *setting AM or PM
         */

        if (format.equals("AM")) {
            calendar.set(Calendar.AM_PM, Calendar.AM);
        } else {
            calendar.set(Calendar.AM_PM, Calendar.PM);
        }
        /**
         *for future use
         */
        standardTime.append(hour).append(" : ");

        if (String.valueOf(min).length() == 1) {
            standardTime.append("0").append(min);
        } else {
            standardTime.append(min);
        }
        standardTime.append(" ").append(format);
    }

    /**
     * will convert the incoming time to millisecond to set the alarm
     */
    private long convertTimeInMilliseconds() {
        /**
         *setting calender time again from the time we get above
         */
        Calendar newCalendar = Calendar.getInstance();
        newCalendar.set(Calendar.HOUR, calendar.get(Calendar.HOUR));
        newCalendar.set(Calendar.MINUTE, calendar.get(Calendar.MINUTE));
        newCalendar.set(Calendar.SECOND, calendar.get(Calendar.SECOND));
        newCalendar.set(Calendar.AM_PM, calendar.get(Calendar.AM_PM));
        /**
         *if the time less than current time
         */
        if (calendar.before(Calendar.getInstance())) {
            newCalendar.add(Calendar.DAY_OF_WEEK, 1);
        }

        /**
         *converting to millisecond
         */
        alarmTimeinMilliseconds = newCalendar.getTimeInMillis() - (newCalendar.getTimeInMillis() % 60000);

        return alarmTimeinMilliseconds;
    }

    /**
     * pusing the alarm to the alarmmanager
     */
    public void scheduleAlarm(Context context) {
        alarmTimeinMilliseconds = convertTimeInMilliseconds();
        /**
         *intent for broadcast manager
         */
        intent = new Intent(context, AlarmReceiver.class);
        // intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        // intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        PendingIntent pi = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT);
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        if (alarmManager == null)
            return;

        if (SDK_INT >= 23)
            alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, alarmTimeinMilliseconds, pi);
        else {
            alarmManager.set(AlarmManager.RTC_WAKEUP, alarmTimeinMilliseconds, pi);
        }


    }

    /**
     * will cancel the alarm
     */
    public void cancelAlarm(Context context) {

        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, intent, 0);

        if (alarmManager != null) {
            alarmManager.cancel(pendingIntent);
        }
    }
}
