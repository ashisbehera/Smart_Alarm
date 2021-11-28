package com.example.smartalarm;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;

import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

public class PlayMedia {
    private static PlayMedia Instance;
    MediaPlayer ringtonePlay;
    AudioManager audioManager;

    static PlayMedia getMediaPlayerInstance() {
        if (Instance == null) {
            return Instance = new PlayMedia();
        }
        return Instance;
    }

    public void playRingtone(Context context, Uri ring) throws IOException {
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
                    stopringtone();
                }
            };
            Timer timer = new Timer();
            timer.schedule(ringtoneTimer, 300000);
        }

    }

    public void stopringtone() {
        if (ringtonePlay != null) {
            ringtonePlay.stop();
        }
        ringtonePlay.reset();
        ringtonePlay.release();
    }
}
