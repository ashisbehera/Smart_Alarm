package com.example.smartalarm;

import android.content.Intent;
import android.os.Bundle;
import android.text.NoCopySpan;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class AddAlarm_Activity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.addalarm_activity);
        Intent intent = getIntent();
        setTitle("Add alarm");
    }
}
