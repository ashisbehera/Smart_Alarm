package com.example.smartalarm.data;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;

public class AlarmContract {

    private AlarmContract(){

    }

    public static final String CONTENT_AUTHORITY = "com.example.smartalarm";

    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    public static final String PATH_ALARM = "alarms";

    public static final class AlarmEntry implements BaseColumns{

        /** The content URI to access the alarm data in the provider
         * */
        public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI, PATH_ALARM);

        /*** for a list of alarms.
         */
        public static final String CONTENT_LIST_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_ALARM;

        /***  for a single alarm.
         */
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_ALARM;

        /** Name of database table for alarms
         * */
        public final static String TABLE_NAME = "alarms";

        /*** Unique ID number for the alarms (only for use in the database table).
         */
        public final static String _ID = BaseColumns._ID;

        /*** Name/label of the alarm.
         */
        public final static String ALARM_NAME ="name";

        /*** TIME of the alarm.
         */
        public final static String ALARM_TIME = "time";

        public final static String ALARM_VIBRATE = "vibrate";

        public final static String ALARM_ACTIVE="alarm_active";

        public final static String ALARM_SNOOZE="alarm_snooze";
        /** tts string for data base **/
        public final static String TTS_STRING="tts_string";

        public final static String TTS_ACTIVE="tts_active";

        public final static String ALARM_SNOOZE_ACTIVE="alarm_snooze_active";

        public final static String RINGTONE_ID="ringtone_id";

        public final static String RINGTONE_NAME="ringtone_name";

        public final static String RINGTONE_PATH="ringtone_path";

        public final static String RINGTONE_ACTIVE="ringtone_active";

        public static  final int ALARM_ACTIVE_OFF = 0;

        public static  final int ALARM_ACTIVE_ON = 1;

        public  static  final int ALARM_VIBRATE_OFF = 0;

        public  static  final int ALARM_VIBRATE_ON = 1;

        public  static  final int ALARM_SNOOZE_OFF = 0;

        public  static  final int ALARM_SNOOZE_ON = 1;

    }
}
