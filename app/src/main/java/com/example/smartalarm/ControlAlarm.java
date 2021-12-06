package com.example.smartalarm;

import android.annotation.SuppressLint;
import android.app.LoaderManager;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.Ringtone;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.Vibrator;
import android.speech.tts.TextToSpeech;
import android.speech.tts.UtteranceProgressListener;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.example.smartalarm.data.AlarmContract;

import java.io.IOException;
import java.util.HashMap;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

public class ControlAlarm extends AppCompatActivity implements
        LoaderManager.LoaderCallbacks<Cursor> {
    /**
     *initialing the vibrator , ringtone and uri for ringtone
     */


    Uri ring;
    PlayMedia playMedia;
    public ControlAlarm(){
        playMedia = PlayMedia.getMediaPlayerInstance();
    }


    /**
     * removing alarm from schedule and updating the service
     * @param alarm
     */
    @SuppressLint("LongLogTag")
    public void stopAlarm(AlarmConstraints alarm , Context context){
        Log.i("stop alarm", "inside stop alarm ");

        Log.i("stop vibration and ringtone" , "successfully stopped");
        removingAlarm(alarm , context);
        Log.i(" alarm removed " , "successfully alarm removed");
        ScheduleService.updateAlarmSchedule(context.getApplicationContext());

        if(alarm.getTts_active() && alarm.getRingtone_active()){
            stopRingtoneR();
            stop_tts();
            stopVibrateV();
        }
        else if(alarm.getTts_active()){
            stop_tts();
            stopVibrateV();
        }
        else if(alarm.getRingtone_active()){
            stopRingtoneR();
            stopVibrateV();
        }

    }



    /**
     * @param alarm
     * play the alarm and ringtone
     */
    @RequiresApi(api = Build.VERSION_CODES.P)
    public void playAlarm(final AlarmConstraints alarm , Context context) throws IOException {
        if(alarm == null) {
            return;
        }
        Log.i("pkey", "pkey - "+alarm.getPKeyDB());
          vibrateV(context);
        if(alarm.getTts_active() && alarm.getRingtone_active()){
           playMedia.playRingtoneTts(alarm , context);
        }
        else if(alarm.getTts_active()){
            ttsSpeak(alarm , context);
        }
        else if(alarm.getRingtone_active()){
            playRingtone(alarm  , context);
        }
    }

    /**will play tts **/
    public void ttsSpeak(AlarmConstraints alarm , Context context){
        playMedia.mediaPlayTts(alarm , context);

    }

    /**will stop the tts **/
    public void stop_tts(){
       playMedia.stop_tts();
    }


    public void playRingtone(AlarmConstraints alarm , Context context) throws IOException {


            Log.i("playringtone", "inside playringtone");
            ring = Uri.parse(alarm.getRingtoneUri());
            playMedia.mediaPlayRingtone(context , ring);

    }


    @RequiresApi(api = Build.VERSION_CODES.O)
    public void vibrateV(Context context){
        playMedia.vibrate(context.getApplicationContext());
    }

    public void stopVibrateV(){
        playMedia.stopVibrate();
    }

    /**
     * stop the vibration and ringtone
     */
    public void stopRingtoneR() {
        playMedia.stopRingtone();
    }

    /**
     * removing alarm form the schedule
     * @param alarm
     */
    public void removingAlarm(AlarmConstraints alarm , Context context) {
        /**
         * set the toggle alarm (not to repeat the alarm)
         */

        /** if snooze is active then don't turn off the toggle **/
        if (!alarm.isSnooze_active())
        setToggleOnOfAfterAlarm(alarm , 0 , context);
        Log.i("toggled off","toggle" );
        alarm.cancelAlarm(context.getApplicationContext() , alarm);
        Log.i("this alarm has canceled","alarm");


    }

    /**
     * using the primary key of the alarm update the toggle value in the data base
     * @param alarm
     * @param zero
     * @param context
     */
    public void setToggleOnOfAfterAlarm(AlarmConstraints alarm, int zero, Context context) {

        Log.i("the pkey in cancel alrm",String.valueOf(alarm.getPKeyDB()));
        Toast.makeText(context.getApplicationContext(), "pkey -  :"+alarm.getPKeyDB(),
                Toast.LENGTH_SHORT).show();
        update_database(alarm.getPKeyDB(),zero , context);
        Log.i("database updated","for toggle off/on" );
    }

    /**
     * updating the data base using primary key
     * @param id
     * @param zero
     * @param context
     */
    public void update_database(int id, int zero, Context context){
        int alarm_on_off = zero;
        ContentValues values = new ContentValues();
        values.put(AlarmContract.AlarmEntry.ALARM_ACTIVE, alarm_on_off);
        Uri currentPetUri = ContentUris.withAppendedId(AlarmContract.AlarmEntry.CONTENT_URI ,id);
        context.getContentResolver().update(currentPetUri , values, null, null);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        return null;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }

}
