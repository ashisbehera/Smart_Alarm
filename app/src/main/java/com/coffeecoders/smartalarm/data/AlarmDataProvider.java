package com.coffeecoders.smartalarm.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.coffeecoders.smartalarm.data.AlarmContract.AlarmEntry;
public class AlarmDataProvider extends ContentProvider {
    private static final String TAG = "AlarmDataProvider";
    /** URI matcher code for the content URI for the alarm table
     * */
    private static final int ALARMS = 50;

    private static final int CAL_EVENTS = 53;

    /** URI matcher code for the content URI for the single alarm in the alarm table
     * */
    private static final int ALARM_ID = 51;
    private static final int CAL_ID = 54;
    /** URI matcher code for the content URI for the ringtone table
     * */
    private static final int RINGTONE = 52;

    private static final UriMatcher alarmUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);


    static {

        /**
         *  This URI is used to provide access to MULTIPLE rows in alarm table
         */
        alarmUriMatcher.addURI(AlarmContract.CONTENT_AUTHORITY,AlarmContract.PATH_ALARM,ALARMS);

        alarmUriMatcher.addURI(AlarmContract.CONTENT_AUTHORITY,AlarmContract.PATH_CAL_EVENTS,CAL_EVENTS);
        alarmUriMatcher.addURI(AlarmContract.CONTENT_AUTHORITY,AlarmContract.PATH_CAL_EVENTS+ "/#",CAL_ID);
        /**
         *  This URI is used to provide access to MULTIPLE rows in ringtone table
         */
        alarmUriMatcher.addURI(AlarmContract.CONTENT_AUTHORITY,AlarmContract.PATH_RINGTONE,RINGTONE);
        /**
         * This URI is used to provide access to ONE single row
         */
        alarmUriMatcher.addURI(AlarmContract.CONTENT_AUTHORITY,AlarmContract.PATH_ALARM+ "/#",ALARM_ID);

    }

    /**
     * object of alarm data base class
     */
    private Alarm_Database aDatabase;

    @Override
    public boolean onCreate() {
        /**
         * initializing data base object
         */
        aDatabase = new Alarm_Database(getContext());
        return true;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection,
                        @Nullable String selection, @Nullable String[] selectionArgs,
                        @Nullable String sortOrder) {

        /**
         * initializing sqlitedatabase
         */
        SQLiteDatabase database = aDatabase.getReadableDatabase();
        /**
         * initializing cursor
         */
        Cursor cursor;

        /**
         * will compare the uri the with the ALARMS AND ALARM_ID
         */
        int match = alarmUriMatcher.match(uri);

        switch (match) {
            /**
             * this will query the whole alarm table
             */
            case ALARMS:

                cursor = database.query(AlarmEntry.TABLE_NAME, projection, selection, selectionArgs,
                        null, null, sortOrder);
                break;
            /**
             * this will query the whole ringtone table
             */
            case RINGTONE:

                cursor = database.query(AlarmEntry.RINGTONE_TABLE, projection, selection, selectionArgs,
                        null, null, sortOrder);
                break;

            /**
             * this will query the single row of the data base
             */
            case ALARM_ID:

                selection = AlarmEntry._ID + "=?";
                /**
                 * extracting the row id from the uri
                 */
                selectionArgs = new String[] { String.valueOf(ContentUris.parseId(uri)) };


                cursor = database.query(AlarmEntry.TABLE_NAME, projection, selection, selectionArgs,
                        null, null, sortOrder);
                break;

            case CAL_ID:
                Log.e(TAG, "query: "+ "cal events type" );
                selection = AlarmEntry._ID + "=?";
                /**
                 * extracting the row id from the uri
                 */
                selectionArgs = new String[] { String.valueOf(ContentUris.parseId(uri)) };


                cursor = database.query(AlarmEntry.CAL_EVENTS_TABLE_NAME, projection, selection, selectionArgs,
                        null, null, sortOrder);
                break;

            default:
                throw new IllegalArgumentException("Cannot query unknown URI " + uri);
        }

        /**
         *  If the data at this URI changes, then we know we need to update the Cursor.
         */
        cursor.setNotificationUri(getContext().getContentResolver(), uri);

        /**
         * Return the cursor
         */
        return cursor;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        final int match = alarmUriMatcher.match(uri);
        /**
         * will return the uri which is matched
         */
        switch (match) {
            case ALARMS:
                return AlarmEntry.CONTENT_LIST_TYPE;
            case ALARM_ID:
                return AlarmEntry.CONTENT_ITEM_TYPE;
            case CAL_EVENTS:
                return AlarmEntry.CONTENT_ITEM_TYPE_CAL;
            default:
                throw new IllegalStateException("Unknown URI " + uri + " with match " + match);
        }
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues contentValues) {
        final int match = alarmUriMatcher.match(uri);
        switch (match) {
            case ALARMS:
                return insertAlarms(uri, contentValues);
            case CAL_EVENTS:
                return insertCal_Events(uri , contentValues);
            default:
                throw new IllegalArgumentException("Insertion failed for " + uri);
        }
    }

    private Uri insertCal_Events(Uri uri, ContentValues contentValues) {
        SQLiteDatabase database = aDatabase.getWritableDatabase();

        long id = database.insert(AlarmEntry.CAL_EVENTS_TABLE_NAME, null, contentValues);

        if (id == -1) {
            /**
             * failed to insert
             */
            return null;
        }

        /**
         * Notify all listeners that the data has changed for the alarm content URI
         */
        getContext().getContentResolver().notifyChange(uri, null);

        /**
         * Return the new URI with the ID of the newly inserted row appended at the end
         */
        return ContentUris.withAppendedId(uri, id);
    }

    private Uri insertAlarms(Uri uri, ContentValues contentValues) {

        SQLiteDatabase database = aDatabase.getWritableDatabase();

        /**
         * Insert the new alarm with the given values
         */
        long id = database.insert(AlarmEntry.TABLE_NAME, null, contentValues);

        if (id == -1) {
            /**
             * failed to insert
             */
            return null;
        }

        /**
         * Notify all listeners that the data has changed for the alarm content URI
         */
        getContext().getContentResolver().notifyChange(uri, null);

        /**
         * Return the new URI with the ID of the newly inserted row appended at the end
         */
        return ContentUris.withAppendedId(uri, id);
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        SQLiteDatabase database = aDatabase.getWritableDatabase();

        /**
         * Track the number of rows that were deleted
         */
        int rowsDeleted;

        final int match = alarmUriMatcher.match(uri);
        switch (match) {
            case ALARMS:
                /**
                 * will delete all the rows from the data base
                 */
                rowsDeleted = database.delete(AlarmEntry.TABLE_NAME, selection, selectionArgs);
                break;
            case ALARM_ID:
                /**
                 *  Delete a single row given by the ID in the URI
                 */
                selection = AlarmEntry._ID + "=?";
                selectionArgs = new String[] { String.valueOf(ContentUris.parseId(uri)) };
                rowsDeleted = database.delete(AlarmEntry.TABLE_NAME, selection, selectionArgs);
                break;
            default:
                throw new IllegalArgumentException("Deletion failed for " + uri);
        }

        /**
         * If 1 or more rows were deleted, then notify all listeners that the data at the
         *  given URI has changed.
         *  if 0 means no rows has deleted
         */
        if (rowsDeleted != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }


        return rowsDeleted;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues contentValues,
                      @Nullable String selection, @Nullable String[] selectionArgs) {
        final int match = alarmUriMatcher.match(uri);
        switch (match) {
            case ALARMS:
                return updateAlarm(uri, contentValues, selection, selectionArgs);
            case ALARM_ID:

                selection = AlarmEntry._ID + "=?";
                selectionArgs = new String[] { String.valueOf(ContentUris.parseId(uri)) };
                return updateAlarm(uri, contentValues, selection, selectionArgs);
            default:
                throw new IllegalArgumentException("Update failed for " + uri);
        }
    }

    private int updateAlarm(Uri uri, ContentValues contentValues, String selection, String[] selectionArgs) {
        SQLiteDatabase database = aDatabase.getWritableDatabase();


        int rowsUpdated = database.update(AlarmEntry.TABLE_NAME, contentValues, selection, selectionArgs);

        if (rowsUpdated != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }

        return rowsUpdated;
    }
}