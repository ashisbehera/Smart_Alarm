package com.example.smartalarm.data;

import android.annotation.SuppressLint;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import androidx.annotation.Nullable;

import com.example.smartalarm.AlarmConstraints;
import com.example.smartalarm.data.AlarmContract.AlarmEntry;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.StreamCorruptedException;
import java.lang.ref.WeakReference;
import java.util.Arrays;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

public class Alarm_Database extends SQLiteOpenHelper {



    /** Name of the database file */
    private static final String DATABASE_NAME = "alarm.db";
    private static Alarm_Database AlarmDatabase=null;
    private  SQLiteDatabase myDatabase=null;
    /**
     * list for the alarm
     */
    private List<AlarmConstraints>alarmList=new LinkedList<>();


    private Context context;

    /**
     * Database version.
     */
    private static final int DATABASE_VERSION = 1;


    @SuppressLint("LongLogTag")
    /**
     * will use to send instance of the database
     */
    public static synchronized Alarm_Database getInstance(Context context)
    {
        Log.i("in get instance method" , "getinstance");
        if(AlarmDatabase==null)
        {
            AlarmDatabase=new Alarm_Database(context);
        }
        return AlarmDatabase;
    }

    /**
     * Alarm_Database constructor
     * @param context
     */
    @SuppressLint("LongLogTag")
    public Alarm_Database(@Nullable Context context) {
        super(context, DATABASE_NAME,null,DATABASE_VERSION);
        this.context=context;

        /**
         * check if database exist or not
         */
        if(checkifDBExists()) {
            return;
        }

        myDatabase=getWritableDatabase();

    }

    /**
     * check if data exist or not
     * @return
     */
    private boolean checkifDBExists()
    {
        File file=context.getDatabasePath(DATABASE_NAME);
        if(myDatabase==null) {
            try {
                myDatabase = SQLiteDatabase.openDatabase
                        (file.getAbsolutePath(), null, SQLiteDatabase.OPEN_READWRITE);
                Log.i("Confirmed", "DB exists");
                return true;
            } catch (Exception database) {
                Log.e("SQLException", "No database");
                return false;
            }
        }
        return true;
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

    /**
     * will get the alarms from the database and will return as list
     * @return
     */
    @SuppressLint("Range")
    public List<AlarmConstraints> getAlarmsFromDataBase()
    {
        /**
         * which columns cursor will to move
         */
        String [] columns = new String[]{AlarmEntry._ID,AlarmEntry.ALARM_NAME,
                AlarmEntry.ALARM_TIME,AlarmEntry.ALARM_VIBRATE,AlarmEntry.ALARM_ACTIVE,AlarmEntry.ALARM_SNOOZE};
        Log.i("columns arr created" , " string arr");
        /**
         * set the cursor
         */
        Cursor cursor= myDatabase.query(AlarmEntry.TABLE_NAME,columns,
                null,null,null,null,null);
        Log.i("cursor created" , "cursor");
        AlarmConstraints [] alarms=new AlarmConstraints [cursor.getCount()];

        /**
         * move the cursor and get all alarms to the list
         */
        if(cursor.moveToFirst())
        {
            int i=0;
            while(!cursor.isAfterLast())
            {
                alarms[i]=new AlarmConstraints();
                alarms[i].setPKeyDB(cursor.getInt(cursor.getColumnIndex(AlarmEntry._ID)));
                alarms[i].setLabel(cursor.getString(cursor.getColumnIndex(AlarmEntry.ALARM_NAME)));
                alarms[i].setAlarmTime(cursor.getString(cursor.getColumnIndex(AlarmEntry.ALARM_TIME)));

                if(cursor.getInt(cursor.getColumnIndex(AlarmEntry.ALARM_ACTIVE))==1)
                    alarms[i].setToggleOnOff(true);
                else
                    alarms[i].setToggleOnOff(false);

                i++;
                cursor.moveToNext();
            }
        }
        cursor.close();


        alarmList.clear();
        alarmList.addAll(Arrays.asList(alarms));

        return alarmList;
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }
}
