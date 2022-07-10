package com.coffeecoders.smartalarm;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.concurrent.TimeUnit;

public class AlarmConstraints implements Parcelable  {

    private final static String TAG = "AlarmConstraints";

    public AlarmConstraints() {

    }
    /** this will save the repeat days int value as key and days as value (key , value) **/
    private TreeMap<Integer , String>  repeatDayMap;
    /**getRepeatDayMap
     * @return
     */
    public TreeMap<Integer, String> getRepeatDayMap() {
        return repeatDayMap;
    }
    /**setRepeatDayMap
     * @param repeatDayMap
     */
    public void setRepeatDayMap(TreeMap<Integer, String> repeatDayMap) {
        this.repeatDayMap = repeatDayMap;
    }

    private String label = "Alarm";

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    /**
     * key to be use in every bundle
     */
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
    private int pKeyDB= -1;
    /** default tts string **/
    private  String ttsString = "";
   /** default ringtone uri **/
    private  String ringtoneUri = "";
    /** tts active or not **/
    private boolean tts_active = false;
   /** ringtone active or not **/
    private boolean ringtone_active = false;
    /** snooze active or not **/
    private boolean snooze_active = false;

    private boolean temp_snooze_active = false;

    public boolean isTemp_snooze_active() {
        return temp_snooze_active;
    }

    public void setTemp_snooze_active(boolean temp_snooze_active) {
        this.temp_snooze_active = temp_snooze_active;
    }

    private boolean vibrate_active = false;

    public boolean isVibrate_active() {
        return vibrate_active;
    }

    public void setVibrate_active(boolean vibrate_active) {
        this.vibrate_active = vibrate_active;
    }

    /** will use to cancel snooze after main alarm **/
    private boolean cancel_snooze_alarm = false;

    public boolean isSnooze_active() {
        return snooze_active;
    }

    public void setSnooze_active(boolean snooze_active) {
        this.snooze_active = snooze_active;
    }

    private boolean repeating = false;

    public boolean isRepeating() {
        return repeating;
    }

    public void setRepeating(boolean repeating) {
        this.repeating = repeating;
    }

    public boolean isPlayed = false;

    public boolean isVibrated = false;

    private String eventDate = null;

    private String event_start_full_time = null;
    private String event_end_full_time = null;

    public String getEvent_start_full_time() {
        return event_start_full_time;
    }

    public void setEvent_start_full_time(String event_start_full_time) {
        this.event_start_full_time = event_start_full_time;
    }

    public String getEvent_end_full_time() {
        return event_end_full_time;
    }

    public void setEvent_end_full_time(String event_end_full_time) {
        this.event_end_full_time = event_end_full_time;
    }

    /**
     *calender to get the time
     */
    private Calendar calendar=Calendar.getInstance();


    /**
     * check if toggle on/off
     * @return
     */

    public boolean isAlarmOn()
    {
        return isAlarmOn;
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


    /** will set the tts string from the data base **/
    public void setTtsString(String tts){
        ttsString = tts;
    }

    public  void setTts_active(boolean val){
        tts_active = val;
    }
    public  void setRingtone_active(boolean val){
        ringtone_active = val;
    }
    public boolean getTts_active(){
        return tts_active;
    }
    public boolean getRingtone_active(){
        return ringtone_active;
    }
    /** will get the tts string **/
    public String getTtsString(){
        return ttsString;
    }
    public String getRingtoneUri() {
        return ringtoneUri;
    }

    public void setRingtoneUri(String ringtoneUri) {
        this.ringtoneUri = ringtoneUri;
    }

    /**
     * set the toggle on/off
     * @param val
     */
    public void setToggleOnOff(boolean val)
    {
        isAlarmOn=val;
    }

    public boolean getToggleOnOff(){
        return isAlarmOn;
    }
    /**
     *will set the time from the timepicker to alarmtime
     */
    public void setAlarmTime(String alarmTime) {
        this.alarmTime=alarmTime;
    }

    public void setEventDate(String date){
        eventDate = date;
    }

    public String getEventDate(){
        return eventDate;
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
    public long convertTimeInMS(String time , boolean repeatingVal , TreeMap<Integer , String> dayMap) {
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
         *converting to millisecond
         */
        alarmTimeInMS= newCalendar.getTimeInMillis();
        Log.i("UPDATE",String.valueOf(alarmTimeInMS));
        /**
         * if repeating then collect all the millisecond for the individual days
         * then return the lowest one for the alarm
         */
        if (repeatingVal) {
            TreeSet<Long> set = new TreeSet<>();
            for (int i = 0; i < 7; i++) {
                long repeatAlarmTime = alarmTimeInMS;
                /**if day present in the days map **/
                if (dayMap.containsKey(i)) {
                    Calendar currentCalendar = Calendar.getInstance();
                    int currDay = currentCalendar.get(Calendar.DAY_OF_WEEK);
                    /** default day counting starts from 1 but we are taking from 0
                     * sow decreasing by 1
                     */
                    currDay--;
                    /**if today is the repeating day but time is already passed or to day is not
                     * repeating day then calculate the times
                     */
                    if ((repeatAlarmTime < System.currentTimeMillis() && currDay == i)
                            || currDay != i) {
                        /**repeating day is in the future days but not in the next week **/
                        if (i > currDay) {
                            repeatAlarmTime += TimeUnit.MILLISECONDS.convert(i - currDay, TimeUnit.DAYS);
                        }
                        /** if repeating day is in the next week **/
                        else {
                            repeatAlarmTime += TimeUnit.MILLISECONDS.convert(7 - currDay, TimeUnit.DAYS);
                            repeatAlarmTime += TimeUnit.MILLISECONDS.convert(i, TimeUnit.DAYS);
                        }
                        set.add(repeatAlarmTime);
                    }
                    /**if today is the repeating day but time is not passed the this is the minimun
                     * so return this
                     */
                    else if(currDay == i){
                        set.add(repeatAlarmTime);
                    }
                }

            }
            if (!set.isEmpty())
            return set.first();
        }

        /** if not repeating then just calculate time **/

        /**
         *if the time less than current time
         */
        if (newCalendar.before(Calendar.getInstance())) {
            newCalendar.add(Calendar.DAY_OF_WEEK,1);
        }

        alarmTimeInMS= newCalendar.getTimeInMillis();
        return alarmTimeInMS;
    }

    public Long getMillisecondTime(String time , boolean repeatingVal , TreeMap<Integer , String> dayMap) {
        return convertTimeInMS(time , repeatingVal , dayMap);
    }

    /**
     * this will use to show toast msg of the ringing alarm time
     * @param
     * @return
     */
    public String getDurationBreakdown(long time) {
        Log.i(TAG, "getDurationBreakdown: time "+ time);
        if(time < 0) {
            throw new IllegalArgumentException("Duration must be greater than zero!");
        }
        long remainingMillis = time - Calendar.getInstance().getTimeInMillis();

        long days = TimeUnit.MILLISECONDS.toDays(remainingMillis);
        long daysMillis = TimeUnit.DAYS.toMillis(days);
        long hours = TimeUnit.MILLISECONDS.toHours(remainingMillis - daysMillis);
        long hoursMillis = TimeUnit.HOURS.toMillis(hours);
        long minutes = TimeUnit.MILLISECONDS.toMinutes(remainingMillis - daysMillis - hoursMillis);
        long minutesMillis = TimeUnit.MINUTES.toMillis(minutes);
        long seconds = TimeUnit.MILLISECONDS.toSeconds(remainingMillis - daysMillis - hoursMillis - minutesMillis);


        StringBuilder sb = new StringBuilder(64);
        sb.append(days);
        sb.append(":d ");
        sb.append(hours);
        sb.append(":H ");
        sb.append(minutes);
        sb.append(":M ");
        sb.append(seconds);
        sb.append(":S");
        Log.i(TAG, "getDurationBreakdown: duration "+sb);
        return(sb.toString());
    }

    /**
     *pusing the alarm to the alarmmanager
     */
    @SuppressLint("LongLogTag")
    public void scheduleAlarm(Context context , String time ,
                              boolean repeatingVal , TreeMap<Integer , String> dayMap)
    {
        alarmTimeInMS = convertTimeInMS(time , repeatingVal , dayMap);
        /**
         *intent for broadcast manager
         */
        intent = new Intent(context, AlarmReceiver.class);
        Bundle bundle=new Bundle();
        bundle.putParcelable(ALARM_KEY,this);
        intent.putExtra(ALARM_KEY,bundle);
        Log.i("the pkey in alarmconstaints",String.valueOf(this.getPKeyDB()));
        PendingIntent pi = PendingIntent.getBroadcast
                (context, this.getPKeyDB(), intent, PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        if (alarmManager == null)
            return;

//        if (android.os.Build.VERSION.SDK_INT >= 23) {
//            alarmManager.setAndAllowWhileIdle(AlarmManager.RTC_WAKEUP,
//                    alarmTimeInMS , pi);
//        } else

        if (android.os.Build.VERSION.SDK_INT >= 21) {

            alarmManager.setAlarmClock(new AlarmManager.AlarmClockInfo(alarmTimeInMS , pi) , pi);
            Log.e(TAG, "scheduleAlarm: alarm set" );
        }
        else if (android.os.Build.VERSION.SDK_INT>= 19){
            alarmManager.setExact(AlarmManager.RTC_WAKEUP, alarmTimeInMS , pi);
        }else{
            alarmManager.set(AlarmManager.RTC_WAKEUP, alarmTimeInMS , pi);
        }

        Log.i("alarm will ring in :",String.valueOf(getDurationBreakdown(alarmTimeInMS)));

    }

    /**
     * will set the alarm for snooze only
     * @param context
     * @param alarm
     */
    public void scheduleSnoozeAlarm(Context context , AlarmConstraints alarm){

        Calendar calendar = Calendar.getInstance();
        /** add the snooze time in current mills**/
        calendar.add(Calendar.MILLISECOND, 180000);
        long snoozeTimeInMs = calendar.getTimeInMillis();

        Intent snoozeIntent = new Intent(context, AlarmReceiver.class);
        Bundle bundle=new Bundle();
        bundle.putParcelable(alarm.ALARM_KEY,alarm);
        snoozeIntent.putExtra(alarm.ALARM_KEY,bundle);
        /** use pKeyDB to differentiate the pending intent **/
        PendingIntent snoozePendingIntent = PendingIntent.getBroadcast
                (context, (alarm.getPKeyDB()-alarm.getPKeyDB())-alarm.getPKeyDB(),
                           snoozeIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        Log.i("TAG", "scheduleSnoozeAlarm: "+alarm.getPKeyDB());
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        if (alarmManager == null)
            return;
//        Toast.makeText(context.getApplicationContext(), "alarm is snoozed :"+
//                        "1m",
//                Toast.LENGTH_SHORT).show();
//        if (android.os.Build.VERSION.SDK_INT >= 23) {
//            alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP,
//                    snoozeTimeInMs , snoozePendingIntent);
//        } else
        if (android.os.Build.VERSION.SDK_INT >= 19) {
            alarmManager.setExact(AlarmManager.RTC_WAKEUP, snoozeTimeInMs , snoozePendingIntent);
        } else {
            alarmManager.set(AlarmManager.RTC_WAKEUP, snoozeTimeInMs , snoozePendingIntent);
        }
        Log.i("TAG", "scheduleSnoozeAlarm: snooze set");
    }
    /**
     *will cancel the alarm
     */
    public void cancelAlarm(Context context , AlarmConstraints alarm){

        try {
                AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
                intent = new Intent(context, AlarmReceiver.class);
                PendingIntent pendingIntent = PendingIntent.getBroadcast
                        (context, alarm.getPKeyDB(), intent, PendingIntent.FLAG_NO_CREATE);
                Intent snoozeIntent = new Intent(context, AlarmReceiver.class);
                snoozeIntent.putExtra(alarm.ALARM_KEY , alarm.getPKeyDB());
            PendingIntent snoozePendingIntent = PendingIntent.getBroadcast
                    (context, (alarm.getPKeyDB()-alarm.getPKeyDB())-alarm.getPKeyDB(),
                            intent, PendingIntent.FLAG_NO_CREATE);

                Log.i("removefrmSchedule", "going to remove");
                if (alarmManager != null) {
                    if (snoozePendingIntent !=null){
                        alarmManager.cancel(snoozePendingIntent);
                        Log.i("TAG", "cancelAlarm: snoozependingintent canceled");
                    }
                    if (pendingIntent !=null){
                        alarmManager.cancel(pendingIntent);
                        Log.i("TAG", "cancelAlarm: mainalarm pendingintent canceled");
                    }
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
        parcel.writeString(ttsString);
        parcel.writeString(ringtoneUri);
        parcel.writeInt(tts_active ? 1 : 0);
        parcel.writeInt(ringtone_active ? 1 : 0);
        parcel.writeInt(snooze_active ? 1 : 0);
        parcel.writeInt(cancel_snooze_alarm ? 1 : 0);
        parcel.writeInt(vibrate_active ? 1 : 0);
        parcel.writeInt(repeating ? 1 : 0);
        parcel.writeString(label);
        parcel.writeInt(temp_snooze_active ? 1 : 0);
        parcel.writeInt(isPlayed ? 1 : 0);
        parcel.writeInt(isVibrated ? 1 : 0);

    }

    protected AlarmConstraints(Parcel in) {
        isAlarmOn = in.readInt() == 1;
        alarmTimeInMS = in.readLong();
        alarmTime = in.readString();
        standardTime=new StringBuilder(in.readString());
        pKeyDB = in.readInt();
        ttsString = in.readString();
        ringtoneUri = in.readString();
        tts_active = in.readInt() == 1;
        ringtone_active = in.readInt() == 1;
        snooze_active = in.readInt() == 1;
        cancel_snooze_alarm = in.readInt() == 1;
        vibrate_active = in.readInt() == 1;
        repeating = in.readInt() == 1;
        label = in.readString();
        temp_snooze_active = in.readInt() == 1;
        isPlayed = in.readInt() == 1;
        isVibrated = in.readInt() == 1;
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



    public String getTimeToRing(long timeInM){
        return getDurationBreakdown(timeInM);
    }
}