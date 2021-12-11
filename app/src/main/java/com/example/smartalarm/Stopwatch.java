package com.example.smartalarm;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.SystemClock;
import android.view.View;
import android.widget.Chronometer;

import com.airbnb.lottie.LottieAnimationView;

public class Stopwatch extends AppCompatActivity {
    private Chronometer chronometer;
    private boolean running;
    private long pauseOffset;
    private LottieAnimationView lottieAnimationView;
    Runnable updateSecsThread = new Runnable() {
        @Override
        public void run() {
            // timeInMillis = SystemClock
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stopwatch);
        lottieAnimationView = findViewById(R.id.animationView);
        chronometer = findViewById(R.id.chronometer);
        lottieAnimationView.pauseAnimation();
        // chronometer.setFormat("Time: %s");
        chronometer.setBase(SystemClock.elapsedRealtime());
        chronometer.setOnChronometerTickListener(chronometer -> {
            if (SystemClock.elapsedRealtime() - chronometer.getBase() >= 10000) {
                chronometer.setBase(SystemClock.elapsedRealtime());
            }
        });
    }

    // start stopwatch
    public void startChronometer(View view) {
        if (!running) {
            chronometer.setBase(SystemClock.elapsedRealtime() - pauseOffset);
            chronometer.start();
            lottieAnimationView.resumeAnimation();
            running = true;
        }
    }

    // pause stopwatch
    public void pauseChronometer(View view) {
        if (running) {
            chronometer.stop();
            lottieAnimationView.pauseAnimation();
            pauseOffset = SystemClock.elapsedRealtime() - chronometer.getBase();
            running = false;
        }
    }

    // reset stopwatch
    public void resetChronometer(View view) {
        lottieAnimationView.playAnimation();
        lottieAnimationView.pauseAnimation();
        chronometer.setBase(SystemClock.elapsedRealtime());
        pauseOffset = 0;
    }
}