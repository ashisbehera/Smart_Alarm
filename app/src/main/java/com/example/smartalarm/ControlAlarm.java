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
    private TextToSpeech tts;
    Vibrator vibrator ;
    Uri ring;

    public ControlAlarm(Context context){
        tts = new TextToSpeech(context.getApplicationContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if (status == TextToSpeech.SUCCESS){
                    int result = tts.setLanguage(Locale.US);
                    if (result == TextToSpeech.LANG_MISSING_DATA ||
                            result == TextToSpeech.LANG_NOT_SUPPORTED){
                        Toast.makeText(context.getApplicationContext(), "tts language not supported",
                                Toast.LENGTH_SHORT).show();
                        Log.i("tts language", "tts language not supported ");
                    }
                }else {
                    Toast.makeText(context.getApplicationContext(), "tts  initialization failed",
                            Toast.LENGTH_SHORT).show();
                    Log.i("tts initialization", "tts  initialization failed");
                }

            }
        });



        vibrator = (Vibrator) context.getApplicationContext().getSystemService(Context.VIBRATOR_SERVICE);
    }

//    public static ControlAlarm getInstance() {
//        if (INSTANCE == null) {
//            INSTANCE = new ControlAlarm(Context);
//        }
//        return INSTANCE;
//    }



    /**
     * removing alarm from schedule and updating the service
     * @param alarm
     */
    @SuppressLint("LongLogTag")
    public void stopAlarm(AlarmConstraints alarm , Context context){
        Log.i("stop alarm", "inside stop alarm ");
        stopVib_ringtone();
        stop_tts();
        Log.i("stop vibration and ringtone" , "successfully stopped");
        removingAlarm(alarm , context);
        Log.i(" alarm removed " , "successfully alarm removed");
        ScheduleService.updateAlarmSchedule(context.getApplicationContext());
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
        vibrator.vibrate(20000);
//        if(alarm.getTts_active() && alarm.getRingtone_active()){
//            playRingtone(alarm , context);
//            tts.setOnUtteranceProgressListener(new UtteranceProgressListener() {
//
//
//                @Override
//                public void onStart(String s) {
//                    ringtonePlay.pause();
//                }
//
//                @Override
//                public void onDone(String s) {
//                    ringtonePlay.start();
//                }
//
//                @Override
//                public void onError(String s) {
//
//                }
//            });
//            HashMap<String, String> params = new HashMap<>();
//            params.put(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, "stringId");
//            final Handler handler = new Handler();
//            handler.postDelayed(new Runnable() {
//                @Override
//                public void run() {
//                    tts.speak(alarm.getTtsString(), TextToSpeech.QUEUE_FLUSH, params);
//                }
//            }, 1000);
//
//
//        }
        if(alarm.getTts_active()){
            ttsSpeak(alarm , context);
        }
        else if(alarm.getRingtone_active()){
            playRingtone(alarm  , context);
        }
    }

    /**will play tts **/
    public void ttsSpeak(AlarmConstraints alarm , Context context){

        final Handler handler = new Handler();
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                tts.speak(alarm.getTtsString(), TextToSpeech.QUEUE_FLUSH, null);
                handler.postDelayed(this , 5000);
            }
        };
        handler.postDelayed(runnable , 1000);
        TimerTask ttsTimer = new TimerTask() {
            @Override
            public void run() {
                handler.removeCallbacks(runnable);
            }
        };
        Timer timer = new Timer();
        timer.schedule(ttsTimer, 10000);

    }
    /**will stop the tts **/
    public void stop_tts(){
        if (tts.isSpeaking())
        tts.stop();
        tts.shutdown();
    }

    public void playRingtone(AlarmConstraints alarm , Context context) throws IOException {


            Log.i("playringtone", "inside playringtone");
            ring = Uri.parse(alarm.getRingtoneUri());
            Toast.makeText(context.getApplicationContext(), "uri  :"+alarm.getRingtoneUri(),
                    Toast.LENGTH_SHORT).show();
            PlayMedia playMedia = PlayMedia.getMediaPlayerInstance();
            playMedia.playRingtone(context , ring);
    }

    /**
     * stop the vibration and ringtone
     */
    public void stopVib_ringtone() {
        vibrator.cancel();
        PlayMedia playMedia = PlayMedia.getMediaPlayerInstance();
        playMedia.stopringtone();
    }

    /**
     * removing alarm form the schedule
     * @param alarm
     */
    public void removingAlarm(AlarmConstraints alarm , Context context) {
        /**
         * set the toggle alarm (not to repeat the alarm)
         */
        setToggleOnOfAfterAlarm(alarm , 0 , context);
        Log.i("toggled off","toggle" );
        alarm.cancelAlarm(context.getApplicationContext());
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

//
}
