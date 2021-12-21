package com.example.smartalarm;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;

import android.os.Build;
import android.os.Handler;

import android.os.VibrationEffect;
import android.os.Vibrator;
import android.speech.tts.TextToSpeech;
import android.speech.tts.UtteranceProgressListener;
import android.util.Log;
import android.widget.Toast;


import androidx.annotation.RequiresApi;

import com.example.smartalarm.data.AlarmContract;

import java.io.IOException;
import java.util.HashMap;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

public class PlayMedia {
    private static final String TAG = "PlayMedia";
    private static PlayMedia Instance;
    Vibrator vibrator;
    TextToSpeech tts;
    MediaPlayer ringtonePlay;
    AudioManager audioManager;
    Uri ringM;
    HashMap<String, String> params;
    Timer ringTimer,ttsTimer,ttsRingTimer, vibrateTimer;
    boolean isRingTimerActive , isTtsTimerActive , isTtsRingTimerActive , isVibrateActive;
    static PlayMedia getMediaPlayerInstance() {
        if (Instance == null) {
            return Instance = new PlayMedia();
        }
        return Instance;
    }

    /**
     * will play only ringtone
     * @param context
     * @param ring
     * @param alarm
     * @throws IOException
     */
    public void mediaPlayRingtone(Context context, Uri ring, AlarmConstraints alarm) throws IOException {
        ringtonePlay = new MediaPlayer();
        ringtonePlay.setDataSource(context.getApplicationContext(), ring);
        audioManager = (AudioManager)
                context.getApplicationContext().getSystemService(Context.AUDIO_SERVICE);
        if (audioManager.getStreamVolume(AudioManager.STREAM_ALARM) != 0) {
            ringtonePlay.setAudioStreamType(AudioManager.STREAM_ALARM);
            ringtonePlay.setLooping(true);
            ringtonePlay.prepare();
            ringtonePlay.start();
            TimerTask ringtoneTimerTask = new TimerTask() {
                @Override
                public void run() {
                    stopRingtone();
                    alarm.cancelAlarm(context.getApplicationContext() , alarm);

                    if (alarm.isSnooze_active()) {
                        alarm.scheduleSnoozeAlarm(context.getApplicationContext(), alarm);

                    }

                    if (!alarm.isSnooze_active() && !alarm.isRepeating()){
                        update_db(alarm.getPKeyDB(),0 , context.getApplicationContext());
                    }
                    ScheduleService.updateAlarmSchedule(context.getApplicationContext());
                    Intent dataChangeIntent = new Intent("com.example.smartalarm.dataChangeListener");
                    context.getApplicationContext().sendBroadcast(dataChangeIntent);
                }
            };
            ringTimer = new Timer();
            ringTimer.schedule(ringtoneTimerTask, 30000);
            isRingTimerActive = true;
        }

    }

    public void update_db(int id, int zero, Context context){
        int alarm_on_off = zero;
        ContentValues values = new ContentValues();
        values.put(AlarmContract.AlarmEntry.ALARM_ACTIVE, alarm_on_off);
        Uri currentPetUri = ContentUris.withAppendedId(AlarmContract.AlarmEntry.CONTENT_URI ,id);
        context.getContentResolver().update(currentPetUri , values, null, null);
    }

    /**
     * will play only tts
     * @param alarm
     * @param context
     */
    public void mediaPlayTts(AlarmConstraints alarm , Context context){
    try {
        tts = new TextToSpeech(context.getApplicationContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if (status == TextToSpeech.SUCCESS) {
                    int result = tts.setLanguage(Locale.US);
                    if (result == TextToSpeech.LANG_MISSING_DATA ||
                            result == TextToSpeech.LANG_NOT_SUPPORTED) {
                        Toast.makeText(context.getApplicationContext(), "tts language not supported",
                                Toast.LENGTH_SHORT).show();
                        Log.i("tts language", "tts language not supported ");
                    }
                } else {
                    Toast.makeText(context.getApplicationContext(), "tts  initialization failed",
                            Toast.LENGTH_SHORT).show();
                    Log.i("tts initialization", "tts  initialization failed");
                }

            }
        });

        TimerTask ttsTimerTask = new TimerTask() {
            @Override
            public void run() {
                stop_tts();
                alarm.cancelAlarm(context.getApplicationContext(), alarm);

                if (alarm.isSnooze_active())
                    alarm.scheduleSnoozeAlarm(context.getApplicationContext(), alarm);

                if (!alarm.isSnooze_active() && !alarm.isRepeating()) {
                    update_db(alarm.getPKeyDB(), 0, context.getApplicationContext());
                }
                ScheduleService.updateAlarmSchedule(context.getApplicationContext());
                Intent dataChangeIntent = new Intent("com.example.smartalarm.dataChangeListener");
                context.getApplicationContext().sendBroadcast(dataChangeIntent);
            }
        };
        ttsTimer = new Timer();
        ttsTimer.schedule(ttsTimerTask, 180000);
        isTtsTimerActive = true;
        final Handler handler = new Handler();
        Runnable runnable = new Runnable() {
            @Override
            public void run() {

                tts.setOnUtteranceProgressListener(new UtteranceProgressListener() {


                    @Override
                    public void onStart(String s) {

                    }

                    @Override
                    public void onDone(String s) {
                        try {
                            Thread.sleep(3000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        tts.speak(alarm.getTtsString(), TextToSpeech.QUEUE_FLUSH, params);
                    }

                    @Override
                    public void onError(String s) {

                    }
                });
                params = new HashMap<>();
                params.put(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, "stringId");
                tts.speak(alarm.getTtsString(), TextToSpeech.QUEUE_FLUSH, params);
                Log.i(TAG, "run: tts string is " + alarm.getTtsString());
            }
        };

        handler.postDelayed(runnable, 1000);
      }catch (Exception e){
        e.printStackTrace();
    }

    }

    /**
     * will play ringtone and tts
     * @param alarm
     * @param context
     * @throws IOException
     */
    public void playRingtoneTts(AlarmConstraints alarm , Context context) throws IOException {

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

        ringM = Uri.parse(alarm.getRingtoneUri());
        /**
         * call ringtone player
         */
        playRingWithTts(context , ringM);

        TimerTask ttsRingTimerTask = new TimerTask() {
            @Override
            public void run() {
                stopRingtone();
                stop_tts();

                alarm.cancelAlarm(context.getApplicationContext() , alarm);

                if (alarm.isSnooze_active())
                    alarm.scheduleSnoozeAlarm(context.getApplicationContext() , alarm);

                if (!alarm.isSnooze_active() && !alarm.isRepeating()){
                    update_db(alarm.getPKeyDB(),0 , context.getApplicationContext());
                }
                ScheduleService.updateAlarmSchedule(context.getApplicationContext());
                Intent dataChangeIntent = new Intent("com.example.smartalarm.dataChangeListener");
                context.getApplicationContext().sendBroadcast(dataChangeIntent);
            }
        };
        ttsRingTimer = new Timer();
        ttsRingTimer.schedule(ttsRingTimerTask, 180000);
        isTtsRingTimerActive = true;

        final Handler handl = new Handler();
        Runnable runnabl = new Runnable() {
            @Override
            public void run() {
                tts.setOnUtteranceProgressListener(new UtteranceProgressListener() {


                    @Override
                    public void onStart(String s) {
                        ringtonePlay.pause();
                    }

                    @Override
                    public void onDone(String s) {
                        try {
                            Thread.sleep(1000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        ringtonePlay.start();
                        try {
                            Thread.sleep(8000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        tts.speak(alarm.getTtsString(), TextToSpeech.QUEUE_FLUSH, params);

                    }

                    @Override
                    public void onError(String s) {

                    }
                });
                params = new HashMap<>();
                params.put(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, "stringId");
                tts.speak(alarm.getTtsString(), TextToSpeech.QUEUE_FLUSH, params);
            }
        };

        handl.postDelayed(runnabl , 1000);

    }

    /**
     * this will play ringtone for ringtoneTts
     * @param context
     * @param ring
     * @throws IOException
     */
    public void playRingWithTts(Context context, Uri ring) throws IOException {
        ringtonePlay = new MediaPlayer();
        ringtonePlay.setDataSource(context.getApplicationContext(), ring);
        audioManager = (AudioManager)
                context.getApplicationContext().getSystemService(Context.AUDIO_SERVICE);
        if (audioManager.getStreamVolume(AudioManager.STREAM_RING) != 0) {
            ringtonePlay.setAudioStreamType(AudioManager.STREAM_RING);
            ringtonePlay.setLooping(true);
            ringtonePlay.prepare();
            ringtonePlay.start();
        }
    }




    public void vibrate(Context context , AlarmConstraints alarm) {

        vibrator = (Vibrator) context.getSystemService(context.VIBRATOR_SERVICE);
        long[] pattern = {0, 20000, 1000, 20000, 2000};
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                vibrator.vibrate(VibrationEffect.createWaveform(pattern, 0),
                        new AudioAttributes.Builder()
                                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                                .setUsage(AudioAttributes.USAGE_ALARM)
                                .build());
                Log.i(TAG, "vibrate: success");
            } else {
                vibrator.vibrate(pattern, 0);
                Log.i(TAG, "vibrate: success");
            }
        }catch (Exception e){
            e.printStackTrace();
            Log.i(TAG, "vibrate: failed");
        }
        TimerTask vibrateTimerTask = new TimerTask() {
            @Override
            public void run() {
               stopVibrate();
                if (alarm.getToggleOnOff()) {
                    alarm.cancelAlarm(context.getApplicationContext(), alarm);

                    if (alarm.isSnooze_active())
                        alarm.scheduleSnoozeAlarm(context.getApplicationContext(), alarm);

                    if (!alarm.isSnooze_active() && !alarm.isRepeating()) {
                        update_db(alarm.getPKeyDB(), 0, context.getApplicationContext());
                    }
                    ScheduleService.updateAlarmSchedule(context.getApplicationContext());
                    Intent dataChangeIntent = new Intent("com.example.smartalarm.dataChangeListener");
                    context.getApplicationContext().sendBroadcast(dataChangeIntent);
                }
            }
        };

        vibrateTimer = new Timer();
        vibrateTimer.schedule(vibrateTimerTask, 30000);
        isVibrateActive = true;

        
    }

    /**
     * will stop ringtone
     */
    public void stopRingtone() {
        try {
            if (ringtonePlay != null) {
                ringtonePlay.stop();
                ringtonePlay.reset();
                ringtonePlay.release();
            }
            if (isRingTimerActive) {
                ringTimer.cancel();
                Log.i(TAG, "stopRingtone: ringtimer canceled");
            }

        }catch (Exception e){
            e.printStackTrace();
        }

    }

    /**will stop the tts **/
    public void stop_tts() {
     try {
            if (tts.isSpeaking())
                tts.stop();
            tts.shutdown();
            if (isTtsTimerActive)
            ttsTimer.cancel();
            if (isTtsRingTimerActive)
            ttsRingTimer.cancel();
            Log.i(TAG, "stop_tts: success");
        }catch (Exception e){
            e.printStackTrace();
            Log.i(TAG, "stop_tts: failed");
        }
    }

    /**
     * will stop vibrate
     */
    public void stopVibrate(){
      try {
        vibrator.cancel();
        Log.i(TAG, "stopVibrate: success");
    }catch (Exception e){
        Log.i(TAG, "stopVibrate: failed");
    }

    }
}
