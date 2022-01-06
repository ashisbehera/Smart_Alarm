package com.coffeecoders.smartalarm;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;

import android.os.Build;
import android.os.Bundle;
import android.os.Handler;

import android.os.VibrationEffect;
import android.os.Vibrator;
import android.speech.tts.TextToSpeech;
import android.speech.tts.UtteranceProgressListener;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.Toast;


import com.coffeecoders.smartalarm.data.AlarmContract;
import com.coffeecoders.smartalarm.receiver.BootUpReceiver;
import com.coffeecoders.smartalarm.receiver.CancelAlarmReceiver;
import com.coffeecoders.smartalarm.receiver.SnoozeReceiver;

import java.io.IOException;
import java.util.HashMap;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;


public class PlayMedia{
    private static final String TAG = "PlayMedia";
    private static PlayMedia Instance;
    Vibrator vibrator;
    TextToSpeech tts;
    MediaPlayer ringtonePlay;
    AudioManager audioManager;
    Uri ringM;
    HashMap<String, String> params;
    Timer ringTimer,ttsTimer,ttsRingTimer, vibrateTimer;
    Handler ttsHandler , ringTtsHandler;
    Runnable ttsRunnable , ringTtsRunnable;
    boolean isRingTimerActive , isTtsTimerActive , isTtsRingTimerActive , isVibrateTimerActive
              , isTtsHandlerActive , isRingTtsHandlerActive;
    TelephonyManager telephonyManager;
    Bundle paramsBundle;
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
    public void mediaPlayRingtone(Context context, Uri ring,
                                  AlarmConstraints alarm) throws IOException {
        ringtonePlay = new MediaPlayer();
        ringtonePlay.setDataSource(context.getApplicationContext(), ring);
        audioManager = (AudioManager)
                context.getApplicationContext().getSystemService(Context.AUDIO_SERVICE);

        telephonyManager = (TelephonyManager)
                context.getSystemService(Context.TELEPHONY_SERVICE);

            Log.i(TAG, "mediaPlayRingtone: android version is "+ Build.VERSION.SDK_INT);
            PhoneStateListener phoneStateListener = new PhoneStateListener(){
                @Override
                public void onCallStateChanged(int state, String phoneNumber) {
                    switch (state) {
                        case TelephonyManager.CALL_STATE_RINGING:
                            Log.i(TAG, "onCallStateChanged: call ringing");
                            try {
                                alarm.setTemp_snooze_active(true);
                                alarm.cancelAlarm(context , alarm);
                                alarm.scheduleSnoozeAlarm(context , alarm);
                                stopRingtone();
                                intentSender(context);
                                removeNotification(context);
                            } catch (IllegalStateException e) {
                                e.printStackTrace();
                            }
                            break;
                        case TelephonyManager.CALL_STATE_OFFHOOK:
                            Log.i(TAG, "onCallStateChanged: on call");
                            try{
                                alarm.setTemp_snooze_active(true);
                                alarm.cancelAlarm(context , alarm);
                                alarm.scheduleSnoozeAlarm(context , alarm);
                                stopRingtone();
                                intentSender(context);
                                removeNotification(context);
                            }catch (Exception e){
                                e.printStackTrace();
                            }
                            break;
                        case TelephonyManager.CALL_STATE_IDLE:
                            Log.i(TAG, "onCallStateChanged: no call");
                            try {
                                if (!alarm.isPlayed){
                                    alarm.isPlayed = true;
                                    if (audioManager.getStreamVolume(AudioManager.STREAM_ALARM) != 0) {
                                        playRing();
                                        TimerTask ringtoneTimerTask = new TimerTask() {
                                            @Override
                                            public void run() {
                                                stopRingtone();
                                                manageAlarmState(context , alarm);
                                                intentSender(context);
                                                refreshAlarm(context);
                                                removeNotification(context);
                                            }
                                        };
                                        ringTimer = new Timer();
                                        ringTimer.schedule(ringtoneTimerTask, 120000);
                                        isRingTimerActive = true;
                                    }
                                }else
                                    Log.e(TAG, "onCallStateChanged: already played" );

                            }
                            catch (Exception e) {
                                e.printStackTrace();
                            }
                            break;
                    }
                    super.onCallStateChanged(state, phoneNumber);
                }
            };
            if(telephonyManager!=null)
                telephonyManager.listen(phoneStateListener, PhoneStateListener.LISTEN_CALL_STATE);


    } /** end of MedeaPlayRingtone **/

    public void update_db(int id, int zero, Context context){
        int alarm_on_off = zero;
        ContentValues values = new ContentValues();
        values.put(AlarmContract.AlarmEntry.ALARM_ACTIVE, alarm_on_off);
        Uri currentPetUri = ContentUris.withAppendedId(AlarmContract.AlarmEntry.CONTENT_URI ,id);
        context.getContentResolver().update(currentPetUri , values, null, null);
    }

    private void refreshAlarm(Context context){
        Intent refreshAlarmIntent = new Intent(context , BootUpReceiver.class);
        refreshAlarmIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        refreshAlarmIntent.setAction("smart alarm refresh alarm");
        PendingIntent refreshAlarmPendingIntent = PendingIntent.getBroadcast(context , 0,
                refreshAlarmIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        try {
            refreshAlarmPendingIntent.send();
            Log.e(TAG, "intentSender: sent" );
        }catch (Exception e){
            e.printStackTrace();
            Log.e(TAG, "intentSender: failed");
        }
    }
    private void intentSender(Context context) {
        Intent dataChangeIntent = new Intent("com.example.smartalarm.dataChangeListener");
        context.sendBroadcast(dataChangeIntent);
        Log.e(TAG, "intentSender: intent sent" );
        Intent cancelAlarmActivityIntent = new Intent("com.example.smartalarm.cancelAlarm");
        context.sendBroadcast(cancelAlarmActivityIntent);
        Log.e(TAG, "intentSender: intent sent" );
    }

    private void manageAlarmState(Context context , AlarmConstraints alarm){

        alarm.cancelAlarm(context, alarm);

        if (alarm.isSnooze_active()) {
            alarm.scheduleSnoozeAlarm(context, alarm);
            Log.e(TAG, "manageAlarmState: alarm snoozed" );
        }

        if (!alarm.isSnooze_active() && !alarm.isRepeating()){
            update_db(alarm.getPKeyDB(),0 , context);
            Log.e(TAG, "manageAlarmState: database changed" );
        }
    }


    private void removeNotification(Context context){
        NotificationManager notificationManager = (NotificationManager)
                context.getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
        if (notificationManager != null) notificationManager.cancelAll();
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
            telephonyManager = (TelephonyManager)
                    context.getSystemService(Context.TELEPHONY_SERVICE);

            Log.i(TAG, "mediaPlayTts: "+Build.VERSION.SDK_INT);
                PhoneStateListener phoneStateListener = new PhoneStateListener(){
                    @Override
                    public void onCallStateChanged(int state, String phoneNumber) {
                        switch (state) {
                            case TelephonyManager.CALL_STATE_RINGING:
                                Log.i(TAG, "onCallStateChanged: call ringing");
                                try {
                                    alarm.setTemp_snooze_active(true);
                                    alarm.cancelAlarm(context , alarm);
                                    alarm.scheduleSnoozeAlarm(context , alarm);
                                    stop_tts();
                                    intentSender(context);
                                    removeNotification(context);
                                } catch (IllegalStateException e) {
                                    e.printStackTrace();
                                }
                                break;
                            case TelephonyManager.CALL_STATE_OFFHOOK:
                                Log.i(TAG, "onCallStateChanged: on call");
                                try {
                                    alarm.setTemp_snooze_active(true);
                                    alarm.cancelAlarm(context , alarm);
                                    alarm.scheduleSnoozeAlarm(context , alarm);
                                    stop_tts();
                                    intentSender(context);
                                    removeNotification(context);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                                break;
                            case TelephonyManager.CALL_STATE_IDLE:
                                Log.e(TAG, "onCallStateChanged: no call");
                                try {
                                    if (!alarm.isPlayed){
                                        alarm.isPlayed = true;
                                        Log.e(TAG, "onCallStateChanged: inside playtts" );

                                        ttsHandler = new Handler();
                                        ttsRunnable = new Runnable() {
                                            @Override
                                            public void run() {

                                                tts.setOnUtteranceProgressListener(new UtteranceProgressListener() {


                                                    @Override
                                                    public void onStart(String s) {

                                                    }

                                                    @Override
                                                    public void onDone(String s) {
                                                        tts.stop();
                                                        try {
                                                            Thread.sleep(2000);
                                                        } catch (InterruptedException e) {
                                                            e.printStackTrace();
                                                        }
                                                        if (Build.VERSION.SDK_INT >= 21) {
                                                            tts.speak(alarm.getTtsString(),TextToSpeech.QUEUE_FLUSH,paramsBundle,"TtsId");
                                                        } else {
                                                            tts.speak(alarm.getTtsString(), TextToSpeech.QUEUE_FLUSH, params);
                                                        }

                                                    }

                                                    @Override
                                                    public void onError(String s) {

                                                    }
                                                });
                                                params = new HashMap<>();
                                                paramsBundle = new Bundle();
                                                Log.e(TAG, "onCallStateChanged: bundle set" );
                                                paramsBundle.putString(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID , "stringId");
                                                params.put(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, "stringId");
                                                if (Build.VERSION.SDK_INT >= 21) {
                                                    tts.speak(alarm.getTtsString(),TextToSpeech.QUEUE_FLUSH,paramsBundle,"TtsId");
                                                    Log.e(TAG, "onCallStateChanged: played tts" );
                                                } else {
                                                    tts.speak(alarm.getTtsString(), TextToSpeech.QUEUE_FLUSH, params);
                                                    Log.e(TAG, "onCallStateChanged: played tts" );
                                                }
                                                Log.i(TAG, "run: tts string is " + alarm.getTtsString());
                                            }
                                        };

                                        ttsHandler.postDelayed(ttsRunnable, 1000);
                                        isTtsHandlerActive = true;
                                        TimerTask ttsTimerTask = new TimerTask() {
                                            @Override
                                            public void run() {
                                                Log.e(TAG, "ttsTimerTask:  inside ttsTimerTask" );
                                                ttsHandler.removeCallbacks(ttsRunnable);
                                                stop_tts();
                                                manageAlarmState(context , alarm);
                                                intentSender(context);
                                                refreshAlarm(context);
                                                removeNotification(context);

                                            }
                                        };
                                        ttsTimer = new Timer();
                                        ttsTimer.schedule(ttsTimerTask, 120000);
                                        isTtsTimerActive = true;
                                    }else
                                        Log.e(TAG, "onCallStateChanged: already played" );
                                }catch (Exception e){
                                    e.printStackTrace();
                                }


                                break;
                        }

                        super.onCallStateChanged(state, phoneNumber);
                    }
                };

                if(telephonyManager!=null)
                    telephonyManager.listen(phoneStateListener, PhoneStateListener.LISTEN_CALL_STATE);


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

        try{
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

        ringM = Uri.parse(alarm.getRingtoneUri());
            ringtonePlay = new MediaPlayer();
            ringtonePlay.setDataSource(context.getApplicationContext(), ringM);
            audioManager = (AudioManager)
                    context.getApplicationContext().getSystemService(Context.AUDIO_SERVICE);
        telephonyManager = (TelephonyManager)
                context.getSystemService(Context.TELEPHONY_SERVICE);

            Log.i(TAG, "mediaPlayRingtone: android version is "+ Build.VERSION.SDK_INT);
            PhoneStateListener phoneStateListener = new PhoneStateListener() {
                @Override
                public void onCallStateChanged(int state, String phoneNumber) {
                    switch (state) {
                        case TelephonyManager.CALL_STATE_RINGING:
                            Log.i(TAG, "onCallStateChanged: call ringing");
                            try {
                                alarm.setTemp_snooze_active(true);
                                alarm.cancelAlarm(context , alarm);
                                alarm.scheduleSnoozeAlarm(context , alarm);
                                stop_tts();
                                stopRingtone();
                                intentSender(context);
                                removeNotification(context);
                            } catch (IllegalStateException e) {
                                e.printStackTrace();
                            }
                            break;
                        case TelephonyManager.CALL_STATE_OFFHOOK:
                            Log.i(TAG, "onCallStateChanged: on call");
                            try {
                                alarm.setTemp_snooze_active(true);
                                alarm.cancelAlarm(context , alarm);
                                alarm.scheduleSnoozeAlarm(context , alarm);
                                stop_tts();
                                stopRingtone();
                                intentSender(context);
                                removeNotification(context);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            break;
                        case TelephonyManager.CALL_STATE_IDLE:
                            Log.i(TAG, "onCallStateChanged: ringTts playing");
                            /**
                             * call ringtone player
                             */
                            try {
                                if (!alarm.isPlayed){
                                    alarm.isPlayed = true;
                                    if (audioManager.getStreamVolume(AudioManager.STREAM_ALARM) != 0){
                                        playRing();

                                        ringTtsHandler= new Handler();
                                        ringTtsRunnable = new Runnable() {
                                            @Override
                                            public void run() {
                                                tts.setOnUtteranceProgressListener(new UtteranceProgressListener() {


                                                    @Override
                                                    public void onStart(String s) {
                                                        ringtonePlay.pause();
                                                    }

                                                    @Override
                                                    public void onDone(String s) {
                                                        tts.stop();
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
                                                        if (Build.VERSION.SDK_INT >= 21) {
                                                            tts.speak(alarm.getTtsString(),TextToSpeech.QUEUE_FLUSH,paramsBundle,"TtsId");
                                                        } else {
                                                            tts.speak(alarm.getTtsString(), TextToSpeech.QUEUE_FLUSH, params);
                                                        }

                                                    }

                                                    @Override
                                                    public void onError(String s) {

                                                    }
                                                });
                                                params = new HashMap<>();
                                                paramsBundle = new Bundle();
                                                paramsBundle.putString(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID , "stringId");
                                                params.put(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, "stringId");
                                                if (Build.VERSION.SDK_INT >= 21) {
                                                    Log.e(TAG, "run: sdk version is " + Build.VERSION.SDK_INT );
                                                    tts.speak(alarm.getTtsString(),TextToSpeech.QUEUE_FLUSH,paramsBundle,"TtsId");
                                                } else {
                                                    tts.speak(alarm.getTtsString(), TextToSpeech.QUEUE_FLUSH, params);
                                                }
                                            }
                                        };

                                        ringTtsHandler.postDelayed(ringTtsRunnable, 1000);
                                        isRingTtsHandlerActive = true;
                                        TimerTask ttsRingTimerTask = new TimerTask() {
                                            @Override
                                            public void run() {
                                                ringTtsHandler.removeCallbacks(ringTtsRunnable);
                                                stopRingtone();
                                                stop_tts();
                                                manageAlarmState(context , alarm);
                                                intentSender(context);
                                                refreshAlarm(context);
                                                removeNotification(context);
                                            }
                                        };
                                        ttsRingTimer = new Timer();
                                        ttsRingTimer.schedule(ttsRingTimerTask, 120000);
                                        isTtsRingTimerActive = true;
                                    }

                                }else
                                    Log.e(TAG, "onCallStateChanged: already played");

                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            break;
                    }


                    super.onCallStateChanged(state, phoneNumber);
                }
            };

            if(telephonyManager!=null)
                telephonyManager.listen(phoneStateListener, PhoneStateListener.LISTEN_CALL_STATE);


       }catch (Exception e){
            e.printStackTrace();
        }
    }

    /**
     * this will play ringtone for ringtoneTts
     * @throws IOException
     */
    public void playRing() {
        try {
            ringtonePlay.setAudioAttributes(
                    new AudioAttributes
                            .Builder()
                            .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                            .setUsage(AudioAttributes.USAGE_ALARM)
                            .build());
            ringtonePlay.setLooping(true);
            ringtonePlay.prepare();
            ringtonePlay.start();
        }catch (Exception e){
            e.printStackTrace();
        }

    }




    public void vibrate(Context context , AlarmConstraints alarm) {

        vibrator = (Vibrator) context.getSystemService(context.VIBRATOR_SERVICE);

        telephonyManager = (TelephonyManager)
                context.getSystemService(Context.TELEPHONY_SERVICE);

        Log.i(TAG, "mediaPlayRingtone: android version is "+ Build.VERSION.SDK_INT);
            PhoneStateListener phoneStateListener = new PhoneStateListener() {
                @Override
                public void onCallStateChanged(int state, String phoneNumber) {
                    switch (state) {
                        case TelephonyManager.CALL_STATE_RINGING:
                            Log.i(TAG, "onCallStateChanged: call ringing");
                            try {
                                alarm.setTemp_snooze_active(true);
                                alarm.cancelAlarm(context , alarm);
                                alarm.scheduleSnoozeAlarm(context , alarm);
                                stopVibrate();
                                intentSender(context);
                                removeNotification(context);
                            } catch (IllegalStateException e) {
                                e.printStackTrace();
                            }
                            break;
                        case TelephonyManager.CALL_STATE_OFFHOOK:
                            Log.i(TAG, "onCallStateChanged: on call");
                            try {
                                alarm.setTemp_snooze_active(true);
                                alarm.cancelAlarm(context , alarm);
                                alarm.scheduleSnoozeAlarm(context , alarm);
                                stopVibrate();
                                intentSender(context);
                                removeNotification(context);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            break;
                        case TelephonyManager.CALL_STATE_IDLE:

                            long[] pattern = {0, 20000, 1000, 20000, 2000};
                            try {

                                if (!alarm.isVibrated){
                                        alarm.isVibrated = true;

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

                                        TimerTask vibrateTimerTask = new TimerTask() {
                                            @Override
                                            public void run() {
                                                stopVibrate();
                                                manageAlarmState(context , alarm);
                                                intentSender(context);
                                                refreshAlarm(context);
                                                removeNotification(context);

                                            }
                                        };

                                        vibrateTimer = new Timer();
                                        vibrateTimer.schedule(vibrateTimerTask, 120000);
                                        isVibrateTimerActive = true;


                                }else
                                    Log.e(TAG, "onCallStateChanged: already played");

                            }catch (Exception e){
                                e.printStackTrace();
                                Log.i(TAG, "vibrate: failed");
                            }


                            break;
                    }

                    super.onCallStateChanged(state, phoneNumber);
                }
            };
            if(telephonyManager!=null)
                telephonyManager.listen(phoneStateListener, PhoneStateListener.LISTEN_CALL_STATE);


    }

    /**
     * will stop ringtone
     */
    public void stopRingtone() {
        try {
            if (ringtonePlay != null) {
                ringtonePlay.stop();
                Log.i(TAG, "stopRingtone: stopped");
                ringtonePlay.reset();
                Log.i(TAG, "stopRingtone: reset");
                ringtonePlay.release();
                Log.i(TAG, "stopRingtone: release");
            }
            if (isRingTimerActive) {
                ringTimer.cancel();
                Log.i(TAG, "stopRingtone: ringtimer canceled");
            }

            if (isRingTtsHandlerActive){
                ringTtsHandler.removeCallbacks(ringTtsRunnable);
                isRingTtsHandlerActive = false;
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
        }catch (Exception e) {
        }
        try {
            if (isTtsTimerActive)
                ttsTimer.cancel();
            if (isTtsRingTimerActive)
                ttsRingTimer.cancel();
            Log.i(TAG, "stop_tts: success");
            if (isTtsHandlerActive){
                ttsHandler.removeCallbacks(ttsRunnable);
                isTtsHandlerActive = false;
            }
            if (isRingTtsHandlerActive){
                ringTtsHandler.removeCallbacks(ringTtsRunnable);
                isRingTtsHandlerActive = false;
            }
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

            if (isVibrateTimerActive) {
                vibrateTimer.cancel();
                Log.i(TAG, "stopRingtone: vibrateTimer canceled");
            }
        }catch (Exception e){
            Log.i(TAG, "stopVibrate: failed");
        }

    }


}
