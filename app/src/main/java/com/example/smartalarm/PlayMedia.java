package com.example.smartalarm;

import android.content.Context;
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

import java.io.IOException;
import java.util.HashMap;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

public class PlayMedia {
    private static PlayMedia Instance;
    private static Vibrator vibrator;
    TextToSpeech tts;
    MediaPlayer ringtonePlay;
    AudioManager audioManager;
    Uri ringM;
    HashMap<String, String> params;
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
     * @throws IOException
     */
    public void mediaPlayRingtone(Context context, Uri ring) throws IOException {
        ringtonePlay = new MediaPlayer();
        ringtonePlay.setDataSource(context.getApplicationContext(), ring);
        audioManager = (AudioManager)
                context.getApplicationContext().getSystemService(Context.AUDIO_SERVICE);
        if (audioManager.getStreamVolume(AudioManager.STREAM_RING) != 0) {
            ringtonePlay.setAudioStreamType(AudioManager.STREAM_RING);
            ringtonePlay.setLooping(true);
            ringtonePlay.prepare();
            ringtonePlay.start();
            TimerTask ringtoneTimer = new TimerTask() {
                @Override
                public void run() {
                    stopRingtone();
                }
            };
            Timer timer = new Timer();
            timer.schedule(ringtoneTimer, 180000);
        }

    }

    /**
     * will play only tts
     * @param alarm
     * @param context
     */
    public void mediaPlayTts(AlarmConstraints alarm , Context context){

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

        TimerTask ttsTimer = new TimerTask() {
            @Override
            public void run() {
                stop_tts();
            }
        };
        Timer timer = new Timer();
        timer.schedule(ttsTimer, 180000);
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
            }
        };

        handler.postDelayed(runnable , 1000);

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

        TimerTask ttsRingTimer = new TimerTask() {
            @Override
            public void run() {
                stopRingtone();
                stop_tts();
            }
        };
        Timer timerr = new Timer();
        timerr.schedule(ttsRingTimer, 180000);

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



    @RequiresApi(api = Build.VERSION_CODES.O)
    public static void vibrate(Context context) {
        vibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
        vibrator.vibrate(VibrationEffect.createOneShot(200,VibrationEffect.DEFAULT_AMPLITUDE));
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
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    /**
     * will stop vibrate
     */
    public void stopVibrate(){
        vibrator.cancel();
    }
}
