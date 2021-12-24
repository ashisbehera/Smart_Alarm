package com.example.smartalarm;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.os.SystemClock;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Chronometer;
import android.widget.ImageView;
import android.widget.TextView;

import com.airbnb.lottie.LottieAnimationView;

import java.util.Locale;


public class StopWatch_Fragment extends Fragment {
    private boolean running;
    private long tMilliSec, tStart, tBuff, tUpdate = 0L;
    private int secs, mins, milliSecs;
    private ImageView startPause;
    private TextView millis, seconds;
    private LottieAnimationView lottieAnimationView;
    private Handler handler;
    private Chronometer chronometer;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_stop_watch, container, false);
        getActivity().setTitle("Stop Watch");
        lottieAnimationView = view.findViewById(R.id.animationView);
        chronometer = view.findViewById(R.id.chronometer);
        startPause = view.findViewById(R.id.startPause);
        seconds = view.findViewById(R.id.seconds);
        millis = view.findViewById(R.id.millis);
        ImageView reset = view.findViewById(R.id.reset);
        handler = new Handler();
        lottieAnimationView.pauseAnimation();
        startPause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startPauseStopwatch(view);
            }
        });
        reset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                resetStopwatch(view);
            }
        });
       return view;
    }


    // thread to calculate the stopwatch mins, secs and milliseconds
    Runnable runnable = new Runnable() {
        @Override
        public void run() {
            tMilliSec = SystemClock.uptimeMillis() - tStart;
            tUpdate = tBuff + tMilliSec;
            secs = (int) (tUpdate / 1000);
            mins = secs / 60;
            secs %= 60;
            milliSecs = (int) (tUpdate % 100);
            String milliSecsStr = String.format(Locale.ENGLISH, "%02d", milliSecs);
            String minsStr = String.format(Locale.ENGLISH, "%02d", mins);
            String secsStr = String.format(Locale.ENGLISH, "%02d", secs);
            chronometer.setText(minsStr);
            seconds.setText(secsStr);
            millis.setText(milliSecsStr);
            handler.postDelayed(this, 60);
        }
    };

    // start or pause stopwatch
    public void startPauseStopwatch(View view) {
        if (!running) {
            startPause.setImageResource(R.drawable.outline_pause_circle_filled_24);
            tStart = SystemClock.uptimeMillis();
            handler.postDelayed(runnable, 0);
            chronometer.start();
            lottieAnimationView.resumeAnimation();
            running = true;
        } else {
            startPause.setImageResource(R.drawable.outline_play_circle_filled_24);
            tBuff += tMilliSec;
            handler.removeCallbacks(runnable);
            chronometer.stop();
            lottieAnimationView.pauseAnimation();
            running = false;
        }
    }

    // reset stopwatch
    public void resetStopwatch(View view) {
        if (running) {
            startPause.setImageResource(R.drawable.outline_play_circle_filled_24);
            tBuff += tMilliSec;
            handler.removeCallbacks(runnable);
            chronometer.stop();
            running = false;
        }
        lottieAnimationView.playAnimation();
        lottieAnimationView.pauseAnimation();
        startPause.setImageResource(R.drawable.outline_play_circle_filled_24);
        tMilliSec = 0L;
        tStart = 0L;
        tBuff = 0L;
        tUpdate = 0L;
        secs = 0;
        mins = 0;
        milliSecs = 0;
        String defaultMins = "00";
        String defaultSecs = "00";
        String defaultMillis = "00";
        chronometer.setText(defaultMins);
        seconds.setText(defaultSecs);
        millis.setText(defaultMillis);
        running = false;
    }
}