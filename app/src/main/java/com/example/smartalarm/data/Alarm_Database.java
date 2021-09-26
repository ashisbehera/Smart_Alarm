package com.example.smartalarm.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;
import com.example.smartalarm.data.AlarmContract.AlarmEntry;

public class Alarm_Database extends SQLiteOpenHelper {

    /**
     * tables to be created
     */

    private static final String ALARM_TABLE="alarm_table";
    private static final String RINGTONE_TABLE="ringtone_table";

    /** Name of the database file */
    private static final String DATABASE_NAME = "alarm.db";

    /**
     * Database version.
     */
    private static final int DATABASE_VERSION = 1;

    public Alarm_Database(@Nullable Context context) {
        super(context, DATABASE_NAME,null,DATABASE_VERSION);
    }


    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        // Create a String that contains the SQL statement to create the alarm table
        String SQL_CREATE_ALARM_TABLE =  "CREATE TABLE " + AlarmEntry.TABLE_NAME + " ("
                + AlarmEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + AlarmEntry.ALARM_NAME + " TEXT, "
                + AlarmEntry.ALARM_TIME + " TEXT NOT NULL, "
                + AlarmEntry.ALARM_VIBRATE + " INTEGER NOT NULL DEFAULT 0, "
                + AlarmEntry.ALARM_ACTIVE + " INTEGER NOT NULL DEFAULT 0, "
                + AlarmEntry.ALARM_SNOOZE+ " INTEGER NOT NULL DEFAULT 0);";

        // Execute the SQL statement
        sqLiteDatabase.execSQL(SQL_CREATE_ALARM_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }
}
