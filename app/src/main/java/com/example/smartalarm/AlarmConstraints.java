package com.example.smartalarm;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.provider.Settings;
import android.util.Log;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import static android.os.Build.VERSION.SDK_INT;

public class AlarmConstraints implements Parcelable  {

    public final static String ALARM_KEY="alarm";

    /**
     * this will be alarmtime
     */
    private String alarmTime;
    /**
     *will store the time in standard format
     *
     */
    private StringBuilder standardTime;
    /**
     * for toggle on/off use
     */
    private boolean isAlarmOn=false;
    /**
     *this time will got to alarm manager
     */
    private long alarmTimeInMS=0;
    /**
     *intent for broadcast receiver
     */
    private Intent intent;
    /**
     * this key will be same as the key of the database
     */
    private int pKeyDB=0;

    private String Label="Alarm";
    /**
     *calender to get the time
     */
    private Calendar calendar=Calendar.getInstance();

    public AlarmConstraints() {

    }

    /**
     * check if toggle on/off
     * @return
     */

    public boolean isAlarmOn()
    {
        return isAlarmOn;
    }


    /**
     *will get the calender time
     * for future use
     */


    public String getTime()
    {
        String hour=String.valueOf(calendar.get(Calendar.HOUR_OF_DAY));
        String min=String.valueOf(calendar.get(Calendar.MINUTE));
        return alarmTime=hour+":"+min;
    }

    /**
     * SET pKeyDB
     * @param pKey
     */
    public void setPKeyDB(int pKey) {
        pKeyDB=pKey;
    }

    /**
     * get pKeyDB
     * @return
     */
    public int getPKeyDB(){
        return pKeyDB;
    }

    /**
     * SET label of the alarm
     * @param label
     */

    public void setLabel(String label)
    {
        this.Label=label;
    }

    /**
     * set the toggle on/off
     * @param val
     */
    public void setToggleOnOff(boolean val)
    {
        isAlarmOn=val;
    }
    /**
     *will set the time from the timepicker to alarmtime
     */
    public void setAlarmTime(String alarmTime) {
        this.alarmTime=alarmTime;
    }

    public String getAlarmTime(){
        return alarmTime;
    }
    /**
     *will convert the alarm time to standard time with am/pm
     */
    public void setStandardTime(String time)
    {
        standardTime=new StringBuilder();
        String []splitTime = time.split(":");
        /**
         *extracting hour and min
         */
        int hour=Integer.parseInt(splitTime[0]);
        int min=Integer.parseInt(splitTime[1]);
        String format;

        if (hour == 0) {
            hour += 12;
            format = "AM";
        }
        else if (hour == 12) {
            format = "PM";
        } else if (hour > 12) {
            hour -= 12;
            format = "PM";
        } else {
            format = "AM";
        }

        /**
         *for future use
         */
        standardTime.append(hour).append(" : ");

        if(String.valueOf(min).length()==1) {
            standardTime.append("0").append(min);
        }
        else {
            standardTime.append(min);
        }
        standardTime.append(" ").append(format);
    }

    /**
     * will get the standard time
     * @return
     */
    public StringBuilder getStandardTime(){
        return  standardTime;
    }

    /**
     *will convert the incoming time to millisecond to set the alarm
     */
    private long convertTimeInMS(String time) {
        standardTime=new StringBuilder();
        String []splitTime = time.split(":");
        /**
         *extracting hour and min
         */
        int hour=Integer.parseInt(splitTime[0]);
        int min=Integer.parseInt(splitTime[1]);
        /**
         *setting calender time again from the time we get above
         */
        Calendar newCalendar=Calendar.getInstance();

        newCalendar.set(Calendar.HOUR,hour);
        newCalendar.set(Calendar.MINUTE,min);
        newCalendar.set(Calendar.SECOND,0);

        /**
         * get the system time
         */
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
        String s = sdf.format(new Date());
        String []curTime = s.split(":");
        int hr = Integer.parseInt(curTime[0]);
        /**
         * if the system time hour is pm format then change the alarm pm/am
         */
        if (hr>=12){
            newCalendar.set(Calendar.AM_PM,Calendar.AM);
        }

        /**
         *if the time less than current time
         */
        if (newCalendar.before(Calendar.getInstance())) {
            newCalendar.add(Calendar.DAY_OF_WEEK,1);
        }

        /**
         *converting to millisecond
         */
        alarmTimeInMS= newCalendar.getTimeInMillis();
        Log.i("UPDATE",String.valueOf(alarmTimeInMS));
        return alarmTimeInMS;
    }

    /**
     * this will use to show toast msg of the ringing alarm time
     * @param
     * @return
     */
    public String getDurationBreakdown(long time) {
        if(time < 0) {
            throw new IllegalArgumentException("Duration must be greater than zero!");
        }
        long millis = time - Calendar.getInstance().getTimeInMillis();
        long hours = TimeUnit.MILLISECONDS.toHours(millis);
        millis -= TimeUnit.HOURS.toMillis(hours);
        long minutes = TimeUnit.MILLISECONDS.toMinutes(millis);
        millis -= TimeUnit.MINUTES.toMillis(minutes);
        long seconds = TimeUnit.MILLISECONDS.toSeconds(millis);

        StringBuilder sb = new StringBuilder(64);
        sb.append(hours);
        sb.append(":H ");
        sb.append(minutes);
        sb.append(":M ");
        sb.append(seconds);
        sb.append(":S");

        return(sb.toString());
    }

    /**
     *pusing the alarm to the alarmmanager
     */
    @SuppressLint("LongLogTag")
    public void scheduleAlarm(Context context , String time)
    {
        alarmTimeInMS = convertTimeInMS(time);
        /**
         *intent for broadcast manager
         */
        intent = new Intent(context, AlarmReceiver.class);
        Bundle bundle=new Bundle();
        bundle.putParcelable(ALARM_KEY,this);
        intent.putExtra(ALARM_KEY,bundle);
        Log.i("the pkey in alarmconstaints",String.valueOf(this.getPKeyDB()));
        PendingIntent pi = PendingIntent.getBroadcast
                (context, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT);
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        if (alarmManager == null)
            return;

        if (android.os.Build.VERSION.SDK_INT >= 23) {
            alarmManager.setAndAllowWhileIdle(AlarmManager.RTC_WAKEUP,
                    alarmTimeInMS , pi);
        } else if (android.os.Build.VERSION.SDK_INT >= 19
                && android.os.Build.VERSION.SDK_INT < 23) {
            alarmManager.setExact(AlarmManager.RTC_WAKEUP, alarmTimeInMS , pi);
        } else {
            alarmManager.set(AlarmManager.RTC_WAKEUP, alarmTimeInMS , pi);
        }
        Toast.makeText(context, "alarm will ring in :"+
                        String.valueOf(getDurationBreakdown(alarmTimeInMS)) ,
                Toast.LENGTH_SHORT).show();
        Log.i("alarm will ring in :",String.valueOf(getDurationBreakdown(alarmTimeInMS)));

    }
    /**
     *will cancel the alarm
     */
    public void cancelAlarm(Context context){

        try {
            AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
            intent = new Intent(context, AlarmReceiver.class);
            PendingIntent pendingIntent = PendingIntent.getBroadcast
                    (context, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT);

            Log.i("removefrmSchedule", "going to remove");
            if (alarmManager != null) {
                alarmManager.cancel(pendingIntent);
                Log.i("removefrmSchedule", "removed");
            }
        }
        catch (NullPointerException NPE) {
            Log.i("remove alarm",NPE.getLocalizedMessage());
        }
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(isAlarmOn ? 1 : 0);
        parcel.writeLong(alarmTimeInMS);
        parcel.writeString(alarmTime);
        parcel.writeString(standardTime.toString());
        parcel.writeInt(pKeyDB);
        //parcel.writeParcelable(intent, i);

    }

    protected AlarmConstraints(Parcel in) {
        isAlarmOn = in.readInt() == 1;
        alarmTimeInMS = in.readLong();
        alarmTime = in.readString();
        standardTime=new StringBuilder(in.readString());
        pKeyDB = in.readInt();
        //intent = in.readParcelable(Intent.class.getClassLoader());
    }

    public static final Parcelable.Creator<AlarmConstraints> CREATOR = new ClassLoaderCreator<AlarmConstraints>() {
        @Override
        public AlarmConstraints createFromParcel(Parcel parcel, ClassLoader classLoader) {
            return new AlarmConstraints(parcel);
        }

        @Override
        public AlarmConstraints createFromParcel(Parcel in) {
            return new AlarmConstraints(in);
        }

        @Override
        public AlarmConstraints[] newArray(int size) {
            return new AlarmConstraints[0];
        }
    };

    public Long getMillisecondTime(String time) {
        return convertTimeInMS(time);
    }

    public String getTimeToRing(long timeInM){
        return getDurationBreakdown(timeInM);
    }
}