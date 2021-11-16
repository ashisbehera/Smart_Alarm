package com.example.smartalarm;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.LoaderManager;
import android.content.BroadcastReceiver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Vibrator;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Toast;
import android.os.Handler;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.smartalarm.data.AlarmContract.AlarmEntry;
import com.example.smartalarm.data.Alarm_Database;

import java.util.List;
import java.util.Locale;

public class CancelAlarm extends AppCompatActivity implements
        LoaderManager.LoaderCallbacks<Cursor> {

    /**
     *initialing the vibrator , ringtone and uri for ringtone
    */
    private TextToSpeech tts;
    Vibrator vibrator ;
    private Uri ring;
    private MediaPlayer ringtonePlay;
   // private Ringtone ringtone;

    @SuppressLint("LongLogTag")
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.cancel_alarm);
        /**
         * for notification future use
         */
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1) {
//            setShowWhenLocked(true);
//            setTurnScreenOn(true);
//        } else {
//            getWindow().addFlags(WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD |
//                    WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED |
//                    WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON |
//                    WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
//        }


        Button cancelb = findViewById(R.id.cancel_button);
       // vibrator = (Vibrator) getApplicationContext().getSystemService(Context.VIBRATOR_SERVICE);
       // ring = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
//
        tts = new TextToSpeech(this, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if (status == TextToSpeech.SUCCESS){
                    int result = tts.setLanguage(Locale.US);
                    if (result == TextToSpeech.LANG_MISSING_DATA ||
                            result == TextToSpeech.LANG_NOT_SUPPORTED){
                        Toast.makeText(getApplicationContext(), "tts language not supported",
                                Toast.LENGTH_SHORT).show();
                        Log.i("tts language", "tts language not supported ");
                    }
                }else {
                    Toast.makeText(getApplicationContext(), "tts  initialization failed",
                            Toast.LENGTH_SHORT).show();
                    Log.i("tts initialization", "tts  initialization failed");
                }

            }
        });

        /**
         * getting intent from pending intent
         */
        Intent intent=getIntent();
        /**
         * getting bundle from from the intent
         */
        AlarmConstraints alarm=(AlarmConstraints)intent.getBundleExtra
                (AlarmConstraints.ALARM_KEY).getParcelable(AlarmConstraints.ALARM_KEY);
        try {
            /**
             * if alarm is not null then play the alarm
             */
            if(alarm!=null) {
                playAlarm((AlarmConstraints) alarm);
                Log.i("successfully  alarm played" , " alarm played");
            }
        }catch (Exception e){
            Log.i("bundle/alarm null Exception" , " bundle/alarm is null");
        }
//
//        Toast.makeText(getApplicationContext(), "pkey in cancel alarm:"+
//                String.valueOf(alarm.getPKeyDB()) ,
//                Toast.LENGTH_SHORT).show();


        /**
         * cancel the alarm
         */
        cancelb.setOnClickListener(view -> {
            cancelAlarmButton(alarm);
            Log.i("on cancelb" , "successfully canceled alarm");
           // AlarmWakeLock.releaseCpuLock();
            finish();
        });


    }


    /**
     * removing alarm from schedule and updating the service
     * @param alarm
     */
    @SuppressLint("LongLogTag")
    public void cancelAlarmButton(AlarmConstraints alarm){

        stopVib_ringtone();
        //stop_tts();
        Log.i("stop vibration and ringtone" , "successfully stopped");
        removingAlarm(alarm);
        Log.i(" alarm removed " , "successfully alarm removed");
        ScheduleService.updateAlarmSchedule(getApplicationContext());
    }

    @SuppressLint("MissingSuperCall")
    @Override
    public void onNewIntent(Intent intent) {
       // super.onNewIntent(intent);
        Log.i("onNewIntent", "beforeKeyCheck");

        tts = new TextToSpeech(this, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if (status == TextToSpeech.SUCCESS){
                    int result = tts.setLanguage(Locale.US);
                    if (result == TextToSpeech.LANG_MISSING_DATA ||
                            result == TextToSpeech.LANG_NOT_SUPPORTED){
                        Toast.makeText(getApplicationContext(), "tts language not supported",
                                Toast.LENGTH_SHORT).show();
                        Log.i("tts language", "tts language not supported ");
                    }
                }else {
                    Toast.makeText(getApplicationContext(), "tts  initialization failed",
                            Toast.LENGTH_SHORT).show();
                    Log.i("tts initialization", "tts  initialization failed");
                }

            }
        });

        if (intent.hasExtra(AlarmConstraints.ALARM_KEY)) {

            AlarmConstraints alarm = (AlarmConstraints) intent.getBundleExtra
                    (AlarmConstraints.ALARM_KEY).get(AlarmConstraints.ALARM_KEY);
            if (alarm == null)
                return;

            Log.i("onNewIntent", "called");

                playAlarm(alarm);

        }
    }

    /**
     * @param alarm
     * play the alarm and ringtone
     */
    private void playAlarm(final AlarmConstraints alarm) {
        if(alarm == null) {
            return;
        }

//        if(alarm.getTts_active() && alarm.getRingtone_active()){
//            ttsSpeak(alarm);
//            Thread.sleep(500);
//            playRingtone(alarm);
//        }
//         if(alarm.getTts_active())
//            ttsSpeak(alarm);
//         else if(alarm.getRingtone_active()){
            playRingtone(alarm);
        //}
    }

    /**will play tts **/
    private void ttsSpeak(AlarmConstraints alarm){
       // vibrator.vibrate(20000);
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                tts.speak(alarm.getTtsString(), TextToSpeech.QUEUE_FLUSH, null);
            }
        }, 1000);
    }
/**will stop the tts **/
    private void stop_tts(){
        tts.stop();
        tts.shutdown();
    }

    private void playRingtone(AlarmConstraints alarm){
        ring = Uri.parse(alarm.getRingtoneUri());
        Toast.makeText(getApplicationContext(), "uri  :"+alarm.getRingtoneUri(),
                Toast.LENGTH_SHORT).show();
        ringtonePlay = MediaPlayer.create(getApplicationContext() , ring);
        ringtonePlay.start();
    }

    /**
     * stop the vibration and ringtone
     */
    private void stopVib_ringtone() {
        //vibrator.cancel();
        ringtonePlay.stop();
    }

    /**
     * removing alarm form the schedule
     * @param alarm
     */
    private void removingAlarm(AlarmConstraints alarm) {
        /**
         * set the toggle alarm (not to repeat the alarm)
         */
        setToggleOnOfAfterAlarm(alarm , 0);
        Log.i("toggled off","toggle" );
        alarm.cancelAlarm(getApplicationContext());
        Log.i("this alarm has canceled","alarm");


    }

    /**
     * using the primary key of the alarm update the toggle value in the data base
     * @param alarm
     * @param zero
     */
    private void setToggleOnOfAfterAlarm(AlarmConstraints alarm,int zero) {

        Log.i("the pkey in cancel alrm",String.valueOf(alarm.getPKeyDB()));
        update_database(alarm.getPKeyDB(),zero);
        Log.i("database updated","for toggle off/on" );
    }

    /**
     * updating the data base using primary key
     * @param id
     * @param zero
     */
    private void update_database(int id , int zero){
        int alarm_on_off = zero;
        ContentValues values = new ContentValues();
        values.put(AlarmEntry.ALARM_ACTIVE, alarm_on_off);
        Uri currentPetUri = ContentUris.withAppendedId(AlarmEntry.CONTENT_URI ,id);
        getContentResolver().update(currentPetUri , values, null, null);
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
