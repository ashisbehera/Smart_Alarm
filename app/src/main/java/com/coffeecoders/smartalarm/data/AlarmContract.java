package com.coffeecoders.smartalarm.data;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;

public class AlarmContract {

    private AlarmContract(){

    }
    
    public static final String CONTENT_AUTHORITY = "com.coffeecoders.smartalarm";

    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    public static final String PATH_ALARM = "alarms";

    public static final String PATH_RINGTONE = "table_ringtone";

    public static final String PATH_CAL_EVENTS = "calender_events";

    public static final class AlarmEntry implements BaseColumns{

        /** The content URI to access the alarm data in the provider
         * */
        public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI, PATH_ALARM);

        public static final Uri CAL_EVENTS_CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI, PATH_CAL_EVENTS);
        /** The content URI to access the ringtone data in the provider
         * */
        public static final Uri RINGTONE_CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI, PATH_RINGTONE);

        /*** for a list of alarms.
         */
        public static final String CONTENT_LIST_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_ALARM;

        /***  for a single alarm.
         */
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_ALARM;

        public static final String CONTENT_ITEM_TYPE_CAL =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_CAL_EVENTS;

        /** Name of database table for alarms
         * */
        public final static String TABLE_NAME = "alarms";

        public final static String CAL_EVENTS_TABLE_NAME = "cal_events";
        /** Name of database table for ringtone
         * */
        public final static String RINGTONE_TABLE = "table_ringtone";

        /*** Unique ID number for the alarms (only for use in the database table).
         */
        public final static String _ID = BaseColumns._ID;

        /*** Name/label of the alarm.
         */
        public final static String ALARM_NAME ="name";

        /*** TIME of the alarm.
         */
        public final static String ALARM_TIME = "time";
        /** vibrating on or not **/
        public final static String ALARM_VIBRATE = "vibrate";
        /** alarm active or not **/
        public final static String ALARM_ACTIVE="alarm_active";
        /** alarm snooze active or not **/
        public final static String ALARM_SNOOZE="alarm_snooze";
        /** tts string for data base **/
        public final static String TTS_STRING="tts_string";
        /** this string will save in the alarm table **/
        public final static String RINGTONE_STRING="alarm_ringtone_uri";
        /** this RINGTONE NAME will save in the alarm table **/
        public final static String ALARM_RINGTONE_NAME="alarm_ringtone_name";
        /** repeat_days will save in the alarm table **/
        public final static String ALARM_REPEAT_DAYS="alarm_repeat_days";
        /** check if repeating is active or not **/
        public final static String IS_REPEATING="is_repeating";
        /** check if tts is active or not **/
        public final static String TTS_ACTIVE="tts_active";
        /** for ringtone table **/
        public final static String RINGTONE_ID= BaseColumns._ID;
        /** for ringtone table **/
        public final static String RINGTONE_NAME="ringtone_name";
        /** for ringtone table **/
        public final static String RINGTONE_URI="ringtone_uri";
        /** check if ringtone is active or not **/
        public final static String RINGTONE_ACTIVE="ringtone_active";

        public static final String CAL_ACC_NAME = "cal_acc_name";

        public static final String CAL_EVENT_DATE = "cal_event_date";
        public static final String CAL_S_FULL_T = "cal_s_full_time";
        public static final String CAL_E_FULL_T = "cal_e_full_time";
    }
}
