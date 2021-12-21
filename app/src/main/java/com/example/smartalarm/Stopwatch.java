package com.example.smartalarm;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.view.MenuItem;
import android.view.View;
import android.widget.Chronometer;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.airbnb.lottie.LottieAnimationView;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

import java.util.Locale;

public class Stopwatch extends AppCompatActivity {
    private boolean running;
    private long tMilliSec, tStart, tBuff, tUpdate = 0L;
    private int secs, mins, milliSecs;
    private ImageView startPause;
    private TextView millis, seconds;
    private LottieAnimationView lottieAnimationView;
    private Handler handler;
    private Chronometer chronometer;
    BottomNavigationView bottomNavigationView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stopwatch);
        bottomNavigationView= findViewById(R.id.bottom_nv);
        bottomNavigationView.setSelectedItemId(R.id.stopWatch_nv_bt);
        bottomNavigationView.setSelected(true);
        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()){
                    case R.id.alarm_nv_bt:
                        Intent intent1 = new Intent(Stopwatch.this, AlarmActivity.class);
                        startActivity(intent1);
                        return true;
                    case R.id.clock_nv_bt:
                        Intent intent2 = new Intent(Stopwatch.this, WorldClock.class);
                        startActivity(intent2);
                        return true;
                }
                return true;
            }
        });
        setTitle("Stop Watch");
        lottieAnimationView = findViewById(R.id.animationView);
        chronometer = findViewById(R.id.chronometer);
        startPause = findViewById(R.id.startPause);
        seconds = findViewById(R.id.seconds);
        millis = findViewById(R.id.millis);
        ImageView reset = findViewById(R.id.reset);
        handler = new Handler();
        lottieAnimationView.pauseAnimation();
        startPause.setOnClickListener(this::startPauseStopwatch);
        reset.setOnClickListener(this::resetStopwatch);
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